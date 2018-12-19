package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileType extends Box {

    public String fileTypeData;

    public FileType( InputStream source, int length, long extendedLength ) throws IOException {
        super( source, length, extendedLength );
        this.lengthfinal = length - 8;
        byte[] data;
        if ( length == 0 ) {
            data = StreamUtil.readToEnd( source );
        } else if ( length == 1 ) {
            data = StreamUtil.readBytes( source, (int) extendedLength - 16 );
        } else {
            data = StreamUtil.readBytes( source, length - 8 );
            int contador = 0;
            byte[] dataTemp = new byte[data.length];
            for ( int a = 0; a < data.length; a++ ) {
                if ( data[a] != 0 ) {
                    dataTemp[contador] = data[a];
                    contador++;
                }
            }
            data = dataTemp;
        }
        fileTypeData = new String( data, Charset.forName( "UTF8" ) );
    }

}
