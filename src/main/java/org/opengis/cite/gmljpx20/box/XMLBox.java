package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

/**
 * <p>
 * XMLBox class.
 * </p>
 */
public class XMLBox extends Box {

	private String xmldata;

	/**
	 * <p>
	 * Constructor for XMLBox.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a int
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public XMLBox(InputStream source, int length, long extendedLength) throws IOException {
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
		xmldata = new String(data, Charset.forName("UTF8"));
	}

	/**
	 * <p>
	 * Getter for the field <code>xmldata</code>.
	 * </p>
	 * @return a {@link java.lang.String} object
	 */
	public String getXmldata() {
		return xmldata;
	}

}
