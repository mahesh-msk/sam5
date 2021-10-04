package com.faiveley.samng.vueexplorateur.ihm.vue.viewprovider;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

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

	
	public ViewCellLabelProvider() {
		imagesInitialize();
	}
	
	private Image repositoryImage;
	private Image filePleinImage;
	private Image fileVideImage;
	private Image fileMultimediaImage;
	private Image folderImage;
	private Image mensuelImage;
	private Image missionImage;
	private Image segmentImage;
	private Image compressedFileImage;

	public void imagesInitialize() {
		repositoryImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/IMG_OBJ_PROJECT.jpg"),null)).createImage();
		folderImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/IMG_OBJ_FOLDER.jpg"),null)).createImage(); 

		filePleinImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/unexplored.png"),null)).createImage();

		fileVideImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/explored.png"),null)).createImage();
		
		fileMultimediaImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/multimedia.png"),null)).createImage();
		
		compressedFileImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/file_extension_gz.png"),null)).createImage();

		mensuelImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/mensuel.png"),null)).createImage();

		missionImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/mission.png"),null)).createImage();

		segmentImage = ImageDescriptor.createFromURL(
				FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(),
						new Path("/icons/segment.png"),null)).createImage();	
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
			return repositoryImage;
		if (obj instanceof TreeFolder)
			return folderImage;
		if (obj instanceof TreeFile){
			TreeFile treeFile = (TreeFile) obj;
			if (treeFile.hasChildren()) {
				return filePleinImage;
			} else {
				boolean isMultimediaFile = BridageFormats.isMultimedia(treeFile.getAbsoluteName());
				boolean isCompressedFile = BridageFormats.isCompressed(treeFile.getAbsoluteName());
				if (isMultimediaFile) {
					return fileMultimediaImage;
				} else if (isCompressedFile) {
					return compressedFileImage;
				} else {
					return fileVideImage;
				}
			}
		}
		if (obj instanceof TreeMensuel)
			return mensuelImage;
		if (obj instanceof TreeMission)
			return missionImage;
		if (obj instanceof TreeSegment)
			return segmentImage;

		return null;
	}

}
