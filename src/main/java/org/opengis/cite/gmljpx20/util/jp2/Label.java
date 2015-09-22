package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.opengis.cite.gmljpx20.util.jp2.Box.BoxTypes;

public class Label extends Box {
    private byte[] _Data;
    private byte[] value;

    public byte[] Data(byte[] _Data)
    {
    	//this.value = _Data;
        return _Data;
    }
    
    public String xmldata = "";

    public long start = 0;
    public int lengthfinal = 0;
    public BoxTypes type;
    public long extendedLength;

    //public XMLBox(InputStream source, long start, int length, BoxTypes type, long extendedLength) throws IOException
    public Label(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
        //this.start = start;
        this.lengthfinal = length - 8;
        //this.type = type;
        this.extendedLength = extendedLength;

        if (length == 0) _Data = StreamUtil.ReadToEnd(source);
        else if (length == 1)
        {
            _Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else _Data = StreamUtil.ReadBytes(source, (int)length - 8);

        xmldata = new String(_Data, Charset.forName("UTF8"));// convertCString(_Data, System.Text.Encoding.UTF8);
        
    }

}
