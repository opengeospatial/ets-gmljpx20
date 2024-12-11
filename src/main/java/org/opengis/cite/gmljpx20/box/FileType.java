package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

/**
 * <p>
 * FileType class.
 * </p>
 */
public class FileType extends Box {

	private String fileTypeData;

	/**
	 * <p>
	 * Constructor for FileType.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a int
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public FileType(InputStream source, int length, long extendedLength) throws IOException {
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
			int contador = 0;
			byte[] dataTemp = new byte[data.length];
			for (int a = 0; a < data.length; a++) {
				if (data[a] != 0) {
					dataTemp[contador] = data[a];
					contador++;
				}
			}
			data = dataTemp;
		}
		fileTypeData = new String(data, Charset.forName("UTF8"));
	}

	/**
	 * <p>
	 * Getter for the field <code>fileTypeData</code>.
	 * </p>
	 * @return a {@link java.lang.String} object
	 */
	public String getFileTypeData() {
		return fileTypeData;
	}

}
