package net.teamfruit.emojicord;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

public class EmojiCache {
	public static final long EMOJI_LIFETIME_SEC = 60;

	public static final EmojiCache instance = new EmojiCache();

	private EmojiCache() {
	}

	private final LoadingCache<EmojiId, Emoji> EMOJI_ID_MAP = CacheBuilder.newBuilder()
			.expireAfterAccess(EMOJI_LIFETIME_SEC, TimeUnit.SECONDS)
			.removalListener(
					(final RemovalNotification<EmojiId, Emoji> notification) -> notification.getValue().delete())
			.build(new CacheLoader<EmojiId, Emoji>() {
				@Override
				public Emoji load(final EmojiId key) throws Exception {
					return new Emoji(key);
				}
			});

	public Emoji getEmoji(final EmojiId name) {
		return this.EMOJI_ID_MAP.getUnchecked(name);
	}
}
