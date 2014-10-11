package com.museum.travel.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

import com.museum.travel.util.LeUtils;

public class LeToolbar extends ViewGroup {

	public static final int DEFAULT_COL_NUM = 6;
	
	public static final int UI_HEIGHT = 44;
	
	private static final int UI_BUTTON_WIDTH = 56;
	private static final int UI_BUTTON_HEIGHT = 32;
	
	protected int mColNum;

	private List<LeToolbarButton> mToolbarButtons;

	public LeToolbar(Context context) {
		super(context);

		mColNum = DEFAULT_COL_NUM;

		mToolbarButtons = new ArrayList<LeToolbarButton>();

		setClickable(true);
	}
	
	public void addToolbarButton(LeToolbarButton button) {
		addView(button);
		mToolbarButtons.add(button);
	}
	
	public List<LeToolbarButton> getAllToolbarButtons() {
		return mToolbarButtons;
	}
	
	public LeToolbarButton getToolbarButtonById(int id) {
		for (LeToolbarButton button : mToolbarButtons) {
			if (button.getId() == id) {
				return button;
			}
		}
		return null;
	}
	
	public int getColNum() {
		return mColNum;
	}
	
	public void setColNum(int colNum) {
		mColNum = colNum;
	}
	
	public static int getFixedHeight(Context context) {
		return LeUtils.getDensityDimen(context, UI_HEIGHT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = LeUtils.getDensityDimen(getContext(), UI_HEIGHT);
		setMeasuredDimension(width, height);
		
		final int buttonWidth = LeUtils.getDensityDimen(getContext(), UI_BUTTON_WIDTH);
		final int buttonHeight = LeUtils.getDensityDimen(getContext(), UI_BUTTON_HEIGHT);
		for (LeToolbarButton button : mToolbarButtons) {
			LeUtils.measureExactly(button, buttonWidth, buttonHeight);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mToolbarButtons.size() == 0) {
			return;
		}
		final int itemWidth = getMeasuredWidth() / mColNum;
		int offsetX, offsetY;
		offsetX = (itemWidth - mToolbarButtons.get(0).getMeasuredWidth()) / 2;
		offsetY = (getMeasuredHeight() - mToolbarButtons.get(0).getMeasuredHeight()) / 2;

		for (LeToolbarButton button : mToolbarButtons) {
			if (button.getPos() < 0) {
				LeUtils.layoutViewAtPos(button, offsetX, offsetY);
				offsetX += itemWidth;
			} 
		}
		
		for (LeToolbarButton button : mToolbarButtons) {
			if (button.getPos() >= 0) {
				offsetX = (itemWidth - mToolbarButtons.get(0).getMeasuredWidth()) / 2 + button.getPos() * itemWidth;
				LeUtils.layoutViewAtPos(button, offsetX, offsetY);
			}
		}
	}

}
