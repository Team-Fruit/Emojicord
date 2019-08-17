package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.teamfruit.emojicord.emoji.Models.EmojiGateway;
import net.teamfruit.emojicord.emoji.Models.EmojiStandard;
import net.teamfruit.emojicord.emoji.Models.EmojiStandardGroup;
import net.teamfruit.emojicord.emoji.Models.EmojiStandardList;
import net.teamfruit.emojicord.util.DataUtils;

public class Endpoint {
	public static final String EMOJI_GATEWAY = "https://raw.githubusercontent.com/Team-Fruit/Emojicord/api/api.json";
	public static EmojiGateway EMOJI_API;
	public static List<String> EMOJI_WEB_ENDPOINT;

	public static boolean loadGateway() {
		final EmojiGateway data = DataUtils.loadUrl(EMOJI_GATEWAY, EmojiGateway.class, "Emojicord API");
		if (data!=null) {
			EMOJI_API = data;
			return true;
		}
		return false;
	}

	public static void loadStandardEmojis() {
		final Map<String, EmojiId> dict = Maps.newHashMap();
		final Map<String, EmojiId> utfdict = Maps.newHashMap();
		for (final String emojiUrls : EMOJI_API.emojis) {
			final EmojiStandardList emojiList = DataUtils.loadUrl(emojiUrls, EmojiStandardList.class,
					"Standard Emojis");
			if (emojiList!=null)
				for (final EmojiStandardGroup emojiGroup : emojiList.groups)
					for (final EmojiStandard emoji : emojiGroup.emojis) {
						final EmojiId id = new EmojiId.StandardEmojiId(emojiGroup.location+emoji.location, emoji.name);
						utfdict.put(emoji.surrogates, id);
						for (final String string : emoji.strings)
							dict.put(string, id);
					}
		}
		EmojiId.StandardEmojiId.EMOJI_DICTIONARY.putAll(dict);
		EmojiId.StandardEmojiId.EMOJI_UTF_DICTIONARY.putAll(utfdict);
	}
}
