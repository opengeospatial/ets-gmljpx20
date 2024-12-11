package org.opengis.cite.gmljpx20.box;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * <p>
 * Label class.
 * </p>
 *
 */
public class Label extends Box {

	private String label;

	/**
	 * <p>
	 * Constructor for Label.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a int
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public Label(InputStream source, int length, long extendedLength) throws IOException {
		super(length);

		byte[] data;
		if (length == 0) {
			data = StreamUtil.readToEnd(source);
		}
		else if (length == 1) {
			data = StreamUtil.readBytes(source, (int) extendedLength - 16);
		}
		else {
			data = StreamUtil.readBytes(source, length - 8);
		}
		label = new String(data, Charset.forName("UTF8"));
	}

	/**
	 * <p>
	 * Getter for the field <code>label</code>.
	 * </p>
	 * @return a {@link java.lang.String} object
	 */
	public String getLabel() {
		return label;
	}

}
