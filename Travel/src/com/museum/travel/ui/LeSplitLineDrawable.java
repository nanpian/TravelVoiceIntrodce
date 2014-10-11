package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.museum.travel.R;

public class LeSplitLineDrawable extends Drawable {

	private Paint mPaint;

	private Context mContext;

	private int mLineColor;
	private int mShadowColor;

	private boolean mHasShadow;
	
	public LeSplitLineDrawable(Context context) {
		this(context, true);
	}
	
	public LeSplitLineDrawable(Context context, boolean hasShadow) {
		this(context, 2, hasShadow);
	}

	public LeSplitLineDrawable(Context context, int strokeWidth, boolean hasShadow) {
		mContext = context;
		mHasShadow = hasShadow;
		initResource(strokeWidth);
	}

	private void initResource(int strokeWidth) {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(strokeWidth);

		mLineColor = mContext.getResources().getColor(R.color.common_splitline);
		mShadowColor = mContext.getResources().getColor(R.color.common_splitline_shadow);
	}

	public void setLineColor(int color) {
		mLineColor = color;
	}

	public void setShadowColor(int color) {
		mShadowColor = color;
	}
	
	public int getLineWidthDimension() {
		if (mHasShadow) {
			return (int) (mPaint.getStrokeWidth() * 2);
		}
		return (int) mPaint.getStrokeWidth();
	}

	@Override
	public void draw(Canvas canvas) {
		int offsetX, offsetY;

		if (mHasShadow) {
			if (getBounds().left == getBounds().right) {
				offsetX = getBounds().left;
				mPaint.setColor(mLineColor);
				canvas.drawLine(offsetX, getBounds().top, offsetX, getBounds().bottom, mPaint);

				offsetX += mPaint.getStrokeWidth();
				mPaint.setColor(mShadowColor);
				canvas.drawLine(offsetX, getBounds().top, offsetX, getBounds().bottom, mPaint);
			} else {
				offsetY = getBounds().top;
				mPaint.setColor(mLineColor);
				canvas.drawLine(getBounds().left, offsetY, getBounds().right, offsetY, mPaint);

				offsetY += mPaint.getStrokeWidth();
				mPaint.setColor(mShadowColor);
				canvas.drawLine(getBounds().left, offsetY, getBounds().right, offsetY, mPaint);
			}
		}else{
			mPaint.setColor(mLineColor);
			canvas.drawLine(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, mPaint);
		}
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

}
