package org.opengis.cite.gmljpx20.box;

import org.opengis.cite.gmljpx20.util.jp2.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class XMLBox extends Box {

    private String xmldata;

    public XMLBox( InputStream source, int length, long extendedLength ) throws IOException {
        super( source, length, extendedLength );

        byte[] data;
        if ( length == 0 ) {
            data = StreamUtil.readToEnd( source );
        } else if ( length == 1 ) {
            data = StreamUtil.readBytes( source, (int) extendedLength - 16 );
        } else {
            data = StreamUtil.readBytes( source, length - 8 );
        }
        xmldata = new String( data, Charset.forName( "UTF8" ) );
    }

    public String getXmldata() {
        return xmldata;
    }

}
