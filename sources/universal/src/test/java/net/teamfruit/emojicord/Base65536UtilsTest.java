package net.teamfruit.emojicord;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

public class Base65536UtilsTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		for (long i = 1; i < Long.MAX_VALUE - 1 && i < i * 2; i *= 2)
			Log.log.info(i + " : " + Base65536Utils.encode(i).length() + " : " + Base65536Utils.encode(i));
	}

	@Test
	public void test1A() {
		for (long i = 1; i < Long.MAX_VALUE - 1 && i < i * 2; i *= 2)
			Log.log.info(i + " : " + Base64Utils.encode2(i).length() + " : " + Base65536Utils.encode(i).length());
	}

	@Test
	public void test2() {
		for (long i = 1; i < Long.MAX_VALUE - 1 && i < i * 2; i *= 2)
			assertEquals(i, Base65536Utils.decode(Base65536Utils.encode(i)));
	}

	@Test
	public void test3() {
		for (int i = 1; i < 1000; i++) {
			final long num = RandomUtils.nextLong();
			assertEquals(num, Base65536Utils.decode(Base65536Utils.encode(num)));
		}
	}
}
