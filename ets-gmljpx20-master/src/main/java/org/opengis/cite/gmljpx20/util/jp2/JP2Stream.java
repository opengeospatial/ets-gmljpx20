package org.opengis.cite.gmljpx20.util.jp2;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JP2Stream {
    public InputStream fs;
    public List<Box> Boxes = new ArrayList<Box>();

    /*public File Jp2Stream(String Filename)
    {
        return new File(Filename);
    }*/

    public JP2Stream(InputStream source)
    {
        fs = source;
        while (true)
        {
            try
            {
                Box box = Box.FromStream(source);
                Boxes.add(box);
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
    
    /*private bool IsBoxmodinAsso(Box b, Box bmod)
    {
        foreach (Box bson in b.Boxes)
        {
            if (bson.guid == bmod.guid) return true;
            if (bson.Boxes.Count != 0)
            {
                return IsBoxmodinAsso(bson, bmod);
            }
        }
        return false;
    }

    private Box GetOnTopParent(Box b)
    {
        if (b.parent != null)
        {
            return GetOnTopParent(b.parent);
        }
        return b;
    }
    private Box GetParentOfGuid(Box b,Guid guid)
    {
        if (b.guid == guid) return b;
        if (b.parent != null)
        {
            return GetParentOfGuid(b.parent,guid);
        }
        return null;
    }*/

}
