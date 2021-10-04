package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb;

import java.util.Map;

import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.train.TrainCategoryEnum;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.samng.principal.ihm.Activator;

import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage.AtessMessageErrorString;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailLabelProvider;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailContentProvider.AtessMessageTrainInfoShell;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailContentProvider.KVBDecoderResultShell;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailContentProvider.MarkerX14Distance;

public class TableTreeKVBDetailLabelProvider extends TableTreeDetailLabelProvider {	
	private static final String LABEL_IHM_KVB_DATA = "IHM.DonneesTrainKVB";
	private static final String LABEL_IHM_KVB_CATEGORY = "IHM.CategorieKVB";
	private static final String LABEL_IHM_INFORMATIONS = "IHM.Informations";
	public static final String LABEL_IHM_INFORMATION_POINT = "IHM.PointInformation";
	public static final String LABEL_IHM_INFORMATION_POINT_TYPE = "IHM.TypePointInformation";
	public static final String LABEL_IHM_DECODING_ERROR = "IHM.ErreurDecodage";

	private static final Langage language = Activator.getDefault().getCurrentLanguage();
	private static final KVBLoaderService loaderService = KVBLoaderService.getServiceInstance();
	
	@SuppressWarnings("unchecked")
	public String getColumnText(Object element, int columnIndex) {		
		String columnText = "";
		
		switch (columnIndex) {
			case 0:
				if (element instanceof AtessMessageTrainInfoShell) {
					columnText = loaderService.getLabel(LABEL_IHM_KVB_DATA, language.toString());
				} else if (element instanceof KVBDecoderResultShell) {
					columnText = loaderService.getLabel(LABEL_IHM_INFORMATIONS, language.toString());
				} else if (element instanceof AVariableComposant) {
					columnText = getVariableName(language, (AVariableComposant) element);
				} else if (element instanceof TrainCategoryEnum) {
					columnText = loaderService.getLabel(LABEL_IHM_KVB_CATEGORY, language.toString());
				} else if (element instanceof InformationPoint) {
					columnText = String.format(loaderService.getLabel(LABEL_IHM_INFORMATION_POINT, language.toString()), String.valueOf(((InformationPoint) element).getIndex() + 1));
				} else if (element instanceof Map<?, ?>) {
					columnText = loaderService.getLabel(LABEL_IHM_INFORMATION_POINT_TYPE, language.toString());
				} else if (element instanceof Marker) {
					columnText = loaderService.getLabel(((Marker) element).getCode(), language.toString());
				} else if (element instanceof MarkerX14Distance) {
					columnText = loaderService.getLabel(((MarkerX14Distance) element).getCode(), language.toString());
				} else if (element instanceof AtessMessageErrorString) {
					columnText = loaderService.getLabel(LABEL_IHM_DECODING_ERROR, language.toString());
				}
				
				break;
			case 1:
				if (element instanceof AVariableComposant) {
					columnText = getDecodedValue((AVariableComposant) element);
				} else if (element instanceof TrainCategoryEnum) {
					columnText = ((TrainCategoryEnum) element).name();
				} else if (element instanceof InformationPoint) {
					String alertText = ((InformationPoint) element).getAlertText();
					
					if (!alertText.isEmpty()) {
						columnText = loaderService.getLabel(alertText, language.toString());
					}
				} else if (element instanceof Map<?, ?>) {
					String label = ((Map<String, String>) element).get(language.toString());
					
					if (label == null) {
						label = ((Map<String, String>) element).get(KVBLoaderService.XML_LABEL_ATTRIBUTE_LANG_VALUE_DEF);
					}
					
					columnText = label;
				} else if (element instanceof Marker) {
					columnText = ((Marker) element).buildString(language.toString());
				} else if (element instanceof MarkerX14Distance) {
					columnText = ((MarkerX14Distance) element).getValue();
				} else if (element instanceof AtessMessageErrorString) {
					columnText = ((AtessMessageErrorString) element).getValue();
				}

				break;
			case 2:
				if (element instanceof InformationPoint) {
					columnText = String.valueOf(((InformationPoint) element).getIndex());
				} else if (element instanceof Marker) {
					columnText = String.valueOf(((Marker) element).getParent().getIndex());
				} else if (element instanceof MarkerX14Distance) {
					columnText = String.valueOf(((MarkerX14Distance) element).getParent().getIndex());
				}
			default:
				break;
		}
		
		return columnText;
	}
}
