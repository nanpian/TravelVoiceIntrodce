package com.museum.travel.scanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.museum.travel.util.LeUtils;
import com.museum.travel.R;
import com.museum.travel.ui.*;

public class LeCaptureView extends FrameLayout implements View.OnClickListener {
	
	private static final int UI_BUTTON_PADDING_TOP = 30;
	private static final int UI_FLASH_PADDING = 12;
	
	private static final int UI_BACK_BUTTON_WIDTH = 125;
	private static final int UI_BACK_BUTTON_HEIGHT = 34;
	private static final int UI_BACK_BUTTON_TEXT = 16;
	
	private SurfaceView mPreviewView;
	private ViewfinderView mViewfinderView;
	private LeResultView mResultView;
	private TextView mStatusView;
	private LeTextButton mBackButton;
	private LeIconButton mTorchButton;
	
	private boolean mIsTorchOn;
	
	private CameraManager cameraManager;
	private Rect mFrameRect;

	public LeCaptureView(Context context) {
		super(context);
		
		initResource();
		
		initViews();
	}

	private void initResource() {
		
	}
	
	private void initViews() {
		mPreviewView = new SurfaceView(getContext());
		addView(mPreviewView);
		
		mViewfinderView = new ViewfinderView(getContext());
		addView(mViewfinderView);
		
		mResultView = new LeResultView(getContext());
		addView(mResultView);
		
		mStatusView = new TextView(getContext());
//		addView(mStatusView);
		
		mBackButton = new LeTextButton(getContext(), R.string.common_back, UI_BACK_BUTTON_WIDTH, UI_BACK_BUTTON_HEIGHT, UI_BACK_BUTTON_TEXT);
		mBackButton.setNormalBgDrawable(R.drawable.text_button_bg);
		mBackButton.setPressBgDrawable(R.drawable.text_button_press_bg);
		mBackButton.setNormalColor(Color.WHITE);
		mBackButton.setPressColor(0x80ffffff);
		mBackButton.setOnClickListener(this);
		mBackButton.setVisibility(View.INVISIBLE);
		addView(mBackButton);
		
		mIsTorchOn = false;
		mTorchButton = new LeIconButton(getContext());
		mTorchButton.setOnClickListener(this);
		
		resetTorchButtonIcon();
	}
	
	public SurfaceView getPreviewView() {
		return mPreviewView;
	}
	
	public ViewfinderView getViewfinderView() {
		return mViewfinderView;
	}
	
	public LeResultView getResultView() {
		return mResultView;
	}
	
	public TextView getStatusView() {
		return mStatusView;
	}
	
	public View getBackButton() {
		return mBackButton;
	}
	
	public View getTorchButton() {
		return mTorchButton;
	}
	
	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
		mFrameRect = cameraManager.getFramingRect();
	}
	
	public void addTorchButton() {
		Camera camera = cameraManager.getCamera();
		Camera.Parameters parameters = camera.getParameters();
		if (parameters.getSupportedFlashModes() != null) {
			LeUtils.removeFromParent(mTorchButton);
			addView(mTorchButton);
		}
	}
	
	public void setIsTorchOn(boolean isTorchOn) {
		mIsTorchOn = isTorchOn;
		resetTorchButtonIcon();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mBackButton)) {
			CaptureActivity.sInstance.finish();
			CaptureActivity.sInstance.overridePendingTransition(0, 0);
		} else if (v.equals(mTorchButton)) {
			mIsTorchOn = !mIsTorchOn;
			resetTorchButtonIcon();
			if (mIsTorchOn) {
				cameraManager.setTorch(true);
			} else {
				cameraManager.setTorch(false);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		mBackButton.measure(0, 0);
		mTorchButton.measure(0, 0);
		
		cameraManager.getCameraConfigurationManager().setScreenResolution(getMeasuredWidth(), getMeasuredHeight());
		cameraManager.setFrameingRect(null);
		cameraManager.setFramingRectInPreview(null);
		
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		int offsetX, offsetY;
		if (cameraManager != null && cameraManager.getFramingRect() != null) {
			offsetX = (getMeasuredWidth() - mBackButton.getMeasuredWidth()) / 2;
			offsetY = cameraManager.getFramingRect().bottom + LeUtils.getDensityDimen(getContext(), UI_BUTTON_PADDING_TOP);
			LeUtils.layoutViewAtPos(mBackButton, offsetX, offsetY);
		} else {
			mBackButton.layout(0, 0, 0, 0);
		}
		
		final int flashPadding = LeUtils.getDensityDimen(getContext(), UI_FLASH_PADDING);
		offsetX = getMeasuredWidth() - flashPadding - mTorchButton.getMeasuredWidth();
		offsetY = flashPadding;
		LeUtils.layoutViewAtPos(mTorchButton, offsetX, offsetY);
		
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
        int previewWidth = width;
        int previewHeight = height;
        if (cameraManager != null) {
        	Point previewSize = cameraManager.getCameraConfigurationManager().getCameraResolution();
	        if (previewSize != null) {
	            previewWidth = previewSize.y;
	            previewHeight = previewSize.x;
	        } else {
	        	//LeLog.e("gyy: previewsize is null");
	        }
	
	        // Center the child SurfaceView within the parent.
	        if (width * previewHeight > height * previewWidth) {
	            final int scaledChildHeight = previewHeight * width / previewWidth;
	            mPreviewView.layout(0, (height - scaledChildHeight) / 2,
	                    width, (height + scaledChildHeight) / 2);
	        } else {
	            final int scaledChildWidth = previewWidth * height / previewHeight;
	            mPreviewView.layout((width - scaledChildWidth) / 2, 0,
	                    (width + scaledChildWidth) / 2, height);
	        }
        }
	}
	
	private void resetTorchButtonIcon() {
		if (mIsTorchOn) {
			mTorchButton.setIcon(R.drawable.torch_on);
			mTorchButton.setPressIcon(R.drawable.torch_on);
		} else {
			mTorchButton.setIcon(R.drawable.torch_off);
			mTorchButton.setPressIcon(R.drawable.torch_off);
		}
		mTorchButton.postInvalidate();
	}
}
