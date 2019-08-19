package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

public class EmojiText {
	public static final @Nonnull Function<Integer, String> placeHolderSupplier = e -> String.format("{%d}", e);
	public static final @Nonnull Pattern placeHolderPattern = Pattern.compile("\\{(\\d+?)\\}");

	public final @Nonnull String text;
	public final @Nonnull List<Pair<EmojiId, String>> emojis;

	public EmojiText(final String text, final List<Pair<EmojiId, String>> emojis) {
		this.text = text;
		this.emojis = emojis;
	}

	public static class EmojiTextParser {
		// \{(\d+?)\}|(?:(?i)§[0-9A-FK-OR])|<a?\:(?:\w+?)\:([a-zA-Z0-9+/=]+?)>|\:([\w+-]+?)\:(?:\:skin-tone-(\d)\:)?
		static final @Nonnull Pattern pattern = Pattern.compile(
				"\\{(\\d+?)\\}"
						+"|(?:(?i)\u00A7[0-9A-FK-OR])"
						+"|<a?\\:(?:\\w+?)\\:([a-zA-Z0-9+/=]+?)>"
						+"|\\:([\\w+-]+?)\\:(?:\\:skin-tone-(\\d)\\:)?");

		public static EmojiText parse(String text, final boolean isTextField) {
			text = encode(text, isTextField);
			final StringBuffer sb = new StringBuffer();
			final List<Pair<EmojiId, String>> emojis = Lists.newArrayList();
			final Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				final String g0 = matcher.group(0);
				final String g1 = matcher.group(1);
				if (!StringUtils.isEmpty(g1)) {
					matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
					emojis.add(Pair.of(null, g0));
					continue;
				}
				final String g2 = matcher.group(2);
				if (!StringUtils.isEmpty(g2))
					if (StringUtils.length(g2)>12) {
						matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
						emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromDecimalId(g2), g0));
						continue;
					} else {
						matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
						emojis.add(Pair.of(EmojiId.DiscordEmojiId.fromEncodedId(g2), g0));
						continue;
					}
				final String g3 = matcher.group(3);
				final String g4 = matcher.group(4);
				if (!StringUtils.isEmpty(g3))
					if (!StringUtils.isEmpty(g4)) {
						EmojiId emojiId = EmojiId.StandardEmojiId.fromEndpoint(g3+":skin-tone-"+g4);
						if (emojiId==null) {
							matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
							emojiId = EmojiId.StandardEmojiId.fromEndpoint(g3);
						} else
							matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
						emojis.add(Pair.of(emojiId, g0));
						continue;
					} else {
						matcher.appendReplacement(sb, placeHolderSupplier.apply(emojis.size()));
						emojis.add(Pair.of(EmojiId.StandardEmojiId.fromEndpoint(g3), g0));
						continue;
					}
			}
			matcher.appendTail(sb);
			text = sb.toString();
			return new EmojiText(text, emojis);
		}

		public static String encode(String text, final boolean isTextField) {
			{
				final StringBuffer sb = new StringBuffer();
				final Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					final String g3 = matcher.group(3);
					if (!StringUtils.isEmpty(g3))
						if (EmojiId.StandardEmojiId.fromEndpoint(g3)==null) {
							final EmojiId id = DiscordEmojiDictionary.instance.get(g3);
							if (id instanceof EmojiId.DiscordEmojiId)
								matcher.appendReplacement(sb,
										String.format("<:%s:%s>", g3, ((EmojiId.DiscordEmojiId) id).getEncodedId())+(isTextField ? matcher.group(0) : ""));
						}
				}
				matcher.appendTail(sb);
				text = sb.toString();
			}
			{
				final StringBuffer sb = new StringBuffer();
				final Matcher matcher = EmojiId.StandardEmojiId.EMOJI_SHORT_PATTERN.get().matcher(text);
				while (matcher.find()) {
					final String g0 = matcher.group(0);
					final EmojiId emojiId = EmojiId.StandardEmojiId.fromEndpoint(g0);
					if (emojiId!=null)
						matcher.appendReplacement(sb,
								String.format(":%s:", emojiId.getCacheName())+(isTextField ? g0 : ""));
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
								String.format(":%s:", emojiId.getCacheName().replace(":", "::"))+(isTextField ? g0 : ""));
				}
				matcher.appendTail(sb);
				text = sb.toString();
			}
			return text;
		}
	}

	public static class EmojiTextCache {
		public static final long LIFETIME_SEC = 5;

		public static final EmojiTextCache instance = new EmojiTextCache();

		private EmojiTextCache() {
		}

		private final LoadingCache<Pair<String, Boolean>, EmojiText> EMOJI_TEXT_MAP = CacheBuilder.newBuilder()
				.expireAfterAccess(LIFETIME_SEC, TimeUnit.SECONDS)
				.build(new CacheLoader<Pair<String, Boolean>, EmojiText>() {
					@Override
					public EmojiText load(final Pair<String, Boolean> key) throws Exception {
						return EmojiTextParser.parse(key.getLeft(), key.getRight());
					}
				});

		public EmojiText getEmojiText(final String text, final boolean isTextField) {
			return this.EMOJI_TEXT_MAP.getUnchecked(Pair.of(text, isTextField));
		}
	}
}
