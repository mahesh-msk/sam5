package com.faiveley.samng.principal.ihm.actions.vue;

/**
 * Enumerates the types of vues
 * 
 * @author meggy
 *
 */
public enum VueType {

	FILE,
	CONFIG,
	EDITION,
	FILTRE,
	HELP,
	TOOLS,
	VUE,
	MARKERS,
	SEARCH;
	
	private String value;
	
	/**
	 * Private constructor
	 * Sets the name for the created vue
	 */
	private VueType() {
		if (name().equals("VUE")) {
			this.value = "SAMNG.menuVue";
		} else if (name().equals("FILTRE")) {
			this.value = "SAMNG.menuFiltre";
		} else if (name().equals("FILE")) {
			this.value = "SAMNG.menuFile";
		} else if (name().equals("EDITION")) {
			this.value = "SAMNG.menuEdition";
		} else if (name().equals("HELP")) {
			this.value = "SAMNG.menuHelp";
		} else if (name().equals("CONFIG")) {
			this.value = "SAMNG.menuConfig";
		} else if (name().equals("TOOLS")) {
			this.value = "SAMNG.menuTools";
		} else if (name().equals("FILTRE")) {
			this.value = "SAMNG.menuFiltre";
		} else if (name().equals("MARKERS")) {
			this.value = "SAMNG.menuMarkers";
		} else if (name().equals("SEARCH")) {
			this.value = "SAMNG.menuSearch";
		} 
		
	}
	
	/**
	 * Returns the name of the view
	 * @return	the name
	 */
	public String value() {
		return this.value;
	}
}
