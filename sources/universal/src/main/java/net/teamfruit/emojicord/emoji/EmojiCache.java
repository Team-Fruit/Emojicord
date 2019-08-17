package net.teamfruit.emojicord.emoji;

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

	private final LoadingCache<EmojiId, EmojiObject> EMOJI_ID_MAP = CacheBuilder.newBuilder()
			.expireAfterAccess(EMOJI_LIFETIME_SEC, TimeUnit.SECONDS)
			.removalListener(
					(final RemovalNotification<EmojiId, EmojiObject> notification) -> notification.getValue().delete())
			.build(new CacheLoader<EmojiId, EmojiObject>() {
				@Override
				public EmojiObject load(final EmojiId key) throws Exception {
					return new EmojiObject(key);
				}
			});

	public EmojiObject getEmoji(final EmojiId name) {
		return this.EMOJI_ID_MAP.getUnchecked(name);
	}
}
