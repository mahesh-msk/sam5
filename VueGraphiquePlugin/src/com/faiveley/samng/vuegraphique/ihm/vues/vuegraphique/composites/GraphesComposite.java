package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.TypeMenuOptions;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GrapheGUI;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.ZoomAction;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Graphe;

public class GraphesComposite extends Composite {
	public boolean lastGraphe=false;
	private int numGraphes;
	private GrapheGUI graphesGui[];

	public GraphesComposite(Composite parent, int style, int numGraphes) {
		super(parent, style);
		this.numGraphes = numGraphes;
		graphesGui = new GrapheGUI[this.numGraphes];
		initialize();
		this.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				System.err.println("Resize GraphesComposite " + getBounds().height);
			}
		});
	}

	public boolean isLastGraphe(int position){
		if (position+1==GraphicConstants.MAX_GRAPHICS_COUNT) {
			return true;
		}
		int nbGraphesRestants=VueGraphique.getFilterGraphesNr();
		if (position+1==nbGraphesRestants) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;

		this.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);	
		lastGraphe=false;

		InfosFichierSamNg infos;
		try{
			infos = (InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo();
		}catch(Exception ex){
			infos = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());
		}	
		
		List<Graphe> listeGraphes = null;
		//if(infos.getNomFichierParcoursBinaire().equals(Activator.getDefault().getDernierFichierOuvert())){
		//if(ActivatorData.getInstance().getPoolDonneesVues().get("listesGraphes")!=null)
		listeGraphes = (List<Graphe>)ActivatorData.getInstance().getPoolDonneesVues().get("listesGraphes");
		//}
		//else{
		listeGraphes = new ArrayList<Graphe>();
		//Activator.getDefault().setDernierFichierOuvert(infos.getNomFichierParcoursBinaire());
		
		for(int j = 0; j<GraphicConstants.MAX_GRAPHICS_COUNT; j++) {	
			
			GraphiqueFiltreComposite gfc=FabriqueGraphe.getGrapheFiltre(j);
			if (!gfc.isActif()) {
				continue;
			}
			
			System.gc();
			Graphe g=FabriqueGraphe.creerGraphe(j,this.numGraphes);
			if (g.getListeCourbe().size()>0) {
				listeGraphes.add(g);
			}
		}

		int positionG=0;
		for(int i = 0; i<GraphicConstants.MAX_GRAPHICS_COUNT; i++) {
			GraphiqueFiltreComposite gfc=FabriqueGraphe.getGrapheFiltre2(i);
			if (gfc==null || !gfc.isActif()) {
				continue;
			}
			
			//si pas de variable renseignées on ne l'ajoute pas			
			if (!gfc.contientUneVariableRenseignee()) {
				continue;
			}
			
			lastGraphe=isLastGraphe(i);

			graphesGui[positionG] = new GrapheGUI(this, SWT.NONE, listeGraphes.get(positionG),lastGraphe, numGraphes,positionG);
			graphesGui[positionG].setLayoutData(gridData);
			graphesGui[positionG].addKeyListener(new KeyListener(){
				private boolean ctrPressed=false;
				public void keyPressed(KeyEvent e) {
					if(e.keyCode==SWT.CTRL){
						ctrPressed=true;
					} 
				}

				public void keyReleased(KeyEvent e) {
					if(e.keyCode==SWT.CTRL){
						ctrPressed=false;
					}
					if(e.keyCode=='m' && ctrPressed){
						new ZoomAction(TypeMenuOptions.MANUAL_ZOOM).run();	
					}else if(e.keyCode=='b' && ctrPressed){
						new ZoomAction(TypeMenuOptions.PREVIOUS_ZOOM).run();	
					}else if(e.keyCode=='n' && ctrPressed){
						new ZoomAction(TypeMenuOptions.NEXT_ZOOM).run();	
					}else if(e.keyCode=='a' && ctrPressed){
						new ZoomAction(TypeMenuOptions.MAIN_ZOOM).run();	
					}
				}
			});
			positionG++;
		}	
	}

	@Override
	public void dispose() {
		for(int i = 0; i<numGraphes; i++) {
			if(graphesGui[i] != null) {
				graphesGui[i].dispose();
			}
		}
		//GestionnaireZoom.reset();
		super.dispose();
	}

	@Override
	public void setMenu(Menu menu) {
		for(int i = 0; i<numGraphes; i++) {
			graphesGui[i].setMenu(menu);
		}
		super.setMenu(menu);
	}

	public void redrawGraphes(boolean redrawCourbes) {
		for(int i = 0; i<numGraphes; i++) {
			if(redrawCourbes)
				graphesGui[i].forceRedraw();
			else 
				graphesGui[i].redraw();
		}

	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
