package com.faiveley.samng.principal.sm.data.multimedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.gagravarr.opus.OpusFile;
import org.gagravarr.opus.OpusStatistics;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;

public class MultimediaFile {
	private static final String OPUS_FORMAT_EXTENSION = "opus";
	public static final long MILLISECONDS_IN_ONE_SECOND = 1000;
	public static final long SECONDS_IN_ONE_MINUTE = 60;
	public static final long MINUTES_IN_ONE_HOUR = 60;

	private File file = null;
	private Message msg = null;
	private Integer indexInParcoursList = null;
	private double duration = 0;
	private String durationToString = null;
	private boolean playable = true;
	
	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

	public MultimediaFile(File file, Message msg, Integer indexInParcoursList) {
		this.file = file;
		this.msg = msg;
		this.indexInParcoursList = indexInParcoursList;
		
		calculateDuration();
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public double getDuration() {
		return duration;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}

	public Long getAbsoluteTime() {
		return msg != null ? msg.getAbsoluteTime() : null;
	}

	public String getDurationToString() {
		return durationToString;
	}

	public void setDurationToString(String durationToString) {
		this.durationToString = durationToString;
	}
	
	public Message getMessage() {
		return this.msg;
	}
	
	public int getIndexInParcoursList() {
		return indexInParcoursList;
	}

	public void setIndexInParcoursList(int indexInParcoursList) {
		this.indexInParcoursList = indexInParcoursList;
	}

	public void calculateDuration() {
		String fileName = file.getName();
		String[] fileNameParts = fileName.split(File.separator + ".");
		String format = fileNameParts[fileNameParts.length - 1].toLowerCase();
				
		if (format.startsWith(OPUS_FORMAT_EXTENSION)) {
			try {
				OpusFile opusFile = new OpusFile(file);
				OpusStatistics stats = new OpusStatistics(opusFile);
		        stats.calculate();			        
				this.duration = stats.getDurationSeconds();
				this.durationToString = durationtoString((long) (this.duration * MILLISECONDS_IN_ONE_SECOND));
			} catch (FileNotFoundException e) {
				this.setPlayable(false);
				e.printStackTrace();
			} catch (IOException e) {
				this.setPlayable(false);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				this.setPlayable(false);
				e.printStackTrace();
			}
		}
	}
	
	public static String durationtoString(long duration) {
		if (duration == -1) {
			duration = 0;
		}

		long hours = TimeUnit.MILLISECONDS.toHours(duration);
    	long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toSeconds(hours);
    	long seconds = Math.round((float) duration / (float) MILLISECONDS_IN_ONE_SECOND) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);
    	
    	if (seconds == SECONDS_IN_ONE_MINUTE) {
    		seconds = 0;
    		minutes++;
    	}

    	if (minutes == MINUTES_IN_ONE_HOUR) {
    		minutes = 0;
    		hours++;
    	}
    	
    	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
