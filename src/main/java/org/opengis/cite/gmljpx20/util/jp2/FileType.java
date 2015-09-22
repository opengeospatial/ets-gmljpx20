package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileType extends Box {
	
    private byte[] _Data;
    public String fileTypeData = "";
    //public List<Box> boxes = new ArrayList<Box>();
    
    public FileType(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
    	byte[] _DataTemp = null;
        //this.start = start;
        this.lengthfinal = length - 8;
        //this.type = type;
        //this.extendedLength = extendedLength;

        if (length == 0) _Data = StreamUtil.ReadToEnd(source);
        else if (length == 1)
        {
            _Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else { 
        	_Data = StreamUtil.ReadBytes(source, (int)length - 8);
        	int contador = 0;
        	_DataTemp = new byte[_Data.length];
        	for(int a = 0; a < _Data.length; a++){
        		if (_Data[a] != 0) {
        			_DataTemp[contador] = _Data[a];
        			contador ++;
        		}
        	}
        }
        fileTypeData = new String(_DataTemp, Charset.forName("UTF8"));// convertCString(_Data, System.Text.Encoding.UTF8);
    }

}
