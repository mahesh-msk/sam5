package com.faiveley.samng.principal.sm.data.flag;

import com.faiveley.samng.principal.logging.SamngLogger;

public class Flag {

	private String label;
	private String eventName;
	private int id;
	
	
	public Flag(int id, String label, String eventName) {
		this.label = label;
		this.eventName = eventName;
		this.id = id;
	}
	
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return this.eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void appendFlag(Flag flag) {
		if (flag != null) {
			this.label += flag.getLabel();
			this.id |= flag.getId();
		} else {
			SamngLogger.getLogger().debug(Messages.getString("dataFlag.1"));
		}
	}
}
