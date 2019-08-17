package net.teamfruit.emojicord.emoji;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.emoji.EmojiText.EmojiTextParser;

public class EmojiTextParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		final String input = "rtvh<:crime:332181988633083925>we:sushi:bxfn";
		final Pattern pattern = EmojiTextParser.pattern;
		final Matcher matcher = EmojiTextParser.pattern.matcher(input);
		while (matcher.find()) {
			final String matched = matcher.group();
			System.out.printf("[%s] がマッチしました。 Pattern:[%s] input:[%s] m0:[%s] m1:[%s] m2:[%s] m3[%s]\n", matched,
					pattern, input, matcher.group(0), matcher.group(1), matcher.group(2), matcher.group(3));
		}
		Log.log.info("input[{}] pattern[{}]", input, pattern);
		Log.log.info("find() result is {}", matcher.find());
		Log.log.info("lookingAt() result is {}", matcher.lookingAt());
		Log.log.info("matches() result is {}\n", matcher.matches());

		//assertEquals(EmojiParser.p., actual);
	}

}
