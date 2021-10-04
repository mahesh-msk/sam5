package com.faiveley.samng.principal.sm.search;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Result {
	private static final int INDEX_FILE = 0;
	private static final int INDEX_DIR = 1;
	private static final int INDEX_SIZE = 2;
	private static final int INDEX_TYPE = 3;
	private static final int INDEX_MODIF = 4;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
	
	private String fileName;
	private String directory;
	private String type;
	private String size;
	private String modified;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public void setModified(Date modified) {
		this.modified = sdf.format(modified);
	}
	
	public void setSize(long size) {
		this.size = Long.toString(size);
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue(int index) {
		String str = null;
		switch (index) {
			case INDEX_FILE:
				str = this.fileName;
				break;
			case INDEX_DIR:
				str = this.directory;
				break;
			case INDEX_SIZE:
				str = this.size;
				break;
			case INDEX_TYPE:
				str = this.type;
				break;
			case INDEX_MODIF:
				str = this.modified;
				break;

			default:
				break;
			}
		return str;
	}
	
	public int getNoValues() {
		return INDEX_MODIF + 1;
	}
}