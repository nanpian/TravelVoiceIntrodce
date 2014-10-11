package com.museum.travel.ui;

import android.content.Context;
import android.graphics.Canvas;

import com.museum.travel.R;
import com.museum.travel.ui.*;
import com.museum.travel.util.*;

public class LeFrameToolbar extends LeToolbar {

	private LeSplitLineDrawable mSplitLineDrawable;
	
	public LeFrameToolbar(Context context) {
		super(context);
		
		setBackgroundColor(getResources().getColor(R.color.toolbar_bg));
		
		mSplitLineDrawable = new LeSplitLineDrawable(getContext());
		mSplitLineDrawable.setLineColor(getResources().getColor(R.color.toolbar_split_line));
		mSplitLineDrawable.setShadowColor(getResources().getColor(R.color.toolbar_split_line_shadow));
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = LeUtils.getDensityDimen(getContext(), UI_HEIGHT);
		setMeasuredDimension(width, height);
		
		final int itemWidth = width / getColNum();
		for (LeToolbarButton button : getAllToolbarButtons()) {
			LeUtils.measureExactly(button, itemWidth, height);
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mSplitLineDrawable.setBounds(0, 0, getMeasuredWidth(), 0);
		mSplitLineDrawable.draw(canvas);
	}

}
