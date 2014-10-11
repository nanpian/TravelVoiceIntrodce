package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class LeButton extends ViewGroup {

	protected boolean mIsPress = false;

	public LeButton(Context context) {
		this(context, null);
	}
	
	public LeButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setClickable(true);
		setWillNotDraw(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isClickable()) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mIsPress = true;
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsPress = false;
				invalidate();
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mIsPress) {
			canvas.drawColor(Color.GRAY);
		}
	}

}
