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
        List<Box> boxes = new JP2Stream( is ).Boxes;

        assertThat( boxes.size(), is( 6 ) );

        Box box0 = boxes.get( 0 );
        assertThat( box0, is( instanceOf( UnsupportedBox.class ) ) );
        assertThat( box0.Boxes.size(), is( 0 ) );

        Box box1 = boxes.get( 1 );
        assertThat( box1, is( instanceOf( FileType.class ) ) );
        assertThat( box1.Boxes.size(), is( 0 ) );

        Box box2 = boxes.get( 2 );
        assertThat( box2, is( instanceOf( ResourceRequirements.class ) ) );
        assertThat( box2.Boxes.size(), is( 0 ) );

        Box box3 = boxes.get( 3 );
        assertThat( box3, is( instanceOf( UnsupportedBox.class ) ) );
        assertThat( box3.Boxes.size(), is( 0 ) );

        Box box4 = boxes.get( 4 );
        assertThat( box4, is( instanceOf( Association.class ) ) );
        List<Box> box4Boxes = box4.Boxes;
        assertThat( box4Boxes.size(), is( 2 ) );
        assertThat( box4Boxes.get( 0 ), is( instanceOf( Label.class ) ) );
        assertThat( box4Boxes.get( 0 ).Boxes.size(), is( 0 ) );
        assertThat( box4Boxes.get( 1 ), is( instanceOf( Association.class ) ) );
        List<Box> boxes4Boxes1Boxes = box4Boxes.get( 1 ).Boxes;
        assertThat( boxes4Boxes1Boxes.size(), is( 2 ) );
        assertThat( boxes4Boxes1Boxes.get( 0 ), is( instanceOf( Label.class ) ) );
        assertThat( boxes4Boxes1Boxes.get( 0 ).Boxes.size(), is( 0 ) );
        assertThat( boxes4Boxes1Boxes.get( 1 ), is( instanceOf( XMLBox.class ) ) );
        assertThat( boxes4Boxes1Boxes.get( 1 ).Boxes.size(), is( 0 ) );

        Box box5 = boxes.get( 5 );
        assertThat( box5, is( instanceOf( ContigousCodestream.class ) ) );
        assertThat( box5.Boxes.size(), is( 0 ) );
    }

}
