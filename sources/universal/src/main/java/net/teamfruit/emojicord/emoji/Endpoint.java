package net.teamfruit.emojicord.emoji;

import java.util.List;

import net.teamfruit.emojicord.emoji.Models.EmojiGateway;
import net.teamfruit.emojicord.emoji.Models.EmojiStandard;
import net.teamfruit.emojicord.emoji.Models.EmojiStandardGroup;
import net.teamfruit.emojicord.emoji.Models.EmojiStandardList;
import net.teamfruit.emojicord.emoji.StandardEmojiIdDictionary.StandardEmojiIdDictionaryBuilder;
import net.teamfruit.emojicord.emoji.StandardEmojiIdDictionary.StandardEmojiIdRepository;
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
		final StandardEmojiIdDictionaryBuilder builder = new StandardEmojiIdDictionaryBuilder();
		for (final String emojiUrls : EMOJI_API.emojis) {
			final EmojiStandardList emojiList = DataUtils.loadUrl(emojiUrls, EmojiStandardList.class,
					"Standard Emojis");
			if (emojiList!=null)
				for (final EmojiStandardGroup emojiGroup : emojiList.groups)
					for (final EmojiStandard emoji : emojiGroup.emojis) {
						final EmojiId id = new EmojiId.StandardEmojiId(emojiGroup.location+emoji.location, emoji.name);
						builder.putName(emoji.name, id);
						builder.putUtf(emoji.surrogates, id);
						for (final String string : emoji.strings)
							builder.putAlias(string, id);
					}
		}
		StandardEmojiIdRepository.instance = builder.build();
	}
}
