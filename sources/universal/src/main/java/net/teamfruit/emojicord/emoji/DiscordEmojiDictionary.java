package net.teamfruit.emojicord.emoji;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

public class DiscordEmojiDictionary {
	public static final DiscordEmojiDictionary instance = new DiscordEmojiDictionary();

	private final ListMultimap<String, EmojiId> dictionary = Multimaps.newListMultimap(Maps.newHashMap(),
			() -> Lists.newArrayList());

	public EmojiId get(final String name) {
		final String str = StringUtils.substringBeforeLast(name, "~");
		final String numstr = StringUtils.substringAfterLast(str, "~");
		int num = 0;
		if (!StringUtils.isEmpty(numstr)) {
			if (!NumberUtils.isDigits(numstr))
				return null;
			num = NumberUtils.toInt(numstr);
		}
		final List<EmojiId> list = this.dictionary.get(str);
		if (list.size()>num)
			return list.get(num);
		return null;
	}

	public void register(final String name, final EmojiId id) {
		this.dictionary.put(name, id);
	}

	public void clear() {
		this.dictionary.clear();
	}

	public void loadAll(final File dictDir) {
		clear();
		new EmojiDictionaryLoader(this).loadAll(dictDir);
	}

	public static class EmojiDictionaryLoader {
		private final DiscordEmojiDictionary dictionary;

		public EmojiDictionaryLoader(final DiscordEmojiDictionary dictionary) {
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
