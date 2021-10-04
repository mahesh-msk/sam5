package com.faiveley.kvbdecoder.model.kvb.train;

import org.json.JSONObject;

import com.faiveley.kvbdecoder.exception.model.train.TrainCategoryEnumException;
import com.faiveley.kvbdecoder.exception.model.train.TrainDirectionEnumException;
import com.faiveley.kvbdecoder.services.json.JSONService;



/**
 * Entité qui réunit les informations sur le train: catégorie et direction.
 * 
 * @author jthoumelin
 *
 */
public class TrainData {
	private TrainCategoryEnum trainCategory;
	private TrainDirectionEnum trainDirection;
	
	public TrainData(TrainCategoryEnum trainCategory, TrainDirectionEnum trainDirection) {
		this.trainCategory = trainCategory;
		this.trainDirection = trainDirection;
	}
	
	public TrainData(String trainInfo) throws TrainCategoryEnumException, TrainDirectionEnumException {
		JSONObject jsonObject = new JSONObject(trainInfo);
		trainCategory = trainCategoryFromString(jsonObject.get(JSONService.JSON_DECODER_TRAINCATEGORY_LABEL).toString());
		trainDirection = trainDirectionFromString(jsonObject.get(JSONService.JSON_DECODER_TRAINDIRECTION_LABEL).toString());
	}
	
	public TrainCategoryEnum getTrainCategory() {
		return trainCategory;
	}
	
	public TrainDirectionEnum getTrainDirection() {
		return trainDirection;
	}
	
	public static TrainCategoryEnum trainCategoryFromString(String trainCategory) throws TrainCategoryEnumException {		  
		for (TrainCategoryEnum value : TrainCategoryEnum.values()) {
			if (value.toString().equals(trainCategory.toUpperCase())) {
				return value;
			}
		}
	  
		throw new TrainCategoryEnumException(trainCategory);
	}

	public static TrainDirectionEnum trainDirectionFromString(String trainDirection) throws TrainDirectionEnumException {		  
		for (TrainDirectionEnum value : TrainDirectionEnum.values()) {
			if (value.toString().equals(trainDirection.toUpperCase())) {
				return value;
			}
		}
	  
		throw new TrainDirectionEnumException(trainDirection);
	}
}
