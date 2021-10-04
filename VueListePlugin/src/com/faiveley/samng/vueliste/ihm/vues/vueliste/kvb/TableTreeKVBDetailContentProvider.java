package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.faiveley.kvbdecoder.decoder.KVBDecoderResult;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14;
import com.faiveley.kvbdecoder.services.json.JSONService;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage.AtessMessageErrorString;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage.AtessMessageTrainInfo;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailContentProvider;

public class TableTreeKVBDetailContentProvider extends TableTreeDetailContentProvider {	
	@Override
	public Object[] getElements(Object inputElement) {
		AtessMessage msg = (AtessMessage) inputElement;
		
		List<AtessMessageErrorString> decodingErrors = msg.getDecodingErrors();
		KVBDecoderResult decodedEvent = msg.getDecodedEvent();
		AtessMessageTrainInfo trainInfo = msg.getTrainInfo();

		List<Object> childs = new ArrayList<Object>(decodingErrors);
		
		childs.add(0, new KVBDecoderResultShell(decodedEvent));
		childs.add(0, new AtessMessageTrainInfoShell(trainInfo));
		
		return childs.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AtessMessageTrainInfoShell) {
			AtessMessageTrainInfo trainInfo = ((AtessMessageTrainInfoShell) parentElement).getData();
			
			if (trainInfo != null) {
				return trainInfo.getChilds();
			}
		} else if (parentElement instanceof KVBDecoderResultShell) {
			KVBDecoderResult result = ((KVBDecoderResultShell) parentElement).getData();
			
			if (result != null) {
				return result.getEvent().getKVBVariable().getInformationPoints().toArray();
			}							
		} else if (parentElement instanceof InformationPoint) {
			InformationPoint ip = (InformationPoint) parentElement;
			List<Object> childs = new ArrayList<Object>();
			
			if (ip.isRightDirection()) {
				List<Object> ipChilds = new ArrayList<Object>();
				List<Integer> xMarkersProcessed = new ArrayList<Integer>();
				
				Iterator<Marker> i = ip.getMarkers().iterator();
				
				while (i.hasNext()) {
					Marker m = i.next();
					int x = m.getX();
					
					if (!JSONService.isMarkerToIgnore(m) && !xMarkersProcessed.contains(x)) {
						xMarkersProcessed.add(x);
												
						if (m instanceof MarkerX14 && ((MarkerX14) m).getDistanceCode() != null) {
							ipChilds.add(new MarkerX14Distance((MarkerX14) m));
						}
						
						ipChilds.add(m);
					}
				}
							
				childs.addAll(ipChilds);
			}
			
			childs.add(0, ip.getLabels());
			
			return childs.toArray();
		}
		
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AtessMessageTrainInfoShell) {
			return true;
		} else if (element instanceof KVBDecoderResultShell) {
			return true;
		} else if (element instanceof InformationPoint) {
			if (!((InformationPoint) element).isCorrupted()) {
				return true;
			}
		}
				
		return false;
	}
		
	/**
	 * Classe permettant de gérer le cas très particulier de la balise X=14, où deux informations (distance + déclivité) peuvent être affichées.
	 * Cela est utile dans le cas d'un contexte où les objets sont seulement connus comme étant Object. Exemple: ITableLabelProvider. 
	 */
	public class MarkerX14Distance {
		private MarkerX14 marker;
		
		public MarkerX14Distance(MarkerX14 mX14) {
			this.marker = mX14;
		}

		public String getCode() {
			return marker.getDistanceCode();
		}

		public String getValue() {
			return marker.getDistanceValue();
		}
		
		public InformationPoint getParent() {
			return marker.getParent();
		}
	}
	
	/**
	 * Classe permettant d'englober un objet KVBDecoderResult pouvant être null, cela permet de le traiter même s'il est nul.
	 * 
	 * Cela est utile dans le cas d'un contexte où les objets sont seulement connus comme étant Object. Exemple: ITableLabelProvider. 
	 */
	public class KVBDecoderResultShell {
		private KVBDecoderResult data;
		
		public KVBDecoderResultShell(KVBDecoderResult data) {
			this.data = data;
		}
		
		public KVBDecoderResult getData() {
			return this.data;
		}
	}
	
	/**
	 * Classe permettant d'englober un objet KVBDecoderResult pouvant être null, cela permet de le traiter même s'il est nul.
	 * 
	 * Cela est utile dans le cas d'un contexte où les objets sont seulement connus comme étant Object. Exemple: ITableLabelProvider. 
	 */
	public class AtessMessageTrainInfoShell {
		private AtessMessageTrainInfo data;
		
		public AtessMessageTrainInfoShell(AtessMessageTrainInfo data) {
			this.data = data;
		}
		
		public AtessMessageTrainInfo getData() {
			return this.data;
		}
	}
}
