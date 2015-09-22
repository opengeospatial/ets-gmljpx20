package org.opengis.cite.gmljpx20.util.jp2;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.InputStream;
import java.io.DataInputStream;

public class Box {
	
    public enum BoxTypes
    {
        // JP2 Box Types
        jp00(0x6A502020),    // File Signature
        ftyp(0x66747970),    // File Type
        jp2h(0x6A703268),    // JP2 Header
        ihdr(0x69686472),    // Image Header
        bpcc(0x62706363),    // Bits Per Component
        colr(0x636F6C72),    // Color Specification
        pclr(0x70636C72),    // Palette
        cmap(0x636D6170),    // Component Mapping
        cdef(0x63646566),    // Channel Definition
        res0(0x72657320),    // Resolution
        resc(0x72657363),    // Capture Resolution
        resd(0x72657364),    // Default Display Resolution
        jp2c(0x6A703263),    // Contiguous Codestream
        jp2i(0x6A703269),    // Intellectual Property
        xml0(0x786D6C20),    // XML
        uuid(0x75756964),    // UUID
        uinf(0x75696E66),    // UUID Info
        ulst(0x75637374),    // UUID List
        url0(0x75726C20),    // URL

        // JPX Box Types
        asoc(0x61736F63),    // Association
        bfil(0x6266696C),    // Binary Filter
        cgrp(0x63677270),    // Color Group
        chck(0x6368636B),    // Digital Signature
        comp(0x636F6D70),    // Composition
        copt(0x636F7074),    // Composition Options
        cref(0x63726566),    // Cross-Reference
        creg(0x63726567),    // Codestream Registration
        drep(0x64726570),    // Desired Reproductions
        dtlb(0x6474626C),    // Data Reference
        flst(0x666C7374),    // Fragment List
        free(0x66726565),    // Free
        ftbl(0x6674626C),    // Fragment Table
        gtso(0x6774736F),    // Graphics Technology Standard Output
        inst(0x696E7374),    // Instruction Set
        jpch(0x6A706368),    // Codestream Header
        jplh(0x6A706C68),    // Compositing Layer Header
        lbl0(0x6C626C20),    // Label
        mdat(0x6D646174),    // Media Data
        mp7b(0x6D703762),    // MPEG-7 Binary
        nlst(0x6E6C7374),    // Number List
        opct(0x6F706374),    // Opacity
        roid(0x726F6964),    // ROI Description
        rreq(0x72726571);    // Resource Requirements
       
        private long value;

        private BoxTypes(long value) {
                this.value = value;
        }

	    /*public String GetDescription(Box.BoxTypes valor)
	    {
	            switch (valor)
	            {
	                case jp00: return "File Signature";
	                case ftyp: return "File Type";
	                case jp2h: return "JP2 Header";
	                case ihdr: return "Image Header";
	                case bpcc: return "Bits Per Component";
	                case colr: return "Color Specification";
	                case pclr: return "Palette";
	                case cmap: return "Component Mapping";
	                case cdef: return "Channel Definition";
	                case res0: return "Resolution";
	                case resc: return "Capture Resolution";
	                case resd: return "Default Display Resolution";
	                case jp2c: return "Contiguous Codestream";
	                case jp2i: return "Intellectual Property";
	                case xml0: return "XML";
	                case uuid: return "UUID";
	                case uinf: return "UUID Info";
	                case ulst: return "UUID List";
	                case url0: return "URL";
	                     // JPX Box Types
	                case asoc: return "Association";
	                case bfil: return "Binary Filter";
	                case cgrp: return "Color Group";
	                case chck: return "Digital Signature";
	                case comp: return "Composition";
	                case copt: return "Composition Options";
	                case cref: return "Cross-Reference";
	                case creg: return "Codestream Registration";
	                case drep: return "Desired Reproductions";
	                case dtlb: return "Data Reference";
	                case flst: return "Fragment List";
	                case free: return "Free";
	                case ftbl: return "Fragment Table";
	                case gtso: return "Graphics Technology Standard Output";
	                case inst: return "Instruction Set";
	                case jpch: return "Codestream Header";
	                case jplh: return "Compositing Layer Header";
	                case lbl0: return "Label";
	                case mdat: return "Media Data";
	                case mp7b: return "MPEG-7 Binary";
	                case nlst: return "Number List"; 
	                case opct: return "Opacity";
	                case roid: return "ROI Description";
	                case rreq: return "Rersource Requirements";
	       
	        
	     
	            }
	            return "";
	        }*/
	    }

    public long Start;
    public long ContentStart;

    public long Length;
    public long LengthOri;

    public long lengthfinal = 0;
    public long ExtendedLength;
    public BoxTypes Type;
    
    public Box parent = null;

    public List<AbstractMap.SimpleEntry<String, String>> lstvalues = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
    
    public UUID guid = UUID.randomUUID();
    
    public List<Box> Boxes = new ArrayList<Box>();
    
    public Object Clone()
    {
        
        Box b = (Box)this.Clone();
        b.Start = -1;
        if (this.parent != null)
        {
            b.parent = (Box)this.parent.Clone();
        }
        return b;
    }
    
    public Box(InputStream source, int length, long extendedLength)
    {
        //Start = start;
        Length = length;
        LengthOri = length;
        //Type = type;
        ExtendedLength = extendedLength;
        //ContentStart = source. ;//.Position;
        /*if (length == 0)
        {
            this.lengthfinal = (long)(source. .Length - start - 8);


        }
        else if (length == 1) this.lengthfinal=length;
        else this.lengthfinal = Length - 8;*/
    }

    public static Box FromStream(InputStream source) throws Exception
    {
    	
        //long start = source.Position;
        long length = StreamUtil.ReadBUInt32(source);// .ReadBUInt32(source);
        //return FromStream(source, start, length);
        return FromStream(source, length);
    }

    //public static Box FromStream(InputStream source, long start, long length)
    public static Box FromStream(InputStream source, long length) throws Exception
    {
        long extendedLength = 0;
        int type = StreamUtil.ReadBUInt32(source);
        if (length == 1)
        {
            extendedLength = StreamUtil.ReadBUInt64(source);
        }

        //if (DEBUG.Jp2File) Debug.WriteLine("Box " + type.ToString() + " Length " + length + " Extended Length " + extendedLength);
        switch (type)
        {
            /*case 0x6A502020:// BoxTypes.jp00:
                return new jp00(source, start, length, type, extendedLength);
            case BoxTypes.ftyp:
                return new ftyp(source, start, length, type, extendedLength);
            case BoxTypes.jp2h:
                return new jp2h(source, start, length, type, extendedLength);*/
            case 0x6A703263://contiguous codestream:
                Box e = new ContigousCodestream(source, (int)length, extendedLength);
                return e;
            case 0x66747970://File Type:
                Box d = new FileType(source, (int)length, extendedLength);
                return d;
            case 0x61736F63://BoxTypes.asoc:
                Box c = new Association(source, (int)length, extendedLength);
                return c;
            case 0x786D6C20://BoxTypes.xml0:
                //return new XMLBox(source, start, length, type, extendedLength);
            	Box b = new XMLBox(source, (int)length, extendedLength);
                return b;
            /*case BoxTypes.ihdr:
                return new ihdr(source, start, length, type, extendedLength);*/
            case 0x6C626C20:
                return new Label(source, (int)length, extendedLength);
            /*case BoxTypes.uuid:
                return new UUidBox(source, start, length, type, extendedLength);*/
            case 0x72726571://Resource Requirements (RREQ):
                Box f = new ResourceRequirements(source, (int)length, extendedLength);
                return f;
            default:
                {
                	return new UnsupportedBox(source, (int)length, extendedLength);
                }
        }
    }
    

}
