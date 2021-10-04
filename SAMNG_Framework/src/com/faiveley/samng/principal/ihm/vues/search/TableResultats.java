package com.faiveley.samng.principal.ihm.vues.search;

import java.util.List;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.print.TableResultatsExplorer;
import com.faiveley.samng.principal.sm.search.Result;

public class TableResultats {

	protected List<Result> results = null;
	
	private static final TableResultats INSTANCE = new TableResultats();

	protected TableResultats() {

	}

	public static TableResultats getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return TableResultatsExplorer.getInstance();
		}
		return INSTANCE;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public void setResults(Result[] tab) {
		for (int i = 0; i < tab.length; i++) {
			this.results.set(i, tab[i]);
		}
	}
}
