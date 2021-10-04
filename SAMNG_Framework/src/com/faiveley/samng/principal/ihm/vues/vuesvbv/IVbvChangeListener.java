package com.faiveley.samng.principal.ihm.vues.vuesvbv;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public interface IVbvChangeListener {
	public void onVbvAdded(String vbvName, String oldVbvName);
	public void onVbvRemoved(String vbvName);
}
