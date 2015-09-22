package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;

public class UnsupportedBox extends Box {
	
    public byte[] Data;

    public UnsupportedBox(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
        //if (length == 0) 
        	//Data = StreamUtil.ReadToEnd(source);
        if (length == 1)
        {
        	source.skip((int)extendedLength - 16);
            //Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else 
        	source.skip((int)length - 8);
        	//Data = StreamUtil.ReadBytes(source, (int)length - 8);
    }

}
