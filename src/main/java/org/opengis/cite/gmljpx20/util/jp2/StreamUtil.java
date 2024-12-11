package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * StreamUtil class.
 * </p>
 *
 */
public class StreamUtil {

	/**
	 * Reads count bytes from the current stream into a byte array and advances the
	 * current position by count bytes.
	 * @param s The stream to read.
	 * @param count The number of bytes to read.
	 * @return A byte array containing data read from the underlying stream. This might be
	 * less than the number of bytes requested if the end of the stream is reached.
	 * @throws java.io.IOException An I/O error occurs. // @throws ObjectDisposedException
	 * The stream is closed. // @throws ArgumentOutOfRangeException count is negative.
	 */
	public static byte[] readBytes(InputStream s, int count) throws IOException {
		byte[] buffer = new byte[count];
		int len = s.read(buffer, 0, count);
		if (len < count)
			resizeArray(buffer, len);
		return buffer;
	}

	private static byte[] readExactBytes(InputStream s, int count) throws Exception {
		byte[] buffer = new byte[count];
		int len = s.read(buffer, 0, count);
		if (len < count)
			throw new Exception("Expected " + count + " bytes but only read " + len + " bytes.");
		return buffer;
	}

	/**
	 * <p>
	 * readToEnd.
	 * </p>
	 * @param s a {@link java.io.InputStream} object
	 * @return an array of {@link byte} objects
	 * @throws java.io.IOException if any.
	 */
	public static byte[] readToEnd(InputStream s) throws IOException {
		int len;
		int position = 0;
		byte[] buffer = new byte[512];
		while ((len = s.read(buffer, position, 512)) > 0) {
			position += len;
			if (len < 512) {
				resizeArray(buffer, position);
				break;
			}
			resizeArray(buffer, position + 512);
		}

		return buffer;
	}

	private static Object resizeArray(Object oldArray, int newSize) {
		int oldSize = java.lang.reflect.Array.getLength(oldArray);
		Class<?> elementType = oldArray.getClass().getComponentType();
		Object newArray = java.lang.reflect.Array.newInstance(elementType, newSize);
		int preserveLength = Math.min(oldSize, newSize);
		if (preserveLength > 0)
			System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
		return newArray;
	}

	/**
	 *
	 * Reads a 4-byte signed integer from the current stream using big-endian encoding and
	 * advances the current position of the stream by four bytes.
	 * @param s The stream to read.
	 * @return A 4-byte signed integer read from the current stream. // @throws
	 * EndOfStreamException The end of the stream is reached. // @throws
	 * ObjectDisposedException The stream is closed.
	 * @throws java.lang.Exception if any.
	 */
	public static int readBInt32(InputStream s) throws Exception {
		byte[] buffer = readExactBytes(s, 4);
		return BitConverter.toInt32(buffer, 0);
	}

	/**
	 * Reads an 8-byte signed integer from the current stream using big-endian encoding
	 * and advances the current position of the stream by eight bytes.
	 * @param s The stream to read.
	 * @return An 8-byte signed integer read from the current stream.
	 * @throws java.lang.Exception //@throws EndOfStreamjava.lang.Exception The end of the
	 * stream is reached. //@throws ObjectDisposedjava.lang.Exception The stream is
	 * closed.
	 */
	public static long readBInt64(InputStream s) throws Exception {
		byte[] buffer = readExactBytes(s, 8);
		return BitConverter.toInt64(buffer, 0);
	}

	/**
	 * Reads a 2-byte unsigned integer from the current stream using big-endian encoding
	 * and advances the position of the stream by two bytes.
	 * @param s The stream to read.
	 * @return A 2-byte unsigned integer read from this stream.
	 * @throws java.lang.Exception //@throws EndOfStreamjava.lang.Exception The end of the
	 * stream is reached. //@throws ObjectDisposedjava.lang.Exception The stream is
	 * closed.
	 */
	public static short readBUInt16(InputStream s) throws Exception {
		byte[] buffer = readExactBytes(s, 2);
		return BitConverter.toInt16(buffer, 0);
	}

	/**
	 * Reads a 4-byte unsigned integer from the current stream using big-endian encoding
	 * and advances the position of the stream by four bytes.
	 * @param s The stream to read.
	 * @return A 4-byte unsigned integer read from this stream.
	 * @throws java.lang.Exception //@throws EndOfStreamjava.lang.Exception The end of the
	 * stream is reached. //@throws ObjectDisposedjava.lang.Exception The stream is
	 * closed.
	 */
	public static long readBUInt32(InputStream s) throws Exception {
		byte[] buffer = readExactBytes(s, 4);
		return BitConverter.ToUInt32(buffer, 0);
	}

	/**
	 * Reads an 8-byte unsigned integer from the current stream using big-endian encoding
	 * and advances the position of the stream by eight bytes.
	 * @param s The stream to read.
	 * @return An 8-byte unsigned integer read from this stream.
	 * @throws java.lang.Exception //@throws EndOfStreamjava.lang.Exception The end of the
	 * stream is reached. //@throws ObjectDisposedjava.lang.Exception The stream is
	 * closed.
	 */
	public static long readBUInt64(InputStream s) throws Exception {
		byte[] buffer = readExactBytes(s, 8);
		return BitConverter.toInt64(buffer, 0);
	}

}
