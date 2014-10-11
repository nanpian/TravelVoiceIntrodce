/** 
 * Filename:    LeTextButton.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-7-18 下午3:03:49
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-18     chenwei27    1.0         1.0 Version 
 */
package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.museum.travel.util.Utils;
import com.museum.travel.util.LeUtils;
import com.museum.travel.util.LeTextUtil;
import com.museum.travel.util.LeColorUtil;

public class LeTextButton extends LeIconButton {
	
	private static final int UI_BUTTON_TEXT_SIZE = 20;
	
	private static final int UI_WIDTH = 56;
	private static final int UI_HEIGHT = 32;

	private static final int DEFAULT_SHADOW_OFFSET_Y = 0;

	public static final int COLOR_TEXT_NORMAL = Color.BLACK;
	private static final int COLOR_TEXT_PRESS = Color.GRAY;
	private static final int COLOR_TEXT_DISABLED = Color.GRAY;

	protected String mText;

	protected Paint mPaint;

	protected int mNormalColor;
	protected int mPressColor;
	private int mDisabledColor;

	private int mShadowOffsetY;
	
	private int mWidth;
	private int mHeight;
	private int mTextSize;
	
	public LeTextButton(Context context, int textResId) {
		this(context, context.getString(textResId), 0, 0, UI_BUTTON_TEXT_SIZE);
	}
	
	public LeTextButton(Context context, String text) {
		this(context, text, 0, 0, UI_BUTTON_TEXT_SIZE);
	}
	
	public LeTextButton(Context context, int textResId, int width, int height, int textSize) {
		this(context, context.getString(textResId), width, height, textSize);
	}

	public LeTextButton(Context context, String text, int width, int height, int textSize) {
		super(context);
		
		mText = text;
		mWidth = width;
		mHeight = height;
		mTextSize = textSize;
		
		initResources();
		
		setWillNotDraw(false);
	}

	private void initResources() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(LeUtils.getDensityDimen(getContext(), mTextSize));

		mShadowOffsetY = LeUtils.getDensityDimen(getContext(), DEFAULT_SHADOW_OFFSET_Y);

		mNormalColor = COLOR_TEXT_NORMAL;
		mPressColor = COLOR_TEXT_PRESS;
		mDisabledColor = COLOR_TEXT_DISABLED;
	}

	public void setNormalColor(int normalColor) {
		mNormalColor = normalColor;
	}

	public void setPressColor(int pressColor) {
		mPressColor = pressColor;
	}

	public void setShadowOffsetY(int offsetY) {
		mShadowOffsetY = offsetY;
	}

	public void setText(int resId) {
		mText = getResources().getString(resId);
	}

	public void setText(String text) {
		mText = text;
	}

	public void setTextDisabledColor(int color) {
		mDisabledColor = color;
	}
	
	public void setDisable(boolean disable) {
		mIsDisable = disable;
	}
	
	public boolean isDisabled() {
		return mIsDisable;
	}
	
	public void setTextSize(int size) {
		mTextSize = size;
		mPaint.setTextSize(LeUtils.getDensityDimen(getContext(), mTextSize));
	}
	
	public void setTextSizeByResid(int resid) {
		mPaint.setTextSize(LeUtils.getResDimen(getContext(), resid));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = (int) (mWidth * dm.density);
		int height = (int) (mHeight * dm.density);
		
		if (width == 0 || height == 0) {
			
			int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
			
			if (measuredHeight == 0 || measuredWidth == 0) {
				width = LeUtils.getDensityDimen(getContext(), UI_WIDTH);
				height = LeUtils.getDensityDimen(getContext(), UI_HEIGHT);
			} else {
				height = measuredHeight;
				width = measuredWidth;
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mIsPress) {
			mPaint.setColor(mPressColor);
		} else if (mIsDisable) {
			mPaint.setColor(mDisabledColor);
		} else {
			mPaint.setColor(mNormalColor);
		}
		int offsetX, offsetY;
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mText);
		offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint) + mShadowOffsetY;
		canvas.drawText(mText, offsetX, offsetY, mPaint);
	}

}
