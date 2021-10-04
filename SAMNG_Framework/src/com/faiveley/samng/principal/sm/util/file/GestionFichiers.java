package com.faiveley.samng.principal.sm.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class GestionFichiers {

	public static void copierRepertoire(File f1,File f2){
		if (f1.isDirectory()) {
			File[] f1files = f1.listFiles();
			for (File f1file : f1files) {
				if (!f1file.getName().equals(".svn")) {
					f2.mkdir();
					File f2file=new File(f2.getPath()+"/"+f1file.getName());
					copierRepertoire(f1file, f2file);
				}
			}
		}else{
			try {
				f2.createNewFile();
				copyFile2(f1, f2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void copyFile2(File in, File out) {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(in).getChannel();
			destinationChannel = new FileOutputStream(out).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// or destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			try {
				sourceChannel.close();
				destinationChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void copyFile(File in, File out) {
		try {
			FileInputStream fis  = new FileInputStream(in);
			out.createNewFile();
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i=fis.read(buf))!=-1) {
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
