package net.teamfruit.emojicord;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import net.teamfruit.emojicord.EmojiModel.EmojiDiscord;
import net.teamfruit.emojicord.EmojiModel.EmojiDiscordGroup;
import net.teamfruit.emojicord.EmojiModel.EmojiDiscordIndex;
import net.teamfruit.emojicord.EmojiModel.EmojiDiscordIndexList;
import net.teamfruit.emojicord.EmojiModel.EmojiDiscordList;

public class EmojiDictionary {
	public static final EmojiDictionary instance = new EmojiDictionary();

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
		if (list.size() > num)
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
		private final EmojiDictionary dictionary;

		public EmojiDictionaryLoader(final EmojiDictionary dictionary) {
			this.dictionary = dictionary;
		}

		public void load(final File dictFile) {
			final EmojiDiscordList emojiList = EmojicordData.loadFile(dictFile, EmojiDiscordList.class,
					"Discord Emoji Dictionary");
			if (emojiList != null)
				for (final EmojiDiscordGroup emojiGroup : emojiList.groups)
					for (final EmojiDiscord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id != null)
							this.dictionary.register(emoji.name, id);
					}
		}

		public void loadAll(final File dictDir) {
			final File groupsDir = new File(dictDir, "mappings");
			final File manifestFile = new File(dictDir, "indexes.json");

			final ListMultimap<String, EmojiDiscordGroup> groups = Multimaps.newListMultimap(
					Maps.newHashMap(),
					() -> Lists.newArrayList());
			for (final File dictFile : FileUtils.listFiles(groupsDir, new String[] { "json" }, true)) {
				final EmojiDiscordList emojiList = EmojicordData.loadFile(dictFile, EmojiDiscordList.class,
						"Discord Emoji Dictionary");
				if (emojiList != null)
					for (final EmojiDiscordGroup emojiGroup : emojiList.groups)
						groups.put(emojiGroup.id, emojiGroup);
			}

			final List<Pair<EmojiDiscordIndex, List<EmojiDiscordGroup>>> indexed = Lists.newArrayList();
			final EmojiDiscordIndexList indexes = EmojicordData.loadFile(manifestFile, EmojiDiscordIndexList.class,
					null);
			if (indexes != null)
				for (final EmojiDiscordIndex index : indexes.indexes)
					if (groups.containsKey(index)) {
						final List<EmojiDiscordGroup> group = groups.get(index.id);
						groups.removeAll(index);
						indexed.add(Pair.of(index, group));
					}
			for (final Entry<String, List<EmojiDiscordGroup>> entry : Multimaps.asMap(groups).entrySet()) {
				final EmojiDiscordIndex index = new EmojiDiscordIndex();
				index.id = entry.getKey();
				index.name = entry.getValue().isEmpty() ? "" : entry.getValue().get(0).name;
				indexed.add(Pair.of(index, entry.getValue()));
			}

			final List<EmojiDiscordIndex> newindexes = indexed.stream().map(Pair::getKey).collect(Collectors.toList());
			if (indexes == null
					|| indexes.indexes.size() != newindexes.size()
					|| !Iterators.elementsEqual(indexes.indexes.stream().map(e -> e.id).iterator(),
							newindexes.stream().map(e -> e.id).iterator())) {
				final EmojiDiscordIndexList data = new EmojiDiscordIndexList();
				data.indexes = newindexes;
				EmojicordData.saveFile(manifestFile, EmojiDiscordIndexList.class, data,
						"Discord Emoji Dictionary Manifest File");
			}

			for (final Pair<EmojiDiscordIndex, List<EmojiDiscordGroup>> emojiList : indexed)
				for (final EmojiDiscordGroup emojiGroup : emojiList.getRight())
					for (final EmojiDiscord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id != null)
							this.dictionary.register(emoji.name, id);
					}
		}
	}
}
