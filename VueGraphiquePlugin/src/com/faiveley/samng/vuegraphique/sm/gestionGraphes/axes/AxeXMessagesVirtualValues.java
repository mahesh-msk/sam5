package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.segments.ASegment;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AxeXMessagesVirtualValues {
	List<AxeXMessageVirtualValue> virtualValues = new ArrayList<AxeXMessageVirtualValue>();
	private Map<Integer, AxeXMessageVirtualValue> mapVirtualValues = new LinkedHashMap<Integer, AxeXMessageVirtualValue>();

	public AxeXMessagesVirtualValues() {

	}

	public void initialize(TypeAxe typeAxe) {
		
		ListMessages messages = ActivatorData.getInstance().getVueData()
		.getDataTable().getEnregistrement().getMessages();

		Message msgStart = messages.get(0);

		HashMap<Integer, ASegment> segments = AxeXUtil.getSegments(typeAxe);
		HashMap<Integer, TypeRupture> ruptures = AxeXUtil.getRuptures(typeAxe);
		Message msg;
		AxeXMessageVirtualValue virtVal;
		virtualValues.clear();
		mapVirtualValues.clear();

		double currentValue;
		double startValue = AxeXUtil.getAxeXValue(msgStart, typeAxe);
		double segStartValue = startValue;
		double prevMsgValue = startValue;

		if(segments == null || segments.size() == 0) {
			for(int i = 0; i<messages.size(); i++) {
				msg = messages.get(i);
				try {


					//SUPPR_HASHMAP
					if (msg!=null) {

						//we are inside a segment so, we have to check if current message is not a break
						//due to the else condition the rupture that caused a new segment creation is ignored
						if(ruptures != null && ruptures.containsKey(msg.getMessageId())) {
							segStartValue = AxeXUtil.getAxeXValue(msg, typeAxe);
							startValue = prevMsgValue;
						}
						//if no break (rupture) then the message value for message will be added
						currentValue = startValue + (AxeXUtil.getAxeXValue(msg, typeAxe) - segStartValue);
						prevMsgValue = currentValue;

						virtVal = new AxeXMessageVirtualValue(msg.getMessageId(), currentValue);
						virtualValues.add(virtVal);
						mapVirtualValues.put(virtVal.getMsgId(), virtVal);
					}
				} catch (Exception e) {
					
				}
			}
		} else {
			ASegment segment = AxeXUtil.getSegment(segments, 0, typeAxe);
			int curSegment = 0;
			try {
				for(int i = 0; i<messages.size(); i++) {

					msg = messages.get(i);
					
					//SUPPR_HASHMAP
					if (msg!=null) {
						//we have a segment change
						if(msg.getMessageId() > segment.getEndMsgId()) {
							if (curSegment+1<segments.size()) {
								curSegment++;
							}

							segment = AxeXUtil.getSegment(segments, curSegment, typeAxe);

							//we moved in another segment
							segStartValue = AxeXUtil.getAxeXValue(msg, typeAxe);
							startValue = prevMsgValue;
						} else {
							//we are inside a segment so, we have to check if current message is not a break
							//due to the else condition the rupture that caused a new segment creation is ignored
							if(ruptures != null && ruptures.containsKey(msg.getMessageId())) {
								segStartValue = AxeXUtil.getAxeXValue(msg, typeAxe);
								startValue = prevMsgValue;
							}
						}
						currentValue = startValue + (AxeXUtil.getAxeXValue(msg, typeAxe) - segStartValue);
						prevMsgValue = currentValue;

						virtVal = new AxeXMessageVirtualValue(msg.getMessageId(), currentValue);
						virtualValues.add(virtVal);
						mapVirtualValues.put(virtVal.getMsgId(), virtVal);
					}
				}
			} catch (Exception e) {
				
			}

		}
	}

	public double getGlobalCumul() {
		return virtualValues.get(virtualValues.size()-1).getVirtualValue() - virtualValues.get(0).getVirtualValue(); 
	}

	public AxeXMessageVirtualValue getValueByMsgId(int msgId) {
		return mapVirtualValues.get(msgId);
	}

	/**
	 * Returns the cumul between two messages
	 * @param startMsgId start message id for computing cumul
	 * @param endMsgId end message id for computing cumul
	 * @return the computed cumul
	 */
	public double getCumul(int startMsgId, int endMsgId) {
		AxeXMessageVirtualValue valueStart = mapVirtualValues.get(startMsgId);
		AxeXMessageVirtualValue valueEnd = mapVirtualValues.get(endMsgId);
		return valueEnd.getVirtualValue() - valueStart.getVirtualValue();
	}

	public double getCumulToMessage(int msgId) {
		return getCumul(virtualValues.get(0).getMsgId(), msgId);
	}
}
