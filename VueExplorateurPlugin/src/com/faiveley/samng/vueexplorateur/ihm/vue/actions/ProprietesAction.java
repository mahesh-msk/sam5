package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.faiveley.samng.principal.sm.data.compression.SAMGZIPHeaderReader;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;

public class ProprietesAction {

	public static void afficherProprietes(TreeSelection selection, TreeViewer viewer){
		Object ob=selection.getFirstElement();
		if (ob instanceof TreeFile) {
			String absolutename=((TreeFile) selection.getFirstElement()).getAbsoluteName();
			File f=new File(absolutename);
			String size=f!=null ? (f.length()+" "+Messages.getString("ProprietesAction_0")) : Messages.getString("ProprietesAction_1");
			
			String content = Messages.getString("ProprietesAction_3")+" : "+absolutename+" \n\n"+Messages.getString("ProprietesAction_6")+" : "+size; 
			if (BridageFormats.getFormat(absolutename) == FormatSAM.COMPRESSED) {
				content += "\n\n" + Messages.getString("ProprietesAction_9") + " : ";
				try {
					content += getFileName(absolutename);
				} catch (IOException e) {
					content += Messages.getString("ProprietesAction_10");
				} finally {
					content += "\n\n";
				}
				
				content += Messages.getString("ProprietesAction_11") + " : ";
				try {
					long decompressedFileLength = getDecompressedFileLength(absolutename);
					content += decompressedFileLength + " " + Messages.getString("ProprietesAction_0");
				} catch (IOException e) {
					content += Messages.getString("ProprietesAction_12");
				}
			}
			
			MessageDialog.openInformation(viewer.getControl().getShell(),Messages.getString("ProprietesAction_2"), 
					content); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-5$
		}else if (ob instanceof TreeFolder) {
			String absolutename=((TreeFolder) selection.getFirstElement()).getAbsoluteName();
			MessageDialog.openInformation(viewer.getControl().getShell(),Messages.getString("ProprietesAction_8"), 
					Messages.getString("ProprietesAction_3")+" : "+absolutename); //$NON-NLS-1$ //$NON-NLS-2$
		}else if (ob instanceof TreeRepository) {
			String absolutename=((TreeRepository) selection.getFirstElement()).getAbsoluteName();
			MessageDialog.openInformation(viewer.getControl().getShell(),Messages.getString("ProprietesAction_8"),  //$NON-NLS-1$
					Messages.getString("ProprietesAction_3")+" : "+absolutename); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private static String getFileName(String absoluteFileName) throws IOException {
		SAMGZIPHeaderReader headerReader = new SAMGZIPHeaderReader(new FileInputStream(absoluteFileName));
		return headerReader.getFileName();
	}
	
	private static long getDecompressedFileLength(String absoluteFileName) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(absoluteFileName, "r");
		raf.seek(raf.length() - 4);
		byte[] bytes = new byte[4];
		raf.read(bytes);
		long fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		if (fileSize < 0)
		  fileSize += (1L << 32);
		raf.close();
		return fileSize;
	}
}
