package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.compression.SAMGZIPHeaderReader;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;

public class DecompressionService {
	private static final int BUFFER_SIZE = 1024;
	private static final String ADD_SEPARATOR_TMP= "-tmp";
	
	private static DecompressionService INSTANCE;
	
	private String currentCompressedFileName;
	
	private DecompressionService() {}
	
	public static DecompressionService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DecompressionService();
		}
		
		return INSTANCE;
	}
	
	public DecompressedFile decompressFile(String absoluteFileName, String fileName, boolean decompressTemp) throws IOException {
		// On récupère le format intrinsèque du fichier compressé
		SAMGZIPHeaderReader headerReader = new SAMGZIPHeaderReader(new FileInputStream(absoluteFileName));
		String innerFileName = headerReader.getFileName();
		
		if (innerFileName == null) {
			innerFileName = fileName;
		}
		
		String innerFileExtension = innerFileName.substring(innerFileName.lastIndexOf('.'));
		boolean isExtensionValid = BridageFormats.getInstance().isextensionValide(innerFileExtension);
		
		if (!isExtensionValid) {
			throw new UnsupportedJourneyFileFormat();
		}
		
		File decompressedFile;
		
		if (decompressTemp) {
			decompressedFile = File.createTempFile(
					innerFileName.substring(0, innerFileName.length() - innerFileExtension.length())+ADD_SEPARATOR_TMP, 
					innerFileExtension);
		} else {
			String parent = new File(absoluteFileName).getParent();
			decompressedFile = new File(parent, innerFileName);
		}
		
		byte[] buffer = new byte[BUFFER_SIZE];
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(absoluteFileName));
		FileOutputStream out = new FileOutputStream(decompressedFile);
		
		int len;
		while ((len = gzis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		
		gzis.close();
		out.close();
		
		return new DecompressedFile(decompressedFile.getAbsolutePath(), innerFileName, absoluteFileName);
	}
	
	public boolean isCompressedFile(String fileName) {
		return BridageFormats.isCompressed(fileName);
	}
	
	public String getCurrentCompressedFileName() {
		return this.currentCompressedFileName;
	}
	
	public void setCurrentCompressedFileName(String fileName) {
		this.currentCompressedFileName = fileName;
	}
	
}
