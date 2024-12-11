package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * UnsupportedBox class.
 * </p>
 *
 */
public class UnsupportedBox extends Box {

	/**
	 * <p>
	 * Constructor for UnsupportedBox.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a int
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public UnsupportedBox(InputStream source, int length, long extendedLength) throws IOException {
		super(length);

		if (length == 1) {
			source.skip((int) extendedLength - 16);
		}
		else {
			source.skip(length - 8);
		}
	}

}
