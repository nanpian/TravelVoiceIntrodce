package com.museum.travel.ui;

import android.content.Context;
import android.util.SparseArray;

public class LeTheme {
	
	private static SparseArray<Integer> sColorMap = new SparseArray<Integer>();
	
	private static boolean sIsNightTheme = false;
	
	private LeTheme(){}
	
	public static int getColor(Context context, int resId) {
		if (sColorMap.indexOfKey(resId) > 0) {
			return sColorMap.get(resId);
		} else {
			int color = context.getResources().getColor(resId);
			sColorMap.put(resId, color);
			return color;
		}
	}

	public static synchronized void setIsNightTheme(boolean isNightTheme) {
		sIsNightTheme = isNightTheme;
	}
	
	public static boolean isNightTheme() {
		return sIsNightTheme;
	}
}
