package org.opengis.cite.gmljpx20.util.jp2;

import org.opengis.cite.gmljpx20.box.Association;
import org.opengis.cite.gmljpx20.box.Box;
import org.opengis.cite.gmljpx20.box.ContigousCodestream;
import org.opengis.cite.gmljpx20.box.FileType;
import org.opengis.cite.gmljpx20.box.Label;
import org.opengis.cite.gmljpx20.box.ResourceRequirements;
import org.opengis.cite.gmljpx20.box.UnsupportedBox;
import org.opengis.cite.gmljpx20.box.XMLBox;

import java.io.InputStream;

/**
 * <p>
 * BoxReader class.
 * </p>
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BoxReader {

	/**
	 * <p>
	 * readFromStream.
	 * </p>
	 * @param source a {@link java.io.InputStream} object
	 * @return a {@link org.opengis.cite.gmljpx20.box.Box} object
	 * @throws java.lang.Exception if any.
	 */
	public static Box readFromStream(InputStream source) throws Exception {
		long length = StreamUtil.readBUInt32(source);
		long extendedLength = 0;
		if (length == 1) {
			extendedLength = StreamUtil.readBUInt64(source);
		}

		int type = StreamUtil.readBInt32(source);
		switch (type) {
			case 0x6A703263:
				return new ContigousCodestream(source, (int) length, extendedLength);
			case 0x66747970:
				return new FileType(source, (int) length, extendedLength);
			case 0x61736F63:
				return new Association(source, (int) length);
			case 0x786D6C20:
				return new XMLBox(source, (int) length, extendedLength);
			case 0x6C626C20:
				return new Label(source, (int) length, extendedLength);
			case 0x72726571:
				return new ResourceRequirements(source, (int) length, extendedLength);
			default:
				return new UnsupportedBox(source, (int) length, extendedLength);
		}
	}

}
