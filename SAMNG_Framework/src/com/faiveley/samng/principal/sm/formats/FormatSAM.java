package com.faiveley.samng.principal.sm.formats;

import java.util.List;

public enum FormatSAM{
	MULTIMEDIA,
	TOMNG,
	TOM4,
	ATESS,
	JRU,
	COMPRESSED;
	
	private FormatSAM() {
		
	}
	
	String format;
	int code;
	List <String> extensions;
	boolean enable=false;
	private FormatJRU fjru;
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public List<String> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	public FormatJRU getFjru() {
		return fjru;
	}
	public void setFjru(FormatJRU fjru) {
		this.fjru = fjru;
	}
}
