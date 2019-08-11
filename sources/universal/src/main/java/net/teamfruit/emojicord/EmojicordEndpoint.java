package net.teamfruit.emojicord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.teamfruit.emojicord.EmojiModel.EmojiGateway;
import net.teamfruit.emojicord.EmojiModel.EmojiStandard;
import net.teamfruit.emojicord.EmojiModel.EmojiStandardGroup;
import net.teamfruit.emojicord.EmojiModel.EmojiStandardList;

public class EmojicordEndpoint {
	public static final String EMOJI_GATEWAY = "https://raw.githubusercontent.com/Team-Fruit/Emojicord/api/api.json";
	public static EmojiGateway EMOJI_API;
	public static List<String> EMOJI_WEB_ENDPOINT;

	public static boolean loadGateway() {
		final EmojiGateway data = EmojicordData.loadUrl(EMOJI_GATEWAY, EmojiGateway.class, "Emojicord API");
		if (data != null) {
			EMOJI_API = data;
			return true;
		}
		return false;
	}

	public static void loadStandardEmojis() {
		final Map<String, EmojiId> dict = new HashMap<>();
		for (final String emojiUrls : EMOJI_API.emojis) {
			final EmojiStandardList emojiList = EmojicordData.loadUrl(emojiUrls, EmojiStandardList.class,
					"Standard Emojis");
			if (emojiList != null)
				for (final EmojiStandardGroup emojiGroup : emojiList.groups)
					for (final EmojiStandard emoji : emojiGroup.emojis) {
						final EmojiId id = new EmojiId.StandardEmojiId(emojiGroup.location + emoji.location,
								emoji.name);
						for (final String string : emoji.strings)
							dict.put(string, id);
					}
		}
		EmojiId.StandardEmojiId.EMOJI_DICTIONARY.putAll(dict);
	}
}
