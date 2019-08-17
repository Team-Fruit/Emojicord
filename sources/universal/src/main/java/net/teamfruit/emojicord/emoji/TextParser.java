package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class TextParser {
	public static final @Nonnull String placeHolder = "\u00A7s";
	public static final @Nonnull Pattern placeHolderPattern = Pattern.compile(placeHolder);

	// (§s)|(?:(?i)§[0-9A-FK-OR])|<a?\:(?:\w+?)\:([a-zA-Z0-9+/=]+?)>|\:([\w+-]+?)\:(?:\:skin-tone-(\d)\:)?
	static final @Nonnull Pattern pattern = Pattern.compile(
			"("+placeHolder+")"
					+"|(?:(?i)\u00A7[0-9A-FK-OR])"
					+"|<a?\\:(?:\\w+?)\\:([a-zA-Z0-9+/=]+?)>"
					+"|\\:([\\w+-]+?)\\:(?:\\:skin-tone-(\\d)\\:)?");
	// (?:([^ §]+?)([ §]|$))
	static final @Nonnull Pattern patternShort = Pattern.compile("(?:([^ \u00A7]+?)([ \u00A7]|$))");

	public static Pair<String, List<Pair<EmojiId, String>>> parse(String text) {
		text = encode(text);
		final StringBuffer sb = new StringBuffer();
		final List<Pair<EmojiId, String>> emojis = Lists.newArrayList();
		final Matcher matcher = TextParser.pattern.matcher(text);
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
					EmojiId emojiId = EmojiId.StandardEmojiId.fromEndpoint(g3+":skin-tone-"+g4);
					if (emojiId==null) {
						matcher.appendReplacement(sb, placeHolder+String.format(":skin-tone-%s:", g4));
						emojiId = EmojiId.StandardEmojiId.fromEndpoint(g3);
					} else
						matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(emojiId, matched));
					continue;
				} else {
					matcher.appendReplacement(sb, placeHolder);
					emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g3), matched));
					continue;
				}
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return Pair.of(text, emojis);
	}

	public static String encode(String text) {
		{
			final StringBuffer sb = new StringBuffer();
			final Matcher matcher = TextParser.pattern.matcher(text);
			while (matcher.find()) {
				final String g3 = matcher.group(3);
				if (!StringUtils.isEmpty(g3))
					if (EmojiId.StandardEmojiId.fromEndpoint(g3)==null) {
						final EmojiId id = DiscordEmojiDictionary.instance.get(g3);
						if (id instanceof EmojiId.DiscordEmojiId)
							matcher.appendReplacement(sb,
									String.format("<:%s:%s>", g3, ((EmojiId.DiscordEmojiId) id).getEncodedId()));
					}
			}
			matcher.appendTail(sb);
			text = sb.toString();
		}
		{
			final StringBuffer sb = new StringBuffer();
			final Matcher matcher = TextParser.patternShort.matcher(text);
			while (matcher.find()) {
				final String g1 = matcher.group(1);
				final String g2 = matcher.group(2);
				if (!StringUtils.isEmpty(g1))
					if (EmojiId.StandardEmojiId.EMOJI_SHORT.get().contains(g1)) {
						final EmojiId emojiId = EmojiId.StandardEmojiId.fromEndpoint(g1);
						if (emojiId!=null)
							matcher.appendReplacement(sb,
									String.format(":%s:", emojiId.getCacheName())+g2);
					}
			}
			matcher.appendTail(sb);
			text = sb.toString();
		}
		{
			final StringBuffer sb = new StringBuffer();
			final Matcher matcher = EmojiId.StandardEmojiId.EMOJI_UTF_PATTERN.get().matcher(text);
			while (matcher.find()) {
				final String g0 = matcher.group(0);
				final EmojiId emojiId = EmojiId.StandardEmojiId.fromEndpointUtf(g0);
				if (emojiId!=null)
					matcher.appendReplacement(sb,
							String.format(":%s:", emojiId.getCacheName().replace(":", "::")));
			}
			matcher.appendTail(sb);
			text = sb.toString();
		}
		return text;
	}
}
