package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class StandardEmojiIdPicker {
	public static StandardEmojiIdPicker instance = new StandardEmojiIdPickerBuilder().build();

	public final List<PickerGroup> categories;

	public StandardEmojiIdPicker(final List<PickerGroup> groups) {
		this.categories = groups;
	}

	public static class PickerItem {
		public final String text;
		public final String name;
		public final List<String> alias;
		public final EmojiId id;

		public PickerItem(final String text, final String name, final List<String> alias, final EmojiId id) {
			this.text = text;
			this.name = name;
			this.alias = alias;
			this.id = id;
		}

		public static @Nullable PickerItem from(final String text) {
			if (text==null)
				return null;
			final String[] split = StringUtils.split(text);
			if (split.length<=0)
				return null;
			final String name = StringUtils.strip(split[0], ":");
			final List<String> alias = Stream.of(split).map(e -> StringUtils.strip(e, ":")).collect(Collectors.toList());
			final EmojiId id = StandardEmojiIdDictionary.instance.aliasDictionary.get(name);
			if (id==null)
				return null;
			return new PickerItem(text, name, alias, id);
		}
	}

	public static class PickerGroup {
		public final String name;
		public final List<PickerItem> items;

		public PickerGroup(final String name, final List<PickerItem> items) {
			this.name = name;
			this.items = items;
		}
	}

	public static class StandardEmojiIdPickerBuilder {
		public List<PickerGroup> groups = Lists.newArrayList();

		public StandardEmojiIdPickerBuilder addGroup(final String name, final List<String> items) {
			this.groups.add(new PickerGroup(name, items.stream().map(PickerItem::from).collect(Collectors.toList())));
			return this;
		}

		public StandardEmojiIdPicker build() {
			return new StandardEmojiIdPicker(this.groups);
		}
	}
}
