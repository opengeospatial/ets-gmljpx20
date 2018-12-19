package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Label extends Box {

    private String label;

    public Label( InputStream source, int length, long extendedLength ) throws IOException {
        super( source, length, extendedLength );

        byte[] data;
        if ( length == 0 ) {
            data = StreamUtil.readToEnd( source );
        } else if ( length == 1 ) {
            data = StreamUtil.readBytes( source, (int) extendedLength - 16 );
        } else {
            data = StreamUtil.readBytes( source, length - 8 );
        }
        label = new String( data, Charset.forName( "UTF8" ) );
    }

    public String getLabel() {
        return label;
    }

}
