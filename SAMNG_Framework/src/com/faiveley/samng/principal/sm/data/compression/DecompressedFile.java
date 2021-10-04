package com.faiveley.samng.principal.sm.data.compression;

public class DecompressedFile {

	private String compressedFileName;

	private String decompressedFileName;
	
	private String innerFileName;
	
	public DecompressedFile(String decompressedFileName, String innerFileName, String compressedFileName) {
		this.decompressedFileName = decompressedFileName;
		this.innerFileName = innerFileName;
		this.compressedFileName = compressedFileName;
	}

	public String getDecompressedFileName() {
		return decompressedFileName;
	}

	public void setDecompressedFileName(String decompressedFileName) {
		this.decompressedFileName = decompressedFileName;
	}

	public String getInnerFileName() {
		return innerFileName;
	}

	public void setInnerFileName(String innerFileName) {
		this.innerFileName = innerFileName;
	}

	public String getCompressedFileName() {
		return compressedFileName;
	}

	public void setCompressedFileName(String compressedFileName) {
		this.compressedFileName = compressedFileName;
	}
}
