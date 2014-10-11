package com.museum.travel.scanner;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ResultParser;
import com.museum.travel.R;
import com.museum.travel.util.LeUtils;


/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {
	
	public static final int MSG_DECODE = 0;
	public static final int MSG_DECODE_FAILED = 1;
	public static final int MSG_DECODE_SUCCEEDED = 2;
	public static final int MSG_LAUNCH_PRODUCT_QUERY = 3;
	public static final int MSG_QUIT = 4;
	public static final int MSG_RESTART_PREVIEW = 5;
	public static final int MSG_RETURN_SCAN_RESULT = 6;
	
	private static final String TAG = CaptureActivity.class.getSimpleName();

	public static CaptureActivity sInstance;

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	
	private LeCaptureView mCaptureView;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private LeResultView resultView;
	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	
	private boolean mIsNotFromPause;
	
	enum IntentSource {
		NATIVE_APP_INTENT, NONE
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
	
	public LeCaptureView getCaptureView() {
		return mCaptureView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		assignInstance(this);
		//LeBasicContainer.switchActivity(this);

		mIsNotFromPause = true;
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mCaptureView = new LeCaptureView(this);
		setContentView(mCaptureView);

		hasSurface = false;
	}
	
	private static void assignInstance(CaptureActivity activity) {
		sInstance = activity;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//LeBasicContainer.switchActivity(this);
		
		mIsNotFromPause = true;

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		viewfinderView = mCaptureView.getViewfinderView();
		mCaptureView.setCameraManager(cameraManager);
		viewfinderView.setCameraManager(cameraManager);
		viewfinderView.postInvalidate();

		resultView = mCaptureView.getResultView();
		statusView = mCaptureView.getStatusView();

		handler = null;
		lastResult = null;

		resetStatusView();

		SurfaceView surfaceView = mCaptureView.getPreviewView();
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {
		
		mIsNotFromPause = false;
		
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		cameraManager.closeDriver();
		mCaptureView.setIsTorchOn(false);
		if (!hasSurface) {
			SurfaceView surfaceView = mCaptureView.getPreviewView();
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (source == IntentSource.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				this.overridePendingTransition(0,0);
				return true;
			}
			if ((source == IntentSource.NONE)
					&& lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, MSG_DECODE_SUCCEEDED, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		if(!mIsNotFromPause){
			hasSurface = true;
			return;
		}
		
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		lastResult = rawResult;
		ResultHandler resultHandler = new ResultHandler(this, ResultParser.parseResult(lastResult), lastResult);

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// historyManager.addHistoryItem(rawResult, resultHandler);
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			drawResultPoints(barcode, scaleFactor, rawResult);
		}

		switch (source) {
		case NATIVE_APP_INTENT:
		case NONE:
			handleDecodeInternally(rawResult, resultHandler, barcode);
			break;
		}
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor,
			Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(scaleFactor * point.getX(), scaleFactor
							* point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
					scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		String content = resultHandler.getDisplayContents().toString();
		if (LeUtils.checkStringIsUrl(content)) {
			//zhudw3 modified
			//LeControlCenter.getInstance().goUrl(content);
			finish();
		} else {
			cameraManager.stopAutoFocus();
			
			
			mCaptureView.getBackButton().setVisibility(View.GONE);
			mCaptureView.getTorchButton().setVisibility(View.GONE);
			statusView.setVisibility(View.GONE);
			viewfinderView.setVisibility(View.GONE);
			
			resultView.setVisibility(View.VISIBLE);
			resultView.setResultBitmap(barcode);
			resultView.setResultText(content);
		}


//		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
//		if (barcode == null) {
//			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(
//					getResources(), R.drawable.ic_launcher_browser));
//		} else {
//			barcodeImageView.setImageBitmap(barcode);
//		}
//
//		TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
//		formatTextView.setText(rawResult.getBarcodeFormat().toString());
//
//		TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
//		typeTextView.setText(resultHandler.getType().toString());
//
//		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
//				DateFormat.SHORT);
//		String formattedTime = formatter.format(new Date(rawResult
//				.getTimestamp()));
//		TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
//		timeTextView.setText(formattedTime);
//
//		TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
//		View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
//		metaTextView.setVisibility(View.GONE);
//		metaTextViewLabel.setVisibility(View.GONE);
//		Map<ResultMetadataType, Object> metadata = rawResult
//				.getResultMetadata();
//		if (metadata != null) {
//			StringBuilder metadataText = new StringBuilder(20);
//			for (Map.Entry<ResultMetadataType, Object> entry : metadata
//					.entrySet()) {
//				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
//					metadataText.append(entry.getValue()).append('\n');
//				}
//			}
//			if (metadataText.length() > 0) {
//				metadataText.setLength(metadataText.length() - 1);
//				metaTextView.setText(metadataText);
//				metaTextView.setVisibility(View.VISIBLE);
//				metaTextViewLabel.setVisibility(View.VISIBLE);
//			}
//		}
//
//		TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
//		CharSequence displayContents = resultHandler.getDisplayContents();
//		contentsTextView.setText(displayContents);
//		// Crudely scale betweeen 22 and 32 -- bigger font for shorter text
//		int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
//		contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
//
//		int buttonCount = resultHandler.getButtonCount();
//		ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
//		buttonView.requestFocus();
//		for (int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {
//			TextView button = (TextView) buttonView.getChildAt(x);
//			if (x < buttonCount) {
//				button.setVisibility(View.VISIBLE);
//				button.setText(resultHandler.getButtonText(x));
//				button.setOnClickListener(new ResultButtonListener(
//						resultHandler, x));
//			} else {
//				button.setVisibility(View.GONE);
//			}
//		}
//
//		if (!resultHandler.areContentsSecure()) {
//			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//			if (displayContents != null) {
//				try {
//					clipboard.setText(displayContents);
//				} catch (NullPointerException npe) {
//					// Some kind of bug inside the clipboard implementation, not
//					// due to null input
//					Log.w(TAG, "Clipboard bug", npe);
//				}
//			}
//		}
	}


	private void initCamera(SurfaceHolder surfaceHolder) {
		//LeLog.i("CaptureActivity init camera");
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("È·¶¨£¿", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(MSG_RESTART_PREVIEW, delayMS);
		}
		cameraManager.startAutoFocus();
		resetStatusView();
	}

	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		mCaptureView.getBackButton().setVisibility(View.VISIBLE);
		mCaptureView.getTorchButton().setVisibility(View.VISIBLE);
		lastResult = null;
	}
}
