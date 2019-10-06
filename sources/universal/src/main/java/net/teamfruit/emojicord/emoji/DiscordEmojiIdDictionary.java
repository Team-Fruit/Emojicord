package net.teamfruit.emojicord.emoji;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscord;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordGroup;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordIndexFolder;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordIndexGroup;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordIndexList;
import net.teamfruit.emojicord.emoji.Models.EmojiDiscordList;
import net.teamfruit.emojicord.util.DataUtils;

public class DiscordEmojiIdDictionary {
	public static final DiscordEmojiIdDictionary instance = new DiscordEmojiIdDictionary();

	private final ListMultimap<String, EmojiId> dictionary = Multimaps.newListMultimap(Maps.newHashMap(),
			() -> Lists.newArrayList());

	public EmojiId get(final String name) {
		final String str = StringUtils.substringBefore(name, "~"); // not substringBeforeLast
		final Optional<EmojiId> result = this.dictionary.get(str).stream().filter(e -> e.node.getUid().equals(name)).findFirst();
		return result.orElse(null);
	}

	public Map<String, EmojiId> get() {
		final Map<String, EmojiId> dict = Maps.newHashMap();
		for (final Entry<String, List<EmojiId>> entry : Multimaps.asMap(this.dictionary).entrySet()) {
			final String key = entry.getKey();
			final List<EmojiId> values = entry.getValue();
			if (!values.isEmpty())
				dict.put(key, values.get(0));
			values.stream().filter(e -> e.node.countPrev()>0).forEach(e -> {
				dict.put(e.node.getUid(), e);
			});
		}
		return dict;
	}

	public void register(final String name, final EmojiId id) {
		final List<EmojiId> list = this.dictionary.get(name);
		if (!list.isEmpty()) {
			final EmojiId last = list.get(list.size()-1);
			last.node.linkNext(id.node);
		} else {
			final EmojiId stdId = StandardEmojiIdDictionary.instance.nameDictionary.get(name);
			if (stdId!=null)
				stdId.node.linkNext(id.node);
		}
		list.add(id);
	}

	public void clear() {
		this.dictionary.clear();
	}

	public void loadAll(final File dictDir) {
		clear();
		new EmojiDictionaryLoader(this).loadAll(dictDir);
	}

	public static class EmojiDictionaryLoader {
		private final DiscordEmojiIdDictionary dictionary;

		public EmojiDictionaryLoader(final DiscordEmojiIdDictionary dictionary) {
			this.dictionary = dictionary;
		}

		public void load(final File dictFile) {
			final EmojiDiscordList emojiList = DataUtils.loadFile(dictFile, EmojiDiscordList.class,
					"Discord Emoji Dictionary");
			if (emojiList!=null)
				for (final EmojiDiscordGroup emojiGroup : emojiList.groups)
					for (final EmojiDiscord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id!=null)
							this.dictionary.register(emoji.name, id);
					}
		}

		public void loadAll(final File dictDir) {
			final File groupsDir = new File(dictDir, "mappings");
			final File manifestFile = new File(dictDir, "indexes.json");

			groupsDir.mkdirs();

			final List<EmojiDiscordList> lists = Lists.newArrayList();
			for (final File dictFile : FileUtils.listFiles(groupsDir, new String[] { "json" }, true)) {
				final EmojiDiscordList emojiList = DataUtils.loadFile(dictFile, EmojiDiscordList.class,
						"Discord Emoji Dictionary");
				if (emojiList!=null)
					lists.add(emojiList);
			}

			final EmojiDiscordIndexFolder listIndex = DataUtils.loadFile(manifestFile, EmojiDiscordIndexFolder.class, null);
			Set<Set<String>> listIndexSample = null;
			if (listIndex!=null)
				try {
					final Map<String, Integer> listMap = IntStream.range(0, listIndex.lists.size())
							.boxed()
							.collect(Collectors.toMap(e -> listIndex.lists.get(e).id, Function.identity()));
					Collections.sort(lists, (a, b) -> {
						final Integer ia = listMap.get(a.id);
						final Integer ib = listMap.get(b.id);
						if (ia==null&&ib==null)
							return 0;
						if (ia==null)
							return 1;
						if (ib==null)
							return -1;
						return ib-ia;
					});

					for (final EmojiDiscordList list : lists) {
						final List<EmojiDiscordGroup> groupIndex = list.groups;
						final Map<String, Integer> groupMap = IntStream.range(0, groupIndex.size())
								.boxed()
								.collect(Collectors.toMap(e -> groupIndex.get(e).id, Function.identity()));
						Collections.sort(groupIndex, (a, b) -> {
							final Integer ia = groupMap.get(a.id);
							final Integer ib = groupMap.get(b.id);
							if (ia==null&&ib==null)
								return 0;
							if (ia==null)
								return 1;
							if (ib==null)
								return -1;
							return ib-ia;
						});
					}

					listIndexSample = listIndex.lists.stream().map(
							e -> e.groups.stream().map(f -> f.id).collect(Collectors.toSet())).collect(Collectors.toSet());
				} catch (final NullPointerException e) {
					Log.log.error("Manifest File is corrupted. ignored : ", e);
				}

			boolean updated = false;
			if (listIndexSample==null)
				updated = true;
			else {
				final Set<Set<String>> listsSample = lists.stream().map(
						e -> e.groups.stream().map(f -> f.id).collect(Collectors.toSet())).collect(Collectors.toSet());
				if (!listsSample.equals(listIndexSample))
					updated = true;
			}
			if (updated) {
				final EmojiDiscordIndexFolder ifolder = new EmojiDiscordIndexFolder();
				ifolder.lists = lists.stream().map(e -> {
					final EmojiDiscordIndexList ilist = new EmojiDiscordIndexList();
					ilist.id = e.id;
					ilist.name = e.name;
					ilist.groups = e.groups.stream().map(f -> {
						final EmojiDiscordIndexGroup igroup = new EmojiDiscordIndexGroup();
						igroup.id = f.id;
						igroup.name = f.name;
						return igroup;
					}).collect(Collectors.toList());
					return ilist;
				}).collect(Collectors.toList());
				DataUtils.saveFile(manifestFile, EmojiDiscordIndexFolder.class, ifolder,
						"Discord Emoji Dictionary Manifest File");
			}

			for (final EmojiDiscordList emojiList : lists)
				for (final EmojiDiscordGroup emojiGroup : emojiList.groups)
					for (final EmojiDiscord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id!=null)
							this.dictionary.register(emoji.name, id);
					}
		}
	}
}
