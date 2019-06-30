package net.teamfruit.emojicord;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EmojiManager {
	public static final EmojiManager instance = new EmojiManager();

	private EmojiManager() {
	}

	private final LoadingCache<EmojiId, Emoji> EMOJI_ID_MAP = CacheBuilder.newBuilder()
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
