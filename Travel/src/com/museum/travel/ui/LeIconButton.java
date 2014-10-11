package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.museum.travel.util.Utils;
import com.museum.travel.util.LeUtils;
import com.museum.travel.util.LeTextUtil;
import com.museum.travel.util.LeColorUtil;

public class LeIconButton extends LeButton {
	
	private static final int DEFAULT_PRESS_COLOR = 0xff929497;
	private static final int DEFAULT_DISABLE_COLOR = Color.GRAY;
	
	protected boolean mIsDisable = false;
	
	private Drawable mNormalBg;
	private Drawable mPressBg;
	
	private Bitmap mNormalIcon;
	private Bitmap mPressIcon;
	
	private Paint mNormalPaint;
	private Paint mPressPaint;
	private Paint mDisablePaint;
	private boolean mIsFocused;
	
	public LeIconButton(Context context) {
		super(context);
		
		setWillNotDraw(false);

		initResource();
	}

	public void setUIFocused(boolean focused) {
		mIsFocused = focused;
	}
	
	private void initResource() {
		mNormalPaint = new Paint();
		
		mPressPaint = new Paint();
		mPressPaint.setColorFilter(LeColorUtil.createColorFilterByColor(DEFAULT_PRESS_COLOR));
		
		mDisablePaint = new Paint();
		mDisablePaint.setColorFilter(LeColorUtil.createColorFilterByColor(DEFAULT_DISABLE_COLOR));
	}
	
	public void setNormalPaint(int color) {
		mNormalPaint.setColorFilter(LeColorUtil.createColorFilterByColor(color));
	}
	
	public void setPressColor(int color) {
		mPressPaint.setColorFilter(LeColorUtil.createColorFilterByColor(color));
	}
	
	public void setPressPaint(Paint paint) {
		mPressPaint = paint;
	}
	
	public void setDisableColor(int color) {
		mDisablePaint.setColorFilter(LeColorUtil.createColorFilterByColor(color));
	}
	
	public void setIcon(int resId) {
		mNormalIcon = LeUtils.getBitmap(getContext(), resId);
	}
	
	public void setPressIcon(int resId) {
		mPressIcon = LeUtils.getBitmap(getContext(), resId);
	}
	
	public void setNormalBgDrawable(int resId) {
		mNormalBg = getResources().getDrawable(resId);
	}
	
	public void setPressBgDrawable(int resId) {
		mPressBg = getResources().getDrawable(resId);
	}
	
	public void setDisable(boolean isDisable) {
		mIsDisable = isDisable;
		postInvalidate();
		
		if (mIsDisable) {
			setClickable(false);
		} else {
			setClickable(true);
		}
	}
	
	public boolean getIsDisable(){
		return mIsDisable;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		if (width == 0 || height == 0) {
			if (mNormalIcon != null) {
				width = mNormalIcon.getWidth();
				height = mNormalIcon.getHeight();
			}
		}
		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mIsPress && !mIsDisable) {
			if (mPressBg != null) {
				mPressBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mPressBg.draw(canvas);
			}
		} else {
			if (mNormalBg != null) {
				mNormalBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mNormalBg.draw(canvas);
			}
		}
		
		if (mNormalIcon == null) {
			return;
		}
		
		int offsetX, offsetY;
		offsetX = (getMeasuredWidth() - mNormalIcon.getWidth()) / 2;
		offsetY = (getMeasuredHeight() - mNormalIcon.getHeight()) / 2;
		if (mIsDisable) {
			canvas.drawBitmap(mNormalIcon, offsetX, offsetY, mDisablePaint);
		} else {
			if (mIsPress || mIsFocused) {
				if (mIsPress && mPressIcon != null) {
					canvas.drawBitmap(mPressIcon, offsetX, offsetY, null);
				} else {
					canvas.drawBitmap(mNormalIcon, offsetX, offsetY, mPressPaint);
				}
			} else {
				canvas.drawBitmap(mNormalIcon, offsetX, offsetY, mNormalPaint);
			}
		}
	}
}
