package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ZoomComposite extends AZoomComposant {
	private int indiceGraph;
	private List<AZoomComposant> listeZoom = new ArrayList<AZoomComposant>();
	
	public ZoomComposite() {
		
	}
	
	public ZoomComposite(ZoomComposite srcZoom) {
		if(srcZoom != null) {
			this.indiceGraph = srcZoom.indiceGraph;
			for(AZoomComposant zoom: srcZoom.listeZoom) {
				if(zoom != null)
					listeZoom.add(zoom.clone());
				else 
					listeZoom.add(null);
			}
		}
	}
	
	public void ajouterZoomComposant(AZoomComposant enfant) {
		listeZoom.add(enfant);
	}
	
	public void ajouterZoomComposant(int indice,AZoomComposant enfant) {
		listeZoom.add(indice,enfant);
	}

	public void ajouterZoomComposants(List<AZoomComposant> enfants) {
		listeZoom.addAll(enfants);
	}

	
	public void supprimerZoomComposant(int indice) {
		if(indice >= 0 && indice < listeZoom.size())
			listeZoom.remove(indice);
	}
	
	public AZoomComposant getEnfant(int indice) {
		AZoomComposant enfant = null;
		if(indice >= 0 && indice < listeZoom.size())
			enfant = listeZoom.get(indice);
		return enfant;
	}
	
	public List<AZoomComposant> getEnfants() {
		return listeZoom;
	}
	
	public int getEnfantCount() {
		return this.listeZoom.size();
	}

	public void setIndiceGraphe(int indiceGraph) {
		this.indiceGraph = indiceGraph;
	}

	public int getIndiceGraphe() {
		return this.indiceGraph;
	}

	@Override
	public AZoomComposant clone() {
		return new ZoomComposite(this);
	}

}
