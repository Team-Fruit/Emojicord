package net.teamfruit.emojicord;

import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 *         Date: 22.07.11
 */
public final class Base65536Utils {
	private Base65536Utils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Converts a byte array into a char array using a 65536-based encoding.
	 *
	 * @param dataBytes array to encode
	 * @return encoded char array
	 */
	@Nonnull
	public static char[] encodeBase65536(@Nonnull final byte[] dataBytes) {
		final int byteCount = dataBytes.length;
		final int charCount = byteCount / 2 + 1;

		final char[] dataChars = new char[charCount];

		for (int byteIndex = 0; byteIndex < byteCount; byteIndex += 2) {
			final int leftByte = (dataBytes[byteIndex] & 0xFF) << 8;
			final int rightByte = (byteIndex + 1 < byteCount ? dataBytes[byteIndex + 1] : 0x01) & 0xFF;

			dataChars[byteIndex / 2] = (char) (leftByte | rightByte);
		}

		if (byteCount % 2 == 0)
			dataChars[charCount - 1] = (char) 0x0100;

		return dataChars;
	}

	/**
	 * Converts a byte array into a {@link String} using a 65536-based encoding.
	 * Equivalent to {@code new String(Base65536.encodeBase65536(dataBytes))}.
	 *
	 * @param dataBytes array to encode
	 * @return encoded {@link String}
	 */
	@Nonnull
	public static String encodeBase65536String(@Nonnull final byte[] dataBytes) {
		return new String(encodeBase65536(dataBytes));
	}

	/**
	 * Restores a byte array from a char array encoded with a 65536-based encoding.
	 *
	 * @param dataChars array to decode
	 * @return restored byte array
	 * @throws IllegalArgumentException if char array is not a correctly encoded byte array
	 */
	@Nonnull
	public static byte[] decodeBase65536(@Nonnull final char[] dataChars) {
		final int charCount = dataChars.length;

		if (dataChars[charCount - 1] != (char) 0x0100 && (dataChars[charCount - 1] & 0xFF) != 0x01)
			throw new IllegalArgumentException("Argument 'dataChars' is not a correctly Base65536-encoded bytes.");

		final int byteCount = dataChars[charCount - 1] == (char) 0x0100 ? (charCount - 1) * 2 : charCount * 2 - 1;

		final byte[] dataBytes = new byte[byteCount];

		for (int byteIndex = 0; byteIndex < byteCount; byteIndex += 2) {
			final char dataChar = dataChars[byteIndex / 2];

			dataBytes[byteIndex] = (byte) (dataChar >>> 8);

			if (byteIndex + 1 < byteCount)
				dataBytes[byteIndex + 1] = (byte) (dataChar & 0xFF);
		}

		return dataBytes;
	}

	/**
	 * Restores a byte array from a {@link String} encoded with a 65536-based encoding.
	 * Equivalent to {@code decodeBase65536(dataString.toCharArray)}.
	 *
	 * @param dataString {@link String} to decode
	 * @return restored byte array
	 * @throws IllegalArgumentException if {@link String} is not a correctly encoded byte array
	 */
	@Nonnull
	public static byte[] decodeBase65536(@Nonnull final String dataString) {
		return decodeBase65536(dataString.toCharArray());
	}

	public static String encode(final long num) {
		return encodeBase65536String(BigInteger.valueOf(num).toByteArray());
	}

	public static long decode(final String str) {
		return new BigInteger(decodeBase65536(str)).longValue();
	}
}