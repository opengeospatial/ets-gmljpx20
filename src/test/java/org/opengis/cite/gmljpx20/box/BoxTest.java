package org.opengis.cite.gmljpx20.box;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.opengis.cite.gmljpx20.util.jp2.JP2Stream;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BoxTest {

    @Test
    public void testFromStream() {
        InputStream is = getClass().getResourceAsStream( "/jp2/romagmljp2-collection2-rreq7.jp2" );
        List<Box> boxes = new JP2Stream( is ).boxes;

        assertThat( boxes.size(), is( 6 ) );

        Box box0 = boxes.get( 0 );
        assertThat( box0, is( instanceOf( UnsupportedBox.class ) ) );

        Box box1 = boxes.get( 1 );
        assertThat( box1, is( instanceOf( FileType.class ) ) );

        Box box2 = boxes.get( 2 );
        assertThat( box2, is( instanceOf( ResourceRequirements.class ) ) );

        Box box3 = boxes.get( 3 );
        assertThat( box3, is( instanceOf( UnsupportedBox.class ) ) );

        Box box4 = boxes.get( 4 );
        assertThat( box4, is( instanceOf( Association.class ) ) );
        List<Box> box4boxes = ( (Association) box4 ).getBoxes();
        assertThat( box4boxes.size(), is( 2 ) );
        assertThat( box4boxes.get( 0 ), is( instanceOf( Label.class ) ) );
        assertThat( box4boxes.get( 1 ), is( instanceOf( Association.class ) ) );
        List<Box> boxes4boxes1boxes = ( (Association) box4boxes.get( 1 ) ).getBoxes();
        assertThat( boxes4boxes1boxes.size(), is( 2 ) );
        assertThat( boxes4boxes1boxes.get( 0 ), is( instanceOf( Label.class ) ) );
        assertThat( boxes4boxes1boxes.get( 1 ), is( instanceOf( XMLBox.class ) ) );

        Box box5 = boxes.get( 5 );
        assertThat( box5, is( instanceOf( ContigousCodestream.class ) ) );
    }

}
