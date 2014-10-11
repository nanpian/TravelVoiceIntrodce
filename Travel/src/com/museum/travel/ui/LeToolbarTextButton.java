package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.museum.travel.R;
import com.museum.travel.ui.LeToolbarButton;
import com.museum.travel.util.*;
import com.museum.travel.ui.*;

public class LeToolbarTextButton extends LeToolbarButton {
	private static final int UI_TEXT = 16;
	private static final int UI_WIDTH = 56;
	private static final int UI_HEIGHT = 32;

	private static final int DEFAULT_SHADOW_OFFSET_Y = -1;

	private String mText;

	private Paint mPaint;

	private int mNormalColor;
	private int mPressColor;
	private int mDisabledColor;
	private int mNormalBgColor;
	private int mPressBgColor;
	private int mDisableBgColor;

	private int mShadowOffsetY;

	public LeToolbarTextButton(Context context) {
		super(context);

		initResources();
	}

	private void initResources() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(LeUtils.getDensityDimen(getContext(), UI_TEXT));

		mShadowOffsetY = LeUtils.getDensityDimen(getContext(), DEFAULT_SHADOW_OFFSET_Y);

		mNormalColor = LeTheme.getColor(getContext(), R.color.common_text);
		mPressColor = LeTheme.getColor(getContext(), R.color.common_text);
		mDisabledColor = LeTheme.getColor(getContext(), R.color.toolbar_text_disable);
		mNormalBgColor = Color.TRANSPARENT;
		mPressBgColor = LeTheme.getColor(getContext(), R.color.common_press);
		mDisableBgColor = Color.TRANSPARENT;
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (widthMeasureSpec == 0 || heightMeasureSpec == 0) {
			final int width = LeUtils.getDensityDimen(getContext(), UI_WIDTH);
			final int height = LeUtils.getDensityDimen(getContext(), UI_HEIGHT);
			setMeasuredDimension(width, height);
		} else {
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int height = MeasureSpec.getSize(heightMeasureSpec);
			setMeasuredDimension(width, height);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mIsDisable) {
			mPaint.setColor(mDisabledColor);
			canvas.drawColor(mDisableBgColor);
		} else if (mIsPress) {
			mPaint.setColor(mPressColor);
			canvas.drawColor(mPressBgColor);
		} else {
			mPaint.setColor(mNormalColor);
			canvas.drawColor(mNormalBgColor);
		}
		int offsetX, offsetY;
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mText);
		offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint) + mShadowOffsetY;
		canvas.drawText(mText, offsetX, offsetY, mPaint);
	}

}
