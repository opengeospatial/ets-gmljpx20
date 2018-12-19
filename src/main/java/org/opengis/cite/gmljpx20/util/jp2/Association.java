package org.opengis.cite.gmljpx20.util.jp2;

import java.io.InputStream;

public class Association extends Box {

    public Association( InputStream source, int length, long extendedLength ) {
        super( source, length, extendedLength );
        while ( true ) {
            try {
                Box box = Box.fromStream( source );
                boxes.add( box );
                int auxLenght = 0;

                for (int i = 0; i < boxes.size(); i++ ) {
                    Box auxBox = boxes.get( i );
                    auxLenght += auxBox.length;
                }
                if ( auxLenght == length - 8 )
                    return;
            } catch ( Exception e ) {
                // if any error occurs
                return;
            }
        }

    }

}
