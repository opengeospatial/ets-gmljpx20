package org.opengis.cite.gmljpx20.util.jp2;

import static org.opengis.cite.gmljpx20.util.jp2.BoxReader.readFromStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opengis.cite.gmljpx20.box.Box;

/**
 * <p>
 * JP2Stream class.
 * </p>
 */
public class JP2Stream {

	public InputStream fs;

	public List<Box> boxes = new ArrayList<>();

	/**
	 * <p>
	 * Constructor for JP2Stream.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 */
	public JP2Stream(InputStream source) {
		fs = source;
		while (true) {
			try {
				Box box = readFromStream(source);
				boxes.add(box);
			}
			catch (Exception e) {
				return;
			}
		}
	}

}
