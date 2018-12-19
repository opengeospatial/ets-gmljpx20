package org.opengis.cite.gmljpx20.util.jp2;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BoxTest {

    @Test
    public void testFromStream() {
        InputStream is = getClass().getResourceAsStream( "/jp2/romagmljp2-collection2-rreq7.jp2" );
        List<Box> boxes = new JP2Stream( is ).boxes;

        assertThat( boxes.size(), is( 6 ) );
        assertThat( boxes.get( 0 ), is( instanceOf( UnsupportedBox.class ) ) );
        assertThat( boxes.get( 1 ), is( instanceOf( FileType.class ) ) );
        assertThat( boxes.get( 2 ), is( instanceOf( ResourceRequirements.class ) ) );
        assertThat( boxes.get( 3 ), is( instanceOf( UnsupportedBox.class ) ) );
        assertThat( boxes.get( 4 ), is( instanceOf( Association.class ) ) );
        assertThat( boxes.get( 5 ), is( instanceOf( ContigousCodestream.class ) ) );
    }

}
