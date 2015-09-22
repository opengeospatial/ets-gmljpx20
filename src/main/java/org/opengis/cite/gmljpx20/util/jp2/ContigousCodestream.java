package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ContigousCodestream extends Box {
	
    private byte[] _Data;
    public int[] ContigousCodestreamData;
    //public List<Box> boxes = new ArrayList<Box>();
    
    public ContigousCodestream(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
    	int[] _DataTemp = null;

        this.lengthfinal = length - 8;
        int position = 0;


        if (length == 0) _Data = StreamUtil.ReadToEnd(source);
        else if (length == 1)
        {
            _Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else { 
        	_Data = StreamUtil.ReadBytes(source, (int)length - 8);

        	_DataTemp = new int[2];
        	if (_Data[0] != 0){
        		//int maskLength = _Data[position];
        		//position += maskLength;
        		int SOC = _Data[position] + _Data[position + 1];
        		position = position + 2;
        		int SIZ = _Data[position] + _Data[position + 1];
        		position = position + 2;
        		int LSIZ = _Data[position] + _Data[position + 1];
        		position = position + 2;
        		int RSIZ = _Data[position] + _Data[position + 1];
        		position = position + 2;
        		
        		String XSIZ = "";
        		for (int a = 0; a < 4; a++){
        			String hex = Integer.toHexString(_Data[position]);
        			try {
	        			if (Integer.parseInt(hex) == 0)
	        				hex = "00";
        			} catch (NumberFormatException ex) {
        				
        			}
        			XSIZ += hex;
        			position ++;
        		}
        		int XSIZhex = Integer.parseInt(XSIZ.toUpperCase(),16);
        		_DataTemp[0] = XSIZhex;
        		
        		String YSIZ = "";
        		for (int a = 0; a < 4; a++){
        			String hex = Integer.toHexString(_Data[position]);
        			if (hex.length() > 2)
        				hex = hex.substring(hex.length() - 2, hex.length());
        			try {
	        			if (Integer.parseInt(hex) == 0)
	        				hex = "00";
        			} catch (NumberFormatException ex) {
        				
        			}
        			YSIZ += hex;
        			position ++;
        		}
        		int YSIZhex = Integer.parseInt(YSIZ.toUpperCase(),16);
        		_DataTemp[1] = YSIZhex;
       		
        	}
        }
        ContigousCodestreamData = _DataTemp;
    }

}
