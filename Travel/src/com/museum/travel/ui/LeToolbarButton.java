package com.museum.travel.ui;

import android.content.Context;

public class LeToolbarButton extends LeIconButton {
	
	private int mPos = -1;

	public LeToolbarButton(Context context) {
		super(context);
	}
	
	public int getPos() {
		return mPos;
	}

	public void setPos(int pos) {
		mPos = pos;
	}
}
