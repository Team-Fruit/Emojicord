package net.teamfruit.emojicord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

public class EmojiParser {
	static final @Nonnull Pattern pg = Pattern.compile("\\((?:([^\\)]*?)~)?(.*?)\\)");
	static final @Nonnull Pattern pp = Pattern.compile("(?:([^\\d-\\+Ee\\.]?)([\\d-\\+Ee\\.]*)?)+?");
	static final @Nonnull Pattern p = Pattern.compile("<a?\\:(\\w+?)\\:([a-zA-Z0-9+/]+?)>|\\:(\\w+?)\\:"); // <a?\:(\w+?)\:([a-zA-Z0-9+/]+?)>|\:(\w+?)\:

	public List<Pair<Emoji, String>> parse(final String text) {
		final List<Pair<Emoji, String>> addedEmojis = new ArrayList<>();
		final Matcher matcher = EmojiParser.p.matcher(text);
		//		while (matcher.find()) {
		//			final String matched = matcher.group();
		//			System.out.printf("[%s] がマッチしました。 Pattern:[%s] input:[%s] m0:[%s] m1:[%s] m2:[%s] m3[%s]\n", matched,
		//					pattern, input, matcher.group(0), matcher.group(1), matcher.group(2), matcher.group(3));
		//		}

		return addedEmojis;
	}
}
