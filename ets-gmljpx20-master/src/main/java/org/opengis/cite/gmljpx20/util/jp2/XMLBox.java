package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;

import java.io.InputStream;
import java.nio.charset.Charset;

public class XMLBox extends Box
{
    private byte[] _Data;
    private byte[] value;

    public byte[] Data(byte[] _Data)
    {
    	//this.value = _Data;
        return _Data;
    }
    /*public String DataXML
    {
        get
        {
            return xmldata;
        }
        set
        {
            if (value != "")
            {
               
                uint size = (uint)Encoding.UTF8.GetBytes(value).Length + 8;

                uint sizeaux = Length;
                Length = size;
                this.xmldata = value;
                _Data = System.Text.Encoding.UTF8.GetBytes(this.xmldata);
                Box b = this;
                while (b.parent != null)
                {
                    uint sizeloop = b.parent.Length;
                    b.parent.Length = (uint)(b.parent.Length - sizeaux + size);
                    sizeaux = sizeloop;
                    size = b.parent.Length;

                    b = b.parent;

                }
            }
            
        }
        
    }*/
    
    public String xmldata = "";

    public long start = 0;
    public int lengthfinal = 0;
    public BoxTypes type;
    public long extendedLength;

    //public XMLBox(InputStream source, long start, int length, BoxTypes type, long extendedLength) throws IOException
    public XMLBox(InputStream source, int length, long extendedLength) throws IOException
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

    /*private static String convertCString(byte[] buffer, Encoding targetEncoding)
    {
        int lengthaux = 0;
        int realMax = buffer.length;
        if (buffer[realMax - 1] == 0)
        {
            for (; 0 != buffer[lengthaux]; ++lengthaux)
            {
                if (lengthaux + 1 >= realMax) break;
            }
        }
        else lengthaux = realMax;

        return targetEncoding.GetString(buffer, 0, lengthaux);
    }

    public string GetValueOfXPath(string xpath)
    {
        try
        {
            byte[] byteArray = Encoding.UTF8.GetBytes(xmldata);
            System.IO.MemoryStream stream = new System.IO.MemoryStream(byteArray);
            System.Xml.XmlDocument document = new System.Xml.XmlDataDocument();
            document.Load(stream);
            System.Xml.XmlNode root = document.DocumentElement;
            System.Xml.XmlNodeList nodeList;


            nodeList = root.SelectNodes(@"//namespace::*");
            System.Xml.XmlNamespaceManager nsmgr = new System.Xml.XmlNamespaceManager(document.NameTable);
            foreach (XmlNode node in nodeList)
            {
                if (node.Prefix == "xmlns")
                {
                    nsmgr.AddNamespace(node.LocalName, node.Value);


                }
                if (node.Name == "xmlns")
                {
                    nsmgr.AddNamespace("ns", node.Value);
                }
            }
            System.Xml.XmlNode nodename = root.SelectSingleNode(xpath, nsmgr);
            if (nodename != null)
            {

                return nodename.InnerText;
            }
            return null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public void SetValueOfXPath(string xpath,string value)
    {
       
            byte[] byteArray = Encoding.UTF8.GetBytes(xmldata);
            System.IO.MemoryStream stream = new System.IO.MemoryStream(byteArray);
            System.Xml.XmlDocument document = new System.Xml.XmlDataDocument();
            document.Load(stream);
            System.Xml.XmlNode root = document.DocumentElement;
            System.Xml.XmlNodeList nodeList;


            nodeList = root.SelectNodes(@"//namespace::*");
            System.Xml.XmlNamespaceManager nsmgr = new System.Xml.XmlNamespaceManager(document.NameTable);
            foreach (XmlNode node in nodeList)
            {
                if (node.Prefix == "xmlns")
                {
                    nsmgr.AddNamespace(node.LocalName, node.Value);


                }
                if (node.Name == "xmlns")
                {
                    nsmgr.AddNamespace("ns", node.Value);
                }
            }
            System.Xml.XmlNode nodename = root.SelectSingleNode(xpath, nsmgr);
            if (nodename != null)
            {
                nodename.InnerText = value;
                //return nodename.InnerText;
            }
            DataXML = document.InnerXml;
            
       
    }*/


}

