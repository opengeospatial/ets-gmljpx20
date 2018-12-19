package org.opengis.cite.gmljpx20.util.jp2;

import org.opengis.cite.gmljpx20.box.Box;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JP2Stream {

    public InputStream fs;

    public List<Box> boxes = new ArrayList<>();

    public JP2Stream( InputStream source ) {
        fs = source;
        while ( true ) {
            try {
                Box box = Box.fromStream( source );
                boxes.add( box );
            } catch ( Exception e ) {
                return;
            }
        }
    }

}
