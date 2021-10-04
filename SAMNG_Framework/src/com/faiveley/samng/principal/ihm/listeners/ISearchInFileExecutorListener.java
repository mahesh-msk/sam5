package com.faiveley.samng.principal.ihm.listeners;

import com.faiveley.samng.principal.sm.search.Result;

/**
 * Interface for the executor of search in file listener
 * @author meggy
 *
 */
public interface ISearchInFileExecutorListener {

	/**
	 * Methods to inform the listener when the execution of the task is finished
	 * @param succes	flag for succes or error
	 */
	public void onFinishExecution(boolean succes);
	
	/**
	 * Methods to inform the listener when the execution of the task is started
	 * @return 		true or false if the operations before staring the task are made with succes
	 * 					if return false, the task should not be started
	 */
	public boolean onStartExecution();
	
	public void onRefresh(Result fileName);
}
