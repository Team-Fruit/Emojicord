package net.teamfruit.emojicord;

import java.math.BigInteger;

public final class Base62 {
	private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int BASE = CHARS.length();
	private static final BigInteger BIG_BASE = BigInteger.valueOf(BASE);

	private Base62() {
	}

	public static String encode(long num) {
		if (num <= 0)
			return "0";
		final StringBuilder sb = new StringBuilder();
		while (num > 0) {
			final int sup = (int) (num % BASE);
			sb.append(CHARS.charAt(sup));
			num = num / BASE;
		}
		return sb.reverse().toString();
	}

	public static long decode(final String str) {
		BigInteger num = BigInteger.valueOf(0);
		final char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			final int index = CHARS.indexOf(chars[chars.length - i - 1]);
			num = num.add(BIG_BASE.pow(i).multiply(BigInteger.valueOf(index)));
		}
		return num.longValue();
	}
}