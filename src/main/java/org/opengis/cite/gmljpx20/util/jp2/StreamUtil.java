package org.opengis.cite.gmljpx20.util.jp2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

public class StreamUtil {

	        /*static byte ReadOctet(InputStream s)
	        {
	            byte[] buffer = new byte[1];
	            if (s.read(buffer, 0, 1) < 1) throw (new Exception());
	            return buffer[0];
	        }*/
	        //
	        // Summary:
	        //     Reads count bytes from the current stream into a byte array and advances
	        //     the current position by count bytes.
	        //
	        // Parameters:
	        //   count:
	        //     The number of bytes to read.
	        //
	        // Returns:
	        //     A byte array containing data read from the underlying stream. This might
	        //     be less than the number of bytes requested if the end of the stream is reached.
	        //
	        // Exceptions:
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.ArgumentOutOfRangeException:
	        //     count is negative.
	        public static byte[] ReadBytes(InputStream s, int count) throws IOException
	        {
	            byte[] buffer = new byte[count];
	            int len = s.read(buffer, 0, count);
	            if (len < count) resizeArray(buffer, len);
	            return buffer;
	        }
	        static byte[] ReadExactBytes(InputStream s, int count) throws Exception
	        {
	            byte[] buffer = new byte[count];
	            int len = s.read(buffer, 0, count);
	            if (len < count) throw(new Exception("Expected " + count + " bytes but only read " + len + " bytes."));
	            return buffer;
	        }
	        public static byte[] ReadToEnd(InputStream s) throws IOException
	        {
	            int len;
	            int position = 0;
	            byte[] buffer = new byte[512];
	            while ((len = s.read(buffer, position, 512)) > 0)
	            {
	                position += len;
	                if (len < 512)
	                {
	                    resizeArray(buffer, position);
	                    break;
	                }
	                resizeArray(buffer, position + 512);
	            }

	            return buffer;
	        }
	        static Object resizeArray (Object oldArray, int newSize) {
	        	int oldSize = java.lang.reflect.Array.getLength(oldArray);
	        	Class elementType = oldArray.getClass().getComponentType();
	        	Object newArray = java.lang.reflect.Array.newInstance(
	        		elementType, newSize);
	        	int preserveLength = Math.min(oldSize, newSize);
	        	if (preserveLength > 0)
	        		System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
	        	return newArray; 
	        }
	         /*   
	        //
	        // Summary:
	        //     Reads a 2-byte signed integer from the current stream using big-endian
	        //     encoding and advances the current position of the stream by two bytes.
	        //
	        // Returns:
	        //     A 2-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        internal static short ReadBInt16(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 2);
	            if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToInt16(buffer, 0));
	        }*/
	        //
	        // Summary:
	        //     Reads a 4-byte signed integer from the current stream using big-endian
	        //     encoding and advances the current position of the stream by four bytes.
	        //
	        // Returns:
	        //     A 4-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        static int ReadBInt32(InputStream s) throws Exception
	        {
	            byte[] buffer = ReadExactBytes(s, 4);
	            //if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.toInt32(buffer, 0));//.ToInt32(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads an 8-byte signed integer from the current stream using big-endian 
	        //     encoding and advances the current position of the stream by eight bytes.
	        //
	        // Returns:
	        //     An 8-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        static long ReadBInt64(InputStream s) throws Exception
	        {
	            byte[] buffer = ReadExactBytes(s, 8);
	            //if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.toInt64(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads a 2-byte unsigned integer from the current stream using big-endian
	        //     encoding and advances the position of the stream by two bytes.
	        //
	        // Returns:
	        //     A 2-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        static short ReadBUInt16(InputStream s) throws Exception
	        {
	            byte[] buffer = ReadExactBytes(s, 2);
	            //if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.toInt16(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads a 4-byte unsigned integer from the current stream using big-endian
	        //     encoding and advances the position of the stream by four bytes.
	        //
	        // Returns:
	        //     A 4-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        static int ReadBUInt32(InputStream s) throws Exception
	        {
	            byte[] buffer = ReadExactBytes(s, 4);
	            //if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.toInt32(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads an 8-byte unsigned integer from the current stream using big-endian
	        //     encoding and advances the position of the stream by eight bytes.
	        //
	        // Returns:
	        //     An 8-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        static long ReadBUInt64(InputStream s) throws Exception
	        {
	            byte[] buffer = ReadExactBytes(s, 8);
	            //if (BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.toInt64(buffer, 0));
	        }


	        //
	        // Summary:
	        //     Reads a 2-byte signed integer from the current stream using little-endian
	        //     encoding and advances the current position of the stream by two bytes.
	        //
	        // Returns:
	        //     A 2-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        /*internal static short ReadInt16(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 2);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToInt16(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads a 4-byte signed integer from the current stream using little-endian
	        //     encoding and advances the current position of the stream by four bytes.
	        //
	        // Returns:
	        //     A 4-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        internal static int ReadInt32(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 4);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToInt32(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads an 8-byte signed integer from the current stream using little-endian 
	        //     encoding and advances the current position of the stream by eight bytes.
	        //
	        // Returns:
	        //     An 8-byte signed integer read from the current stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        internal static long ReadInt64(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 8);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToInt64(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads a 2-byte unsigned integer from the current stream using little-endian
	        //     encoding and advances the position of the stream by two bytes.
	        //
	        // Returns:
	        //     A 2-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        internal static ushort ReadUInt16(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 2);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToUInt16(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads a 4-byte unsigned integer from the current stream using little-endian
	        //     encoding and advances the position of the stream by four bytes.
	        //
	        // Returns:
	        //     A 4-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        internal static uint ReadUInt32(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 4);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToUInt32(buffer, 0));
	        }
	        //
	        // Summary:
	        //     Reads an 8-byte unsigned integer from the current stream using little-endian
	        //     encoding and advances the position of the stream by eight bytes.
	        //
	        // Returns:
	        //     An 8-byte unsigned integer read from this stream.
	        //
	        // Exceptions:
	        //   System.IO.EndOfStreamException:
	        //     The end of the stream is reached.
	        //
	        //   System.IO.IOException:
	        //     An I/O error occurs.
	        //
	        //   System.ObjectDisposedException:
	        //     The stream is closed.
	        internal static ulong ReadUInt64(Stream s)
	        {
	            byte[] buffer = ReadExactBytes(s, 8);
	            if (!BitConverter.IsLittleEndian) Array.Reverse(buffer);
	            return (BitConverter.ToUInt64(buffer, 0));
	        }
	    }
	}*/
}
