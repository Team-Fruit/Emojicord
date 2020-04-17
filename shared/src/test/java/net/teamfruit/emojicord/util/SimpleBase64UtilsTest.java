package net.teamfruit.emojicord.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Base64;

import org.junit.Before;
import org.junit.Test;

import net.teamfruit.emojicord.Log;

public class SimpleBase64UtilsTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		for (long i = 1; i<Long.MAX_VALUE-1&&i<i*2; i *= 2)
			Log.log.info(i+" : "
					+SimpleBase64Utils.encode1(i)+" : "
					+SimpleBase64Utils.encode2(i)+" : "
					+SimpleBase64Utils.encode3(i));
	}

	public static void main(final String[] args) {
		new SimpleBase64UtilsTest().test1();
	}

	@Test
	public void test2() {
		for (long i = 1; i<Long.MAX_VALUE-1&&i<i*2; i *= 2)
			assertEquals(i, SimpleBase64Utils.decode(SimpleBase64Utils.encode2(i)));
	}

	@Test
	public void test3() {
		Log.log.info(264244926311563265l+" : "
				+SimpleBase64Utils.encode1(264244926311563265l)+" : "
				+SimpleBase64Utils.encode2(264244926311563265l)+" : "
				+SimpleBase64Utils.encode3(264244926311563265l));
	}

	@Test
	public void test4() {
		Log.log.info(Base64.getEncoder().encodeToString(new byte[0])+" : "
				+Arrays.toString(Base64.getDecoder().decode(Base64.getEncoder().encodeToString(new byte[0]))));
	}
}
