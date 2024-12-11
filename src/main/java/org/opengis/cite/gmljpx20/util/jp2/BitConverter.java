package org.opengis.cite.gmljpx20.util.jp2;

/**
 * <p>
 * BitConverter class.
 * </p>
 */
public class BitConverter {

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a boolean
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(boolean x) {
		return new byte[] { (byte) (x ? 1 : 0) };
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param c a char
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(char c) {
		return new byte[] { (byte) (c & 0xff), (byte) (c >> 8 & 0xff) };
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a double
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(double x) {
		return getBytes(Double.doubleToRawLongBits(x));
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a short
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(short x) {
		return new byte[] { (byte) (x >>> 8), (byte) x };
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a int
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(int x) {
		return new byte[] { (byte) (x >>> 24), (byte) (x >>> 16), (byte) (x >>> 8), (byte) x };
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a long
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(long x) {
		return new byte[] { (byte) (x >>> 56), (byte) (x >>> 48), (byte) (x >>> 40), (byte) (x >>> 32),
				(byte) (x >>> 24), (byte) (x >>> 16), (byte) (x >>> 8), (byte) x };
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a float
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(float x) {
		return getBytes(Float.floatToRawIntBits(x));
	}

	/**
	 * <p>
	 * getBytes.
	 * </p>
	 * @param x a {@link java.lang.String} object
	 * @return an array of {@link byte} objects
	 */
	public static byte[] getBytes(String x) {
		return x.getBytes();
	}

	/**
	 * <p>
	 * doubleToInt64Bits.
	 * </p>
	 * @param x a double
	 * @return a long
	 */
	public static long doubleToInt64Bits(double x) {
		return Double.doubleToRawLongBits(x);
	}

	/**
	 * <p>
	 * int64BitsToDouble.
	 * </p>
	 * @param x a long
	 * @return a double
	 */
	public static double int64BitsToDouble(long x) {
		return (double) x;
	}

	/**
	 * <p>
	 * toBoolean.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a boolean
	 * @throws java.lang.Exception if any.
	 */
	public boolean toBoolean(byte[] bytes, int index) throws Exception {
		if (bytes.length != 1)
			throw new Exception("The length of the byte array must be at least 1 byte long.");
		return bytes[index] != 0;
	}

	/**
	 * <p>
	 * toChar.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a char
	 * @throws java.lang.Exception if any.
	 */
	public char toChar(byte[] bytes, int index) throws Exception {
		if (bytes.length != 2)
			throw new Exception("The length of the byte array must be at least 2 bytes long.");
		return (char) ((0xff & bytes[index]) << 8 | (0xff & bytes[index + 1]) << 0);
	}

	/**
	 * <p>
	 * toDouble.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a double
	 * @throws java.lang.Exception if any.
	 */
	public double toDouble(byte[] bytes, int index) throws Exception {
		if (bytes.length != 8)
			throw new Exception("The length of the byte array must be at least 8 bytes long.");
		return Double.longBitsToDouble(toInt64(bytes, index));
	}

	/**
	 * <p>
	 * toInt16.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a short
	 * @throws java.lang.Exception if any.
	 */
	public static short toInt16(byte[] bytes, int index) throws Exception {
		if (bytes.length != 8)
			throw new Exception("The length of the byte array must be at least 8 bytes long.");
		return (short) ((0xff & bytes[index]) << 8 | (0xff & bytes[index + 1]) << 0);
	}

	/**
	 * <p>
	 * toInt32.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a int
	 * @throws java.lang.Exception if any.
	 */
	public static int toInt32(byte[] bytes, int index) throws Exception {
		if (bytes.length != 4)
			throw new Exception("The length of the byte array must be at least 4 bytes long.");
		return (int) ((int) (0xff & bytes[index]) << 56 | (int) (0xff & bytes[index + 1]) << 48
				| (int) (0xff & bytes[index + 2]) << 40 | (int) (0xff & bytes[index + 3]) << 32);
	}

	/**
	 * <p>
	 * ToUInt32.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a long
	 */
	public static long ToUInt32(byte[] bytes, int index) {
		long result = (int) bytes[index] & 0xff << 24;
		result |= ((int) bytes[index + 1] & 0xff) << 16;
		result |= ((int) bytes[index + 2] & 0xff) << 8;
		result |= ((int) bytes[index + 3] & 0xff);
		return result & 0xFFFFFFFFL;
	}

	/**
	 * <p>
	 * toInt64.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a long
	 * @throws java.lang.Exception if any.
	 */
	public static long toInt64(byte[] bytes, int index) throws Exception {
		if (bytes.length != 8)
			throw new Exception("The length of the byte array must be at least 8 bytes long.");
		return (long) ((long) (0xff & bytes[index]) << 56 | (long) (0xff & bytes[index + 1]) << 48
				| (long) (0xff & bytes[index + 2]) << 40 | (long) (0xff & bytes[index + 3]) << 32
				| (long) (0xff & bytes[index + 4]) << 24 | (long) (0xff & bytes[index + 5]) << 16
				| (long) (0xff & bytes[index + 6]) << 8 | (long) (0xff & bytes[index + 7]) << 0);
	}

	/**
	 * <p>
	 * toSingle.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @param index a int
	 * @return a float
	 * @throws java.lang.Exception if any.
	 */
	public static float toSingle(byte[] bytes, int index) throws Exception {
		if (bytes.length != 4)
			throw new Exception("The length of the byte array must be at least 4 bytes long.");
		return Float.intBitsToFloat(toInt32(bytes, index));
	}

	/**
	 * <p>
	 * toString.
	 * </p>
	 * @param bytes an array of {@link byte} objects
	 * @return a {@link java.lang.String} object
	 * @throws java.lang.Exception if any.
	 */
	public static String toString(byte[] bytes) throws Exception {
		if (bytes == null)
			throw new Exception("The byte array must have at least 1 byte.");
		return new String(bytes);
	}

}
