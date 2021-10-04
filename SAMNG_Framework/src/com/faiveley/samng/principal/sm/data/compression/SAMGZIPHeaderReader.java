package com.faiveley.samng.principal.sm.data.compression;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class SAMGZIPHeaderReader {
	
	 public final static int GZIP_MAGIC = 0x8b1f;
	
	private InputStream in;
	private CRC32 crc;
	
	public SAMGZIPHeaderReader(FileInputStream in) {
		this.in = in;
		this.crc = new CRC32();
	}
	
    /*
     * File header flags.
     */
    private final static int FHCRC      = 2;    // Header CRC
    private final static int FEXTRA     = 4;    // Extra field
    private final static int FNAME      = 8;    // File name
    private final static int FCOMMENT   = 16;   // File comment
	
	/*
     * Reads GZIP member header to get the optional file name.
     */
    public String getFileName() throws IOException {
    	String fileName = "";
        CheckedInputStream in = new CheckedInputStream(this.in, crc);
        crc.reset();
        // Check header magic
        if (readUShort(in) != GZIP_MAGIC) {
            throw new IOException("Not in GZIP format");
        }
        // Check compression method
        if (readUByte(in) != 8) {
            throw new IOException("Unsupported compression method");
        }
        // Read flags
        int flg = readUByte(in);
        // Skip MTIME, XFL, and OS fields
        skipBytes(in, 6);
        // Skip optional extra field
        if ((flg & FEXTRA) == FEXTRA) {
            skipBytes(in, readUShort(in));
        }
        // Get the optional file name
        if ((flg & FNAME) == FNAME) {
        	int _byte = 0;
            while ((_byte= readUByte(in)) != 0){
                 fileName += (char)_byte;
            }
        }
        // Skip optional file comment
        if ((flg & FCOMMENT) == FCOMMENT) {
            while (readUByte(in) != 0) ;
        }
        // Check optional header CRC
        if ((flg & FHCRC) == FHCRC) {
            int v = (int)crc.getValue() & 0xffff;
            if (readUShort(in) != v) {
                throw new IOException("Corrupt GZIP header");
            }
        }
        
        return fileName;
    }
    
    /*
     * Reads unsigned short in Intel byte order.
     */
    private int readUShort(InputStream in) throws IOException {
        int b = readUByte(in);
        return ((int)readUByte(in) << 8) | b;
    }
    
    /*
     * Reads unsigned byte.
     */
    private int readUByte(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new EOFException();
        }
        if (b < -1 || b > 255) {
            // Report on this.in, not argument in; see read{Header, Trailer}.
            throw new IOException(this.in.getClass().getName()
                + ".read() returned value out of range -1..255: " + b);
        }
        return b;
    }
    
    private byte[] tmpbuf = new byte[128];
    
    /*
     * Skips bytes of input data blocking until all bytes are skipped.
     * Does not assume that the input stream is capable of seeking.
     */
    private void skipBytes(InputStream in, int n) throws IOException {
        while (n > 0) {
            int len = in.read(tmpbuf, 0, n < tmpbuf.length ? n : tmpbuf.length);
            if (len == -1) {
                throw new EOFException();
            }
            n -= len;
        }
    }
    
}
