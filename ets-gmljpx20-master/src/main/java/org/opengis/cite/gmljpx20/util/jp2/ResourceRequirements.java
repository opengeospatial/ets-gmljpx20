package org.opengis.cite.gmljpx20.util.jp2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ResourceRequirements extends Box {
	
    private byte[] _Data;
    public byte[] rreqData;
    
    public ResourceRequirements(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
    	byte[] _DataTemp = null;
        this.lengthfinal = length - 8;
        int position = 0;

        if (length == 0) _Data = StreamUtil.ReadToEnd(source);
        else if (length == 1)
        {
            _Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else { 
        	_Data = StreamUtil.ReadBytes(source, (int)length - 8);

        	/*_DataTemp = new byte[_Data.length];
        	if (_Data[0] != 0){
        		int maskLength = _Data[position];
        		position += maskLength;
        		int fuam = 0;
        		for (int a = 0; a < maskLength; a++){
        			fuam += _Data[position + a];
        			position ++;
        		}
        		int dcm = 0;
        		for (int a = 0; a < maskLength; a++){
        			dcm += _Data[position + a];
        			position ++;
        		}
        		int nsf = _Data[position] + _Data[position + 1];
        		position = position + 2;
        		
        		int[] sfi = new int[nsf];
        		int[] smi = new int[nsf];
        		
        		for (int a = 0; a < nsf; a++){
            		sfi[a] = _Data[position] + _Data[position + 1];
            		position = position + 2;
            		for (int b = 0; b < maskLength; b++){
            			smi[a] += _Data[position + b];
            			position ++;
            		}
        		}
        		
        	}*/
        }
        rreqData = _Data;
    }

}
