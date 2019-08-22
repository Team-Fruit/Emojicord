package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import io.netty.util.internal.StringUtil;
import net.teamfruit.emojicord.compat.Compat;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextElement;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextParser;

public class EmojiContext {
	public final String text;
	public final Map<Integer, EmojiTextElement> emojis;

	public EmojiContext(final String text, final Map<Integer, EmojiTextElement> emojis) {
		this.text = text;
		this.emojis = emojis;
	}

	public static class EmojiContextLoader {
		public static EmojiContext getEmojiFormattedString(String text, final boolean isTextFieldRendering) {
			final Map<Integer, EmojiTextElement> emojiMap = Maps.newHashMap();
			if (!StringUtil.isNullOrEmpty(text)) {
				Compat.CompatMinecraft.getMinecraft().mcProfiler.startSection("emojicordParse");
				final EmojiText emojiPair = EmojiText.createParsed(text);
				text = emojiPair.text;
				final Matcher matcher = EmojiText.placeHolderPattern.matcher(text);
				final StringBuffer sb = new StringBuffer();
				final List<EmojiTextElement> emojis = emojiPair.emojis;
				while (matcher.find()) {
					final int emojiIndex = NumberUtils.toInt(matcher.group(1), -1);
					if (0<=emojiIndex&&emojiIndex<emojis.size()) {
						final EmojiTextElement entry = emojis.get(emojiIndex);
						if (entry.id==null)
							matcher.appendReplacement(sb, entry.raw);
						else {
							matcher.appendReplacement(sb, "?");
							final int index = sb.length()-"?".length();
							emojiMap.put(index, entry);
							if (isTextFieldRendering)
								sb.append(entry.raw);
						}
					}
				}
				matcher.appendTail(sb);
				text = sb.toString();
				Compat.CompatMinecraft.getMinecraft().mcProfiler.endSection();
			}
			return new EmojiContext(text, emojiMap);
		}
	}

	public static class EmojiContextCache {
		public static final long LIFETIME_SEC = 5;

		public static final EmojiContextCache instance = new EmojiContextCache();

		private EmojiContextCache() {
		}

		private final LoadingCache<String, EmojiText> EMOJI_TEXT_MAP = CacheBuilder.newBuilder()
				.expireAfterAccess(LIFETIME_SEC, TimeUnit.SECONDS)
				.build(new CacheLoader<String, EmojiText>() {
					@Override
					public EmojiText load(final String key) throws Exception {
						EmojiText emojiText = EmojiText.createUnparsed(key);
						emojiText = EmojiTextParser.escape(emojiText);
						emojiText = EmojiTextParser.encode(emojiText);
						emojiText = EmojiTextParser.parse(emojiText);
						return emojiText;
					}
				});

		public EmojiText getEmojiText(final String text) {
			return this.EMOJI_TEXT_MAP.getUnchecked(text);
		}
	}
}
