package net.teamfruit.emojicord;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class EmojiParser {
	public static final @Nonnull String placeHolder = "%s";//"\u00A7s";
	public static final @Nonnull Pattern placeHolderPattern = Pattern.compile(placeHolder);

	static final @Nonnull Pattern pattern = Pattern.compile(
			"("+placeHolder+")"
					+"|(?:(?i)\u00A7[0-9A-FK-OR])"
					+"|<a?\\:(?:\\w+?)\\:([a-zA-Z0-9+/=]+?)>"
					+"|\\:([\\w+-]+?)\\:(?:\\:skin-tone-(\\d)\\:)?"
					+"|(?:([^ \u00A7]+?)([ \u00A7]|$))");

	public static Pair<String, List<Pair<EmojiId, String>>> parse(final String text) {
		final StringBuffer sb = new StringBuffer();
		final List<Pair<EmojiId, String>> emojis = Lists.newArrayList();
		final Matcher matcher = EmojiParser.pattern.matcher(text);
		while (matcher.find()) {
			final String matched = matcher.group(0).trim();
			final String g1 = matcher.group(1);
			if (!StringUtils.isEmpty(g1)) {
				matcher.appendReplacement(sb, placeHolder);
				emojis.add(Pair.of(null, matched));
				continue;
			}
			final String g2 = matcher.group(2);
			if (!StringUtils.isEmpty(g2))
				if (StringUtils.length(g2)>12) {
					matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromDecimalId(g2), matched));
					continue;
				} else {
					matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromEncodedId(g2), matched));
					continue;
				}
			final String g3 = matcher.group(3);
			final String g4 = matcher.group(4);
			if (!StringUtils.isEmpty(g3))
				if (!StringUtils.isEmpty(g4)) {
					matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g3+":skin-tone-"+g4), matched));
					continue;
				} else {
					EmojiId emoji = EmojiId.StandardEmojiId.fromEndpoint(g3);
					if (emoji==null)
						emoji = EmojiDictionary.instance.get(g3);
					matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(emoji, matched));
					continue;
				}
			final String g5 = matcher.group(5);
			final String g6 = matcher.group(6);
			if (!StringUtils.isEmpty(g5))
				if (EmojiId.StandardEmojiId.EMOJI_SHORT.get().contains(g5)) {
					matcher.appendReplacement(sb, placeHolder+g6);
					emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g5), matched));
					continue;
				}
		}
		matcher.appendTail(sb);
		return Pair.of(sb.toString(), emojis);
	}

	public static String encode(final String text) {
		final StringBuffer sb = new StringBuffer();
		final Matcher matcher = EmojiParser.pattern.matcher(text);
		while (matcher.find()) {
			final String g3 = matcher.group(3);
			if (!StringUtils.isEmpty(g3))
				if (EmojiId.StandardEmojiId.fromEndpoint(g3)==null) {
					final EmojiId id = EmojiDictionary.instance.get(g3);
					if (id instanceof EmojiId.DiscordEmojiId)
						matcher.appendReplacement(sb,
								String.format("<:%s:%s>", g3, ((EmojiId.DiscordEmojiId) id).getEncodedId()));
				}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
