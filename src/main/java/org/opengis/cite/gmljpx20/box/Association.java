package org.opengis.cite.gmljpx20.box;

import static org.opengis.cite.gmljpx20.util.jp2.BoxReader.readFromStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Association extends Box {

    private List<Box> boxes = new ArrayList<>();

    public Association( InputStream source, int length ) {
        super( length );
        while ( true ) {
            try {
                Box box = readFromStream( source );
                boxes.add( box );
                int auxLenght = 0;

                for ( int i = 0; i < boxes.size(); i++ ) {
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

    public List<Box> getBoxes() {
        return boxes;
    }

}
