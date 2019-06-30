package net.teamfruit.emojicord;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Base64UtilsTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		for (long i = 1; i < Long.MAX_VALUE - 1 && i < i * 2; i *= 2)
			Log.log.info(i + " : "
					+ Base64Utils.encode1(i) + " : "
					+ Base64Utils.encode2(i) + " : "
					+ Base64Utils.encode3(i));
	}

	@Test
	public void test2() {
		for (long i = 1; i < Long.MAX_VALUE - 1 && i < i * 2; i *= 2)
			assertEquals(i, Base64Utils.decode(Base64Utils.encode2(i)));
	}
}
