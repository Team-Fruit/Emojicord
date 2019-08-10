package net.teamfruit.emojicord;

import java.math.BigInteger;

import javax.util.Base64;

import org.apache.commons.lang3.StringUtils;

public final class SimpleBase64Utils {
	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final int BASE = CHARS.length();
	private static final BigInteger BIG_BASE = BigInteger.valueOf(BASE);

	private SimpleBase64Utils() {
	}

	public static String encode1(final long num) {
		return Base64.getEncoder().encodeToString(BigInteger.valueOf(num).toByteArray());
	}

	public static String encode2(long num) {
		if (num <= 0)
			return "0";
		final StringBuilder sb = new StringBuilder();
		while (num > 0) {
			final int sup = (int) (num % BASE);
			sb.append(CHARS.charAt(sup));
			num = num / BASE;
		}
		return StringUtils.rightPad(sb.reverse().toString(), 12, "=");
	}

	public static String encode3(long num) {
		if (num <= 0)
			return "0";
		final StringBuilder sb = new StringBuilder();
		while (num > 0) {
			final int sup = (int) (num & 63);
			sb.append(CHARS.charAt(sup));
			num >>= 6;
		}
		return StringUtils.rightPad(sb.reverse().toString(), 12, "=");
	}

	public static long decode(String str) {
		str = StringUtils.stripEnd(str, "=");
		BigInteger num = BigInteger.valueOf(0);
		final char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			final int index = CHARS.indexOf(chars[chars.length - i - 1]);
			if (index == -1)
				break;
			num = num.add(BIG_BASE.pow(i).multiply(BigInteger.valueOf(index)));
		}
		return num.longValue();
	}
}