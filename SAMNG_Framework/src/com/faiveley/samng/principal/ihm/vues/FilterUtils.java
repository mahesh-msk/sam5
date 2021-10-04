package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

public class FilterUtils {

	public static List<String> matchingValues(String[] values, AFiltreComposant varNameFilter) {
		List<String> filteredValues = new ArrayList<String>();
		for (String value : values) {
			boolean matchesFilter = FilterUtils.matchesFilter(value, varNameFilter);
			if (matchesFilter) {
				filteredValues.add(value);
			}
		}
		return filteredValues;
	}
	
	private static boolean matchesFilter(String value, AFiltreComposant varNameFilter) {
		if(varNameFilter.getEnfantCount()==0) {
			return true;
		} else {
			String operator = varNameFilter.getEnfant(0).getNom();
			String filterOpValue = varNameFilter.getEnfant(1).getNom();
			if (operator == null || "".equals(operator.trim())
					|| filterOpValue == null || "".equals(filterOpValue.trim())) {
				return true;
			} else {
				boolean matchesFilter = matchesVariableVolatileValueWithFilterValue(value, operator, filterOpValue);
				if (matchesFilter) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean matchesFilters(String value, Map<String, AFiltreComposant> varNamesFilters) {
		int nbFiltersMatch = 0;
		for(Entry<String, AFiltreComposant> filterEntry : varNamesFilters.entrySet()) {
			AFiltreComposant filter = filterEntry.getValue();
			// If we do not have an operator for this filter then it matches
			if(filter.getEnfantCount()==0) {
				nbFiltersMatch++;
			}
			else {
				// If we have an operator but it is a null or empty operator
				// or there is no value specified then it matches
				String operator = filter.getEnfant(0).getNom();
				String filterOpValue = filter.getEnfant(1).getNom();
				if (operator == null || "".equals(operator.trim())
						|| filterOpValue == null || "".equals(filterOpValue.trim())) {
					nbFiltersMatch++;
				}
				else {
					boolean matchesFilter = matchesVariableVolatileValueWithFilterValue(value, operator, filterOpValue);
					if (matchesFilter) {
						nbFiltersMatch++;
					}
				}
			}
		}
		
		return nbFiltersMatch == varNamesFilters.size();
	}
	
	public static boolean matchesVariableVolatileValueWithFilterValue(String val,
			String operator, String filterOpValue) {
		char opVal = operator.charAt(0);
		boolean result = false;
		
		switch(opVal) {
			case '=':
				result = val.compareTo(filterOpValue) == 0;
				break;
			case '\u2260': // !=
				result = val.compareTo(filterOpValue) != 0;
				break;
			case '>':
				result = val.compareTo(filterOpValue) > 0;
				break;
			case '\u2265': // >=
				result = val.compareTo(filterOpValue) >= 0;
				break;
			case '<':
				if (operator.length() == 2 && operator.charAt(1) == '<') {
					// We have '<<'
					int dotsIdx = filterOpValue.indexOf("..");
					if (dotsIdx == -1) {
						return true;
					}
					String firstIntervalValue = filterOpValue.substring(0, dotsIdx);
					if (val.compareTo(firstIntervalValue) < 0) {
						return false;
					}
					String secondIntervalValue = filterOpValue.substring(dotsIdx + 2);
					if (val.compareTo(secondIntervalValue) > 0) {
						return false;
					}
				} else {
					// We have a simple '<'
					result = val.compareTo(filterOpValue) < 0;
				}
				break;
			case '\u2264':
				result = val.compareTo(filterOpValue) <= 0;
				break;
			default:
				result = true;
				break;
		}
		
		return result;
	}
	
}
