package org.opengis.cite.gmljpx20.box;

import java.io.IOException;
import java.io.InputStream;

public class UnsupportedBox extends Box {

    public UnsupportedBox( InputStream source, int length, long extendedLength ) throws IOException {
        super( source, length, extendedLength );

        if ( length == 1 ) {
            source.skip( (int) extendedLength - 16 );
        } else {
            source.skip( length - 8 );
        }
    }

}
