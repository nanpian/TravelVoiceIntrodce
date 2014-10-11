package com.museum.travel.scanner;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
//import com.lenovo.browser.core.INoProGuard;

final class ViewfinderResultPointCallback implements ResultPointCallback {

	private final ViewfinderView viewfinderView;

	ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
		this.viewfinderView = viewfinderView;
	}

	@Override
	public void foundPossibleResultPoint(ResultPoint point) {
		viewfinderView.addPossibleResultPoint(point);
	}

}
