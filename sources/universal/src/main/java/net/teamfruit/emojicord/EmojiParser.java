package net.teamfruit.emojicord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class EmojiParser {
	static final @Nonnull Pattern pattern = Pattern.compile("<a?\\:(\\w+?)\\:([a-zA-Z0-9+/]+?)>|\\:(\\w+?)\\:"); // <a?\:(\w+?)\:([a-zA-Z0-9+/]+?)>|\:(\w+?)\:

	public static List<Pair<EmojiId, String>> parse(final String text) {
		final List<Pair<EmojiId, String>> emojis = new ArrayList<>();
		final Matcher matcher = EmojiParser.pattern.matcher(text);
		while (matcher.find()) {
			final String matched = matcher.group(0);
			final String g2 = matcher.group(2);
			if (!StringUtils.isEmpty(g2))
				emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromDecimalId(g2), matched));
			final String g3 = matcher.group(3);
			if (!StringUtils.isEmpty(g3))
				emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g3), matched));
		}
		return emojis;
	}
}
