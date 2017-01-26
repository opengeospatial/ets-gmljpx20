package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Association  extends Box {
	
    public byte[] Data;
    //public List<Box> boxes = new ArrayList<Box>();
    
    public Association(InputStream source, int length, long extendedLength) throws IOException
    {
    	super(source, length, extendedLength);
        /*if (length == 0) 
        	Data = StreamUtil.ReadToEnd(source);
        else if (length == 1)
        {
            Data = StreamUtil.ReadBytes(source, (int)extendedLength - 16);
        }
        else 
        	Data = StreamUtil.ReadBytes(source, (int)length - 8);*/
        while (true)
        {
            try
            {
                Box box = Box.FromStream(source);
                Boxes.add(box);
                int auxLenght = 0;
                
                for (int i = 0; i < Boxes.size() ; i++) {
                	Box auxBox = Boxes.get(i);
                	auxLenght+= auxBox.Length;
				}
                if (auxLenght == length - 8)
                	return;
            }
            catch(Exception e){
                // if any error occurs
            	return;
             }/* finally {
                
                fs.close();
             } (EndOfStreamException)
            {
                if(DEBUG.Jp2File) Debug.WriteLine("End of File");
                break;
            }*/
        }
   	
    }

}
