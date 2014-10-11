package com.museum.travel.scanner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.museum.travel.R;
import com.museum.travel.ui.LeTheme;
import com.museum.travel.util.LeBitmapUtil;
import com.museum.travel.util.LeTextUtil;
import com.museum.travel.util.LeUtils;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final long ANIMATION_DELAY = 80L;
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 20;
	private static final int POINT_SIZE = 6;
	
	private static final int UI_STROKE_WIDTH = 2;
	private static final int UI_TITLE_SIZE = 18;
	private static final int UI_TITLE_PADDING_BOTTOM = 40;
	
	private CameraManager cameraManager;
	
	private final Paint paint;
	private final Paint mTitlePaint;
	private final Paint mBorderPaint;
	
	private Bitmap mShineLine;
	private String mScanTip;
	private final int maskColor;
	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;
	private int mLinePaddingTop = 0;
	private Rect mFrame;
	
	public ViewfinderView(Context context) {
		this(context, null);
	}

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setWillNotDraw(false);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		mTitlePaint = new Paint();
		mTitlePaint.setAntiAlias(true);
		mTitlePaint.setTextSize(LeUtils.getDensityDimen(getContext(), UI_TITLE_SIZE));
		mTitlePaint.setColor(LeTheme.getColor(getContext(), R.color.common_contrast));
		
		mBorderPaint = new Paint();
		mBorderPaint.setStrokeWidth(LeUtils.getDensityDimen(getContext(), UI_STROKE_WIDTH));
		mBorderPaint.setColor(LeTheme.getColor(getContext(), R.color.frame_shine));
		mBorderPaint.setStyle(Style.STROKE);
		
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;
		
		mShineLine = LeBitmapUtil.getBitmap(getContext(), R.drawable.scan_shine_line);
		mScanTip = getResources().getString(R.string.msg_default_status);
		
		mFrame = new Rect();
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null || cameraManager.getFramingRect() == null) {
			return; // not ready yet, early draw before done configuring
		}
		mFrame.left = cameraManager.getFramingRect().left;
		mFrame.right = cameraManager.getFramingRect().right;
		mFrame.top = cameraManager.getFramingRect().top;
		mFrame.bottom = cameraManager.getFramingRect().bottom;
		final int frameBorder = LeUtils.getDensityDimen(getContext(), cameraManager.FRAME_BORDER);
		mFrame.inset(frameBorder, frameBorder);
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(maskColor);
		canvas.drawRect(0, 0, width, mFrame.top, paint);
		canvas.drawRect(0, mFrame.top, mFrame.left, mFrame.bottom + 1, paint);
		canvas.drawRect(mFrame.right + 1, mFrame.top, width, mFrame.bottom + 1,
				paint);
		canvas.drawRect(0, mFrame.bottom + 1, width, height, paint);

		canvas.drawRect(mFrame, mBorderPaint);

		int offsetX, offsetY;

		// draw title
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mTitlePaint, mScanTip);
		offsetY = mFrame.top - LeUtils.getDensityDimen(getContext(), UI_TITLE_PADDING_BOTTOM);
		canvas.drawText(mScanTip, offsetX, offsetY, mTitlePaint);


		offsetX = mFrame.left + (mFrame.width() - mShineLine.getWidth()) / 2;
		mLinePaddingTop += 3;
		if (mLinePaddingTop >= mFrame.height()) {
			mLinePaddingTop = 0;
		}
		offsetY = mFrame.top + mLinePaddingTop;
		canvas.drawBitmap(mShineLine, offsetX, offsetY, null);

		Point cameraResolution = cameraManager.getCameraConfigurationManager().getCameraResolution();
		Point screenResolution = cameraManager.getCameraConfigurationManager().getScreenResolution();
		float ratioX = (float) cameraResolution.x / screenResolution.y;
		float ratioY = (float) cameraResolution.y / screenResolution.x;
		
		List<ResultPoint> currentPossible = possibleResultPoints;
//		List<ResultPoint> currentLast = lastPossibleResultPoints;
		int frameLeft = mFrame.left;
		int frameTop = mFrame.top;
		if (currentPossible.isEmpty()) {
			lastPossibleResultPoints = null;
		} else {
			possibleResultPoints = new ArrayList<ResultPoint>(5);
			lastPossibleResultPoints = currentPossible;
			paint.setAlpha(CURRENT_POINT_OPACITY);
			paint.setColor(resultPointColor);
			synchronized (currentPossible) {
				for (ResultPoint point : currentPossible) {
					int x = frameLeft + (cameraManager.getFramingRect().width() - (int) (point.getY() / ratioY));
					int y = frameTop + (int) (point.getX() / ratioX);
					canvas.drawCircle(x, y, POINT_SIZE, paint);
				}
			}
		}
//		if (currentLast != null) {
//			paint.setAlpha(CURRENT_POINT_OPACITY / 2);
//			paint.setColor(resultPointColor);
//			synchronized (currentLast) {
//				float radius = POINT_SIZE / 2.0f;
//				for (ResultPoint point : currentLast) {
//					canvas.drawCircle(
//							frameLeft + (int) (point.getX() * scaleX), frameTop
//									+ (int) (point.getY() * scaleY), radius,
//							paint);
//				}
//			}
//		}

		// Request another update at the animation interval, but only
		// repaint the laser line,
		// not the entire viewfinder mask.
		postInvalidateDelayed(ANIMATION_DELAY, mFrame.left - POINT_SIZE,
				mFrame.top - POINT_SIZE, mFrame.right + POINT_SIZE, mFrame.bottom
						+ POINT_SIZE);
	}


	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}




}
