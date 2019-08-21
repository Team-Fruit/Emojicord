package net.teamfruit.emojicord.emoji;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Maps;

import io.netty.util.internal.StringUtil;
import net.teamfruit.emojicord.compat.Compat;
import net.teamfruit.emojicord.emoji.EmojiObject.EmojiObjectCache;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextCache;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextElement;

public class EmojiContext {
	public final String text;
	public final Map<Integer, EmojiObject> emojis;

	public EmojiContext(final String text, final Map<Integer, EmojiObject> emojis) {
		this.text = text;
		this.emojis = emojis;
	}

	public static class EmojiContextLoader {
		public static EmojiContext getEmojiFormattedString(String text, final boolean isTextFieldRendering) {
			final Map<Integer, EmojiObject> emojiMap = Maps.newHashMap();
			if (!StringUtil.isNullOrEmpty(text)) {
				Compat.CompatMinecraft.getMinecraft().mcProfiler.startSection("emojicordParse");
				final EmojiText emojiPair = EmojiTextCache.instance.getEmojiText(text);
				text = emojiPair.text;
				final Matcher matcher = EmojiText.placeHolderPattern.matcher(text);
				final StringBuffer sb = new StringBuffer();
				final List<EmojiTextElement> emojis = emojiPair.emojis;
				while (matcher.find()) {
					final int emojiIndex = NumberUtils.toInt(matcher.group(1), -1);
					if (0<=emojiIndex&&emojiIndex<emojis.size()) {
						final EmojiTextElement entry = emojis.get(emojiIndex);
						final EmojiId emojiId = entry.id;
						final EmojiObject emoji = emojiId==null ? null : EmojiObjectCache.instance.getEmojiObject(emojiId);
						if (emoji==null)
							matcher.appendReplacement(sb, entry.raw);
						else {
							matcher.appendReplacement(sb, "?");
							final int index = sb.length()-"?".length();
							emojiMap.put(index, emoji);
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
}
