package com.faiveley.samng.vueexplorateur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class WorkspaceService {

	public static WorkspaceService instance = new WorkspaceService();
	
	public static String WORKSPACE_SEP = ";";
	
	private WorkspaceService() {
		// Singleton
	}
	
	public String[] getWorkspaceDirectories() {
		String[] workspaces;
		
		String defaultdir = getDefaultWorkspaceDirectory();
		String workspacePropValue = null;
		FileInputStream inStream = null;
		try {
			String cheminFichiermissions_PROPERTIES = RepertoiresAdresses.missions_PROPERTIES;
			inStream = new FileInputStream(cheminFichiermissions_PROPERTIES);
			
			Properties props = new Properties();			
			props.load(inStream);
			
			if (props != null && props.get("workspace") != null) {
				workspacePropValue = (String) props.get("workspace");
				
				if (workspacePropValue.equals("defaut") || workspacePropValue.equals("") 
						|| new File(workspacePropValue).isHidden()) {
					workspacePropValue=defaultdir;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			workspacePropValue=defaultdir;
		}
		finally {
			if (workspacePropValue == null) {
				workspacePropValue = defaultdir;
			}
			workspaces = workspacePropValue.split(WORKSPACE_SEP);
		}		
		
		return workspaces;
	}
	
	public void updateWorkspace(String workspace){
		Properties p = new Properties();
		InputStream inputStream;
		OutputStream outputStream;
		
		try {
			inputStream = new FileInputStream(RepertoiresAdresses.missions_PROPERTIES);
			p.load(inputStream);
			inputStream.close();
						
			int max_msg_jru=ParseurParcoursJRU.getMaxMessagesJRU();
			int max_open_file_size = 2500000;
			String max_open_file_size_str =(String)p.get("max_open_file_size"); //$NON-NLS-1$
			if (max_open_file_size_str!=null) {
				max_open_file_size = Integer.valueOf(max_open_file_size_str);
			}
			
			int max_msg_tomng = ParseurParcoursBinaire.getMaxMessages();
			
			p.setProperty("max_messages_mission_tomng", max_msg_tomng+"");
			p.setProperty("workspace", workspace);
			p.setProperty("max_messages_mission_jru", max_msg_jru+"");
			p.setProperty("max_open_file_size", max_open_file_size+"");
			
			outputStream = new FileOutputStream(RepertoiresAdresses.missions_PROPERTIES);
	
			p.store(outputStream, null);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getDefaultWorkspaceDirectory() {
		return System.getProperty("user.home") + File.separator + "Documents";
	}
}
