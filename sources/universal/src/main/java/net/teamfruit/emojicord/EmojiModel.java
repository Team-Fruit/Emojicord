package net.teamfruit.emojicord;

import java.util.List;

public class EmojiModel {
	public static class EmojiGateway {
		public List<String> emojis;
		public List<String> api;
	}

	public static class EmojiStandard {
		public String location;
		public String name;
		public List<String> strings;
	}

	public static class EmojiStandardGroup {
		public List<EmojiStandard> emojis;
		public String location;
	}

	public static class EmojiStandardList {
		public List<EmojiStandardGroup> groups;
	}

	public static class EmojiDicord {
		public String name;
		public String id;
	}

	public static class EmojiDicordGroup {
		public List<EmojiDicord> emojis;
		public String name;
		public String id;
	}

	public static class EmojiDicordList {
		public List<EmojiDicordGroup> groups;
	}

	public static class EmojiDicordIndexList {
		public List<String> indexes;
	}
}
