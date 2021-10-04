package com.faiveley.samng.vuegraphique.sm.gestionGraphes;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.segments.ASegment;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class MessagesUtil {
	
	public static int getFirstMessageAfter(double startValue) {
        Message msg;
//		récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();
			
        
		//récupération de tous les messages
//		List<Message> msgs = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
    	int nbEvent = messages.size();
    	double value;
    	for (int i = 0; i < nbEvent; i++) {
            msg = messages.get(i);
            value = GestionnaireAxes.getAxeXValue(msg);
            if(value >= startValue) {
            	return msg.getMessageId();
            }
        }
    	return messages.get(0).getMessageId();
	}
	
	public static int getLastMessageBefore(double endValue) {
        Message msg;
//		récupération uniquement des bons messages
	ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();
			
        
		//récupération de tous les messages
//		List<Message> msgs = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
    	int nbEvent = messages.size();
    	double value;
    	for (int i = nbEvent-1; i >= 0; i--) {
            msg = messages.get(i);
            value = GestionnaireAxes.getAxeXValue(msg);
            if(value <= endValue) {
            	return msg.getMessageId();
            }
        }
    	return messages.get(nbEvent-1).getMessageId();
	}

	public static int getFirstMessageAfter(double startValue, ASegment segment) {
        Message msg;
//		récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();
			
        
		//récupération de tous les messages
//		List<Message> msgs = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
    	int nbEvent = messages.size();
    	int segmentFirstMsgId = segment.getStartMsgId();
    	int segmentLastMsgId = segment.getEndMsgId();
    	double value;
    	for (int i = 0; i < nbEvent; i++) {
            msg = messages.get(i);
            if(msg.getMessageId() < segmentFirstMsgId || msg.getMessageId() > segmentLastMsgId)
            	continue;
            value = GestionnaireAxes.getAxeXValue(msg);
            if(value >= startValue) {
            	return msg.getMessageId();
            }
        }
    	return segmentFirstMsgId;		//: see if is better to return the last
	}

	public static int getLastMessageBefore(double endValue, ASegment segment) {
        Message msg;
//		récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();
			
        
		//récupération de tous les messages
//		List<Message> msgs = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
    	int nbEvent = messages.size();
    	int segmentFirstMsgId = segment.getStartMsgId();
    	int segmentLastMsgId = segment.getEndMsgId();
    	double value;
    	for (int i = nbEvent-1; i >= 0; i--) {
            msg = messages.get(i);
            if(msg.getMessageId() < segmentFirstMsgId || msg.getMessageId() > segmentLastMsgId)
            	continue;
            value = GestionnaireAxes.getAxeXValue(msg);
            if(value <= endValue) {
            	return msg.getMessageId();
            }
        }
    	return segmentLastMsgId;		//: see if is better to return the first
	}

}
