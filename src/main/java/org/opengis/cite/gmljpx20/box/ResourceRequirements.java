package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

public class ResourceRequirements extends Box {

    private byte[] rreqData;

    public ResourceRequirements( InputStream source, int length, long extendedLength ) throws IOException {
        super( length );

        if ( length == 0 ) {
            rreqData = StreamUtil.readToEnd( source );
        } else if ( length == 1 ) {
            rreqData = StreamUtil.readBytes( source, (int) extendedLength - 16 );
        } else {
            rreqData = StreamUtil.readBytes( source, length - 8 );
        }
    }

    public byte[] getRreqData() {
        return rreqData;
    }

}
