package org.opengis.cite.gmljpx20.util.jp2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Box {

    public enum BoxTypes {
        // JP2 Box Types
        jp00( 0x6A502020 ), // File Signature
        ftyp( 0x66747970 ), // File Type
        jp2h( 0x6A703268 ), // JP2 Header
        ihdr( 0x69686472 ), // Image Header
        bpcc( 0x62706363 ), // Bits Per Component
        colr( 0x636F6C72 ), // Color Specification
        pclr( 0x70636C72 ), // Palette
        cmap( 0x636D6170 ), // Component Mapping
        cdef( 0x63646566 ), // Channel Definition
        res0( 0x72657320 ), // Resolution
        resc( 0x72657363 ), // Capture Resolution
        resd( 0x72657364 ), // Default Display Resolution
        jp2c( 0x6A703263 ), // Contiguous Codestream
        jp2i( 0x6A703269 ), // Intellectual Property
        xml0( 0x786D6C20 ), // XML
        uuid( 0x75756964 ), // UUID
        uinf( 0x75696E66 ), // UUID Info
        ulst( 0x75637374 ), // UUID List
        url0( 0x75726C20 ), // URL

        // JPX Box Types
        asoc( 0x61736F63 ), // Association
        bfil( 0x6266696C ), // Binary Filter
        cgrp( 0x63677270 ), // Color Group
        chck( 0x6368636B ), // Digital Signature
        comp( 0x636F6D70 ), // Composition
        copt( 0x636F7074 ), // Composition Options
        cref( 0x63726566 ), // Cross-Reference
        creg( 0x63726567 ), // Codestream Registration
        drep( 0x64726570 ), // Desired Reproductions
        dtlb( 0x6474626C ), // Data Reference
        flst( 0x666C7374 ), // Fragment List
        free( 0x66726565 ), // Free
        ftbl( 0x6674626C ), // Fragment Table
        gtso( 0x6774736F ), // Graphics Technology Standard Output
        inst( 0x696E7374 ), // Instruction Set
        jpch( 0x6A706368 ), // Codestream Header
        jplh( 0x6A706C68 ), // Compositing Layer Header
        lbl0( 0x6C626C20 ), // Label
        mdat( 0x6D646174 ), // Media Data
        mp7b( 0x6D703762 ), // MPEG-7 Binary
        nlst( 0x6E6C7374 ), // Number List
        opct( 0x6F706374 ), // Opacity
        roid( 0x726F6964 ), // ROI Description
        rreq( 0x72726571 ); // Resource Requirements

        private long value;

        BoxTypes( long value ) {
            this.value = value;
        }

    }

    protected long length;

    protected long extendedLength;

    protected List<Box> boxes = new ArrayList<>();

    public Box( InputStream source, long length, long extendedLength ) {
        this.length = length;
        this.extendedLength = extendedLength;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public static Box fromStream( InputStream source )
                            throws Exception {
        long length = StreamUtil.readBUInt32( source );
        long extendedLength = 0;
        if ( length == 1 ) {
            extendedLength = StreamUtil.readBUInt64( source );
        }

        int type = StreamUtil.readBInt32( source );
        switch ( type ) {
        case 0x6A703263:// contiguous codestream:
            return new ContigousCodestream( source, (int) length, extendedLength );
        case 0x66747970:// File Type:
            return new FileType( source, (int) length, extendedLength );
        case 0x61736F63:// BoxTypes.asoc:
            return new Association( source, (int) length, extendedLength );
        case 0x786D6C20:// BoxTypes.xml0:
            return new XMLBox( source, (int) length, extendedLength );
        case 0x6C626C20:
            return new Label( source, (int) length, extendedLength );
        case 0x72726571:// Resource Requirements (RREQ):
            return new ResourceRequirements( source, (int) length, extendedLength );
        default:
            return new UnsupportedBox( source, (int) length, extendedLength );
        }
    }

}
