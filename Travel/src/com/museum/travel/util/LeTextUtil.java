package com.museum.travel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;


public final class LeTextUtil {
	
	private LeTextUtil(){}

	public static int calcXWhenAlignCenter(int width, Paint paint, String string) {
		if (paint == null) {
			return 0;
		}
		return (int) ((width - paint.measureText(string)) / 2);
	}

	/**
	 * 
	 * @return 提供文字垂直居中�?drawText方法中y�?	 */
	public static int calcYWhenAlignCenter(int height, Paint paint) {
		if (paint == null) {
			return 0;
		}

		final float fontHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
		return (int) ((height - fontHeight) / 2 - paint.getFontMetrics().top);
	}

	public static int getPaintHeight(Paint paint) {
		return (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top);
	}

	public static void drawTextAtCanvasCenter(Canvas canvas, Paint paint, String text) {
		if (text == null) {
			return;
		}

		final int width = canvas.getWidth();
		final int height = canvas.getHeight();
		int offsetX, offsetY;
		offsetX = calcXWhenAlignCenter(width, paint, text);
		offsetY = calcYWhenAlignCenter(height, paint);
		canvas.drawText(text, offsetX, offsetY, paint);
	}

	public static String getTruncateEndString(String srcString, Paint paint, int width) {
		if (srcString == null) {
			return "";
		}
		return TextUtils.ellipsize(srcString, new TextPaint(paint), width, TextUtils.TruncateAt.END)
				.toString();
	}
	
	/**
	 * 获取文本的显示长�?	 */
	public static int getTextShowLength(final String text) {
		int showLength = 0;
		if (text != null) {
			int length = text.length();
			for (int i = 0; i < length; i++) {
				char currentChar = text.charAt(i);
				if (currentChar > 0x7f) {
					showLength += 2;
				} else {
					showLength += 1;
				}
			}
		}
		return showLength;
	}
	
	/**
	 * 获取文字折行，ASCII码中的字符占�?��字节，其它占两个字节
	 */
	public static List<String> getBreakTexts(final String text, final int lineSize) {
		List<String> titles = new ArrayList<String>();
		if (!LeUtils.isEmptyString(text)) {
			int start = 0;
			while (start < text.length()) {
				String cutTitle = getBreakText(text, start, lineSize);
				start += cutTitle.length();
				titles.add(cutTitle);
			}
		}
		return titles;
	}
	
	/**
	 * 文本折行
	 */
	private static String getBreakText(String title, int start, int size) {
		int length = title.length();
		String cutString = "";
		int cutSize = 0;
		for (int i = start; i < length; i++) {
			char currentChar = title.charAt(i);
			if (currentChar > 0x7f) {
				cutSize += 2;
			} else {
				cutSize += 1;
			}
			if (cutSize > size) {
				return cutString;
			}
			cutString += currentChar;
		}
		return cutString;
	}
	
	public static boolean hasChinese(String key) {
		try {
			Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");
			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				return true;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
