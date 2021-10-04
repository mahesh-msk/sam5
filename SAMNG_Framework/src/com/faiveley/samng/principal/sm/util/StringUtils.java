package com.faiveley.samng.principal.sm.util;

import java.util.List;

public class StringUtils {
	public static String join(List<String> list, String separator) {
		return join(list.toArray(new String[0]), separator);
	}
	
	public static String join(String[] array, String separator) {
		StringBuilder result = new StringBuilder();
		
		for(int i = 0; i < array.length; i++) {
			result.append(array[i]);
			
			if (i != array.length - 1) {
				result.append(separator);
			}
		}
		
		return result.toString();
	}
	
	public static boolean isBlank(String str) {
		return str == null || str.isEmpty();
	}
}