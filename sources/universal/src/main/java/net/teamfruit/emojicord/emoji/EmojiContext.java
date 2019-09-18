package net.teamfruit.emojicord.emoji;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextElement;

public class EmojiContext {
	public static final char EMOJI_REPLACE_CHARACTOR = '\u0000';

	public final String text;
	public final Map<Integer, EmojiTextElement> emojis;

	public EmojiContext(final String text, final Map<Integer, EmojiTextElement> emojis) {
		this.text = text;
		this.emojis = emojis;
	}

	public static class EmojiContextLoader {
		public static EmojiContext getEmojiFormattedString(final String text) {
			if (!StringUtils.isEmpty(text)) {
				final EmojiText emojiText = EmojiText.createParsed(text);
				final EmojiContext context = emojiText.getEmojiContext();
				return context;
			}
			return new EmojiContext("", Maps.newHashMap());
		}
	}

	public static class EmojiContextCache {
		public static final long LIFETIME_SEC = 5;

		public static final EmojiContextCache instance = new EmojiContextCache();

		private EmojiContextCache() {
		}

		private final LoadingCache<String, EmojiContext> EMOJI_TEXT_MAP = CacheBuilder.newBuilder()
				.expireAfterAccess(LIFETIME_SEC, TimeUnit.SECONDS)
				.build(new CacheLoader<String, EmojiContext>() {
					@Override
					public EmojiContext load(final String key) throws Exception {
						return EmojiContextLoader.getEmojiFormattedString(key);
					}
				});

		public @Nonnull EmojiContext getContext(final String text) {
			return this.EMOJI_TEXT_MAP.getUnchecked(text);
		}
	}
}
