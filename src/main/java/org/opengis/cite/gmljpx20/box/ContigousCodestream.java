package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

/**
 * <p>
 * ContigousCodestream class.
 * </p>
 *
 */
public class ContigousCodestream extends Box {

	private int[] contigousCodestreamData;

	/**
	 * <p>
	 * Constructor for ContigousCodestream.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @param length a long
	 * @param extendedLength a long
	 * @throws java.io.IOException if any.
	 */
	public ContigousCodestream(InputStream source, long length, long extendedLength) throws IOException {
		super(length);

		int[] dataTemp = null;
		byte[] data;
		if (length == 0) {
			data = StreamUtil.readToEnd(source);
		}
		else if (length == 1) {
			data = StreamUtil.readBytes(source, (int) extendedLength - 16);
		}
		else {
			data = StreamUtil.readBytes(source, (int) length - 8);
			dataTemp = new int[2];
			if (data[0] != 0) {
				int position = 0;
				int SOC = getushort(data, position);
				position = position + 2;
				int SIZ = getushort(data, position);
				position = position + 2;
				int LSIZ = getushort(data, position);
				position = position + 2;
				int RSIZ = getushort(data, position);
				position = position + 2;
				dataTemp[0] = getInt32(data, position);
				position = position + 4;
				dataTemp[1] = getInt32(data, position);
			}
		}
		contigousCodestreamData = dataTemp;
	}

	/**
	 * <p>
	 * Getter for the field <code>contigousCodestreamData</code>.
	 * </p>
	 * @return an array of {@link int} objects
	 */
	public int[] getContigousCodestreamData() {
		return contigousCodestreamData;
	}

	private static int getushort(byte[] arr, int off) {
		return arr[off] << 8 & 0xFF00 | arr[off + 1] & 0xFF;
	}

	private static int getInt32(byte[] arr, int off) {
		return (arr[3 + off] & 0xFF) | ((arr[2 + off] & 0xFF) << 8) | ((arr[1 + off] & 0xFF) << 16)
				| ((arr[0 + off] & 0xFF) << 24);
	}

}
