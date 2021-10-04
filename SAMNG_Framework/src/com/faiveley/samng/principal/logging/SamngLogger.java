package com.faiveley.samng.principal.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class SamngLogger {

	private static Logger logger;

	private SamngLogger() {}

	public static Logger getLogger() {
		if (logger == null) {
			if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) {
				logger = Logger.getLogger(ParseurParcoursJRU.class);
			} else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
				logger = Logger.getLogger(ParseurParcoursAtess.class);
			} else {
				logger = Logger.getLogger(ParseurParcoursSamng.class);
			}
			
			FileAppender appender = null;

			try {
				File file = new File(RepertoiresAdresses.logs_parser_log_TXT);
				
				if (!file.exists()) {
					file.createNewFile();
				}
				
				appender = new FileAppender(new PatternLayout(), file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (logger == null) {
				logger = Logger.getLogger(ParseurParcoursBinaire.class);
			}
			
			logger.addAppender(appender);
			logger.setLevel(Level.DEBUG);
		}
		
		return logger;
	}

	public static void emptyLogFile() {
		File rep = new File(RepertoiresAdresses.logs);
		
		if (!rep.exists()) {
			rep.mkdir();
		}
		
		File file = new File(RepertoiresAdresses.logs_parser_log_TXT);
		
		if (file.exists()) {
			try {
				RandomAccessFile raf = new RandomAccessFile(file, "rws");
				raf.setLength(0);
			} catch (FileNotFoundException fnfe) {
			} catch (IOException ioe) {
				System.err.println("Cannot truncate file to 0 length");
			}
		}
	}
}
