package com.faiveley.samng.vueexplorateur.ihm.vue.viewprovider;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.faiveley.samng.ActivatorVueExplorateur;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vueexplorateur.CommunicationFichiersVue;
import com.faiveley.samng.vueexplorateur.ihm.vue.Messages;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMensuel;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMission;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeSegment;

public class ViewCellLabelProvider extends CellLabelProvider {

	
	private static final String IMG_SEGMENT = "/icons/segment.png";
	private static final String IMG_MISSION = "/icons/mission.png";
	private static final String IMG_MENSUEL = "/icons/mensuel.png";
	private static final String IMG_COMPRESSED_FILE = "/icons/file_extension_gz.png";
	private static final String IMG_MULTIMEDIA = "/icons/multimedia.png";
	private static final String IMG_VIDE = "/icons/explored.png";
	private static final String IMG_PLEIN = "/icons/unexplored.png";
	private static final String IMG_OBJ_FOLDER = "/icons/IMG_OBJ_FOLDER.jpg";
	private static final String IMG_OBJ_PROJECT = "/icons/IMG_OBJ_PROJECT.jpg";

	public ViewCellLabelProvider() {
		imagesInitialize();
	}
	
	// Must use an image registry to manage images 
	private ImageRegistry imgReg; 

	public void imagesInitialize() {
		imgReg = new ImageRegistry();
		Bundle b = FrameworkUtil.getBundle(getClass());
		
		imgReg.put(IMG_OBJ_PROJECT, ImageDescriptor.createFromURL(b.getEntry(IMG_OBJ_PROJECT)));
		imgReg.put(IMG_OBJ_FOLDER, ImageDescriptor.createFromURL(b.getEntry(IMG_OBJ_FOLDER)));
		imgReg.put(IMG_PLEIN, ImageDescriptor.createFromURL(b.getEntry(IMG_PLEIN)));
		imgReg.put(IMG_VIDE, ImageDescriptor.createFromURL(b.getEntry(IMG_VIDE)));
		imgReg.put(IMG_MULTIMEDIA, ImageDescriptor.createFromURL(b.getEntry(IMG_MULTIMEDIA)));
		imgReg.put(IMG_COMPRESSED_FILE, ImageDescriptor.createFromURL(b.getEntry(IMG_COMPRESSED_FILE)));
		imgReg.put(IMG_MENSUEL, ImageDescriptor.createFromURL(b.getEntry(IMG_MENSUEL)));
		imgReg.put(IMG_MISSION, ImageDescriptor.createFromURL(b.getEntry(IMG_MISSION)));
		imgReg.put(IMG_SEGMENT, ImageDescriptor.createFromURL(b.getEntry(IMG_SEGMENT)));
		
	}
	
	@Override
	public String getToolTipText(Object element) {
		String retour = null;
		if (element instanceof TreeMission){
			TypeMission tm = ((TreeMission) element).getMission();
			XMLGregorianCalendar d = tm.getDateDebut();
			XMLGregorianCalendar f = tm.getDateFin();
			
			String deb =  String.format("%02d%s%02d%s%4d %02d:%02d", d.getDay(), CommunicationFichiersVue.SEPARATEUR, 
					d.getMonth(), CommunicationFichiersVue.SEPARATEUR, d.getYear(), d.getHour(), d.getMinute());
			String fin =  String.format("%02d%s%02d%s%4d %02d:%02d", f.getDay(), CommunicationFichiersVue.SEPARATEUR, 
					f.getMonth(), CommunicationFichiersVue.SEPARATEUR, f.getYear(), f.getHour(), f.getMinute());
			
//			retour = "De " + deb + " à " + fin;
			retour = Messages.getString("SampleView_65") + "\u0020" + deb + "\u0020" +Messages.getString("SampleView_66") + "\u0020" + fin;

		}
		
		return retour;
	}

	@Override
	public void update(ViewerCell cell) {
		cell.setText(cell.getElement().toString());
		cell.setImage(getImage(cell.getElement()));
	}

	public Image getImage(Object obj) {
		if (obj instanceof TreeRepository)
			return imgReg.get(IMG_OBJ_PROJECT);
		if (obj instanceof TreeFolder)
			return imgReg.get(IMG_OBJ_FOLDER);
		if (obj instanceof TreeFile){
			TreeFile treeFile = (TreeFile) obj;
			if (treeFile.hasChildren()) {
				return imgReg.get(IMG_PLEIN);
			} else {
				boolean isMultimediaFile = BridageFormats.isMultimedia(treeFile.getAbsoluteName());
				boolean isCompressedFile = BridageFormats.isCompressed(treeFile.getAbsoluteName());
				if (isMultimediaFile) {
					return imgReg.get(IMG_MULTIMEDIA);
				} else if (isCompressedFile) {
					return imgReg.get(IMG_COMPRESSED_FILE);
				} else {
					return imgReg.get(IMG_VIDE);
				}
			}
		}
		if (obj instanceof TreeMensuel)
			return imgReg.get(IMG_MENSUEL);
		if (obj instanceof TreeMission)
			return imgReg.get(IMG_MISSION);
		if (obj instanceof TreeSegment)
			return imgReg.get(IMG_SEGMENT);

		return null;
	}

}
