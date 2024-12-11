package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

/**
 * <p>
 * ResourceRequirements class.
 * </p>
 */
public class ResourceRequirements extends Box {

	private byte[] rreqData;

	/**
	 * <p>
	 * Constructor for ResourceRequirements.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a int
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public ResourceRequirements(InputStream source, int length, long extendedLength) throws IOException {
		super(length);

		if (length == 0) {
			rreqData = StreamUtil.readToEnd(source);
		}
		else if (length == 1) {
			rreqData = StreamUtil.readBytes(source, (int) extendedLength - 16);
		}
		else {
			rreqData = StreamUtil.readBytes(source, length - 8);
		}
	}

	/**
	 * <p>
	 * Getter for the field <code>rreqData</code>.
	 * </p>
	 * @return an array of {@link byte} objects
	 */
	public byte[] getRreqData() {
		return rreqData;
	}

}
