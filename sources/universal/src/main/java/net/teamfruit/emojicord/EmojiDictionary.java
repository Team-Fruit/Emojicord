package net.teamfruit.emojicord;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import net.teamfruit.emojicord.EmojiModel.EmojiDicord;
import net.teamfruit.emojicord.EmojiModel.EmojiDicordGroup;
import net.teamfruit.emojicord.EmojiModel.EmojiDicordIndexList;
import net.teamfruit.emojicord.EmojiModel.EmojiDicordList;

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

	public static class EmojiDictionaryLoader {
		private final EmojiDictionary dictionary;

		public EmojiDictionaryLoader(final EmojiDictionary dictionary) {
			this.dictionary = dictionary;
		}

		public void load(final File dictFile) {
			final EmojiDicordList emojiList = EmojicordData.loadFile(dictFile, EmojiDicordList.class,
					"Discord Emoji Dictionary");
			if (emojiList != null)
				for (final EmojiDicordGroup emojiGroup : emojiList.groups)
					for (final EmojiDicord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id != null)
							this.dictionary.register(emoji.name, id);
					}
		}

		public void loadAll(final File dictDir) {
			final File groupsDir = new File(dictDir, "groups");
			final File manifestFile = new File(dictDir, "indexes.json");

			final ListMultimap<String, EmojiDicordGroup> groups = Multimaps.newListMultimap(Maps.newHashMap(),
					() -> Lists.newArrayList());
			for (final File dictFile : FileUtils.listFiles(groupsDir, new String[] { "json" }, true)) {
				final EmojiDicordList emojiList = EmojicordData.loadFile(dictFile, EmojiDicordList.class,
						"Discord Emoji Dictionary");
				if (emojiList != null)
					for (final EmojiDicordGroup emojiGroup : emojiList.groups)
						groups.put(emojiGroup.id, emojiGroup);
			}

			final List<Pair<String, List<EmojiDicordGroup>>> indexed = Lists.newArrayList();
			final EmojiDicordIndexList indexes = EmojicordData.loadFile(manifestFile, EmojiDicordIndexList.class, null);
			if (indexes != null)
				for (final String index : indexes.indexes)
					if (groups.containsKey(index)) {
						final List<EmojiDicordGroup> group = groups.get(index);
						groups.removeAll(index);
						indexed.add(Pair.of(index, group));
					}
			for (final Entry<String, List<EmojiDicordGroup>> entry : Multimaps.asMap(groups).entrySet())
				indexed.add(Pair.of(entry.getKey(), entry.getValue()));

			final List<String> newindexes = indexed.stream().map(Pair::getKey).collect(Collectors.toList());
			if (indexes == null || !indexes.indexes.equals(newindexes)) {
				final EmojiDicordIndexList data = new EmojiDicordIndexList();
				data.indexes = newindexes;
				EmojicordData.saveFile(manifestFile, EmojiDicordIndexList.class, data,
						"Discord Emoji Dictionary Manifest File");
			}

			for (final Pair<String, List<EmojiDicordGroup>> emojiList : indexed)
				for (final EmojiDicordGroup emojiGroup : emojiList.getRight())
					for (final EmojiDicord emoji : emojiGroup.emojis) {
						final EmojiId id = EmojiId.DiscordEmojiId.fromDecimalId(emoji.id);
						if (id != null)
							this.dictionary.register(emoji.name, id);
					}
		}
	}
}
