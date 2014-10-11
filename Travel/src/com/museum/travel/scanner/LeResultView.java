package com.museum.travel.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.museum.travel.R;
import com.museum.travel.ui.LeFrameToolbar;
import com.museum.travel.ui.LeScrollView;
import com.museum.travel.ui.LeSplitLineDrawable;
import com.museum.travel.ui.LeTextButton;
import com.museum.travel.ui.LeTheme;
import com.museum.travel.ui.LeToolbar;
import com.museum.travel.ui.LeToolbarTextButton;
import com.museum.travel.util.LeBitmapUtil;
import com.museum.travel.util.LeUtils;
import com.museum.travel.util.Utils;
import com.museum.travel.util.LeUtils;
import com.museum.travel.util.LeTextUtil;
import com.museum.travel.util.LeColorUtil;

public class LeResultView extends ViewGroup implements View.OnClickListener {

	public static final int TOOLBAR_ID_BACK = 0;

	private static final int UI_TITLE = 16;
	private static final int UI_CONTENT_PADDING_TOP = 10;
	private static final int UI_CONTENT_PADDING_LEFT = 26;

	private LeToolbar mToolbar;
	private LeResultContentView mContentView;

	private int mTitleHeight;
	private LeSplitLineDrawable mTitleSplitLineDrawable;
	private String mTitle;
	private Paint mPaint;

	private Matrix mMatrix;

	private Bitmap mResultBitmap;

	public LeResultView(Context context) {
		super(context);

		setWillNotDraw(false);

		initResources();

		initViews();

		setBackgroundColor(LeTheme.getColor(getContext(), R.color.common_background));
	}

	private void initResources() {
		mTitleHeight = LeUtils.getResDimen(getContext(), R.dimen.titlebar_height);

		mTitle = getResources().getString(R.string.scanner_result);

		mResultBitmap = LeBitmapUtil.getBitmap(getContext(), R.drawable.kongzi);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(LeTheme.getColor(getContext(), R.color.common_white));
		mPaint.setTextSize(LeUtils.getDensityDimen(getContext(), UI_TITLE));

		mTitleSplitLineDrawable = new LeSplitLineDrawable(getContext(), false);
		mTitleSplitLineDrawable.setLineColor(LeTheme.getColor(getContext(), R.color.titlebar_bottom_line));

		mMatrix = new Matrix();
	}

	private void initViews() {
		mToolbar = new LeFrameToolbar(getContext());
		mToolbar.setBackgroundColor(LeTheme.getColor(getContext(), R.color.toolbar_bg));
		mToolbar.setColNum(5);
		addView(mToolbar);

		LeToolbarTextButton toolbarButton = new LeToolbarTextButton(getContext());
		toolbarButton.setOnClickListener(this);
		toolbarButton.setId(TOOLBAR_ID_BACK);
		toolbarButton.setText(R.string.common_back);
		toolbarButton.setPos(4);
		mToolbar.addToolbarButton(toolbarButton);

		mContentView = new LeResultContentView(getContext());
		addView(mContentView);
	}

	public void setResultBitmap(Bitmap resultBitmap) {
		if (resultBitmap != null) {
			mResultBitmap = resultBitmap;
		} else {
			mResultBitmap = LeBitmapUtil.getBitmap(getContext(), R.drawable.kongzi);
		}
		requestLayout();
		postInvalidate();
	}

	public void setResultText(String resultText) {
		mContentView.setResultText(resultText);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);

		LeUtils.measureExactly(mToolbar, width, 0);

		int contentPaddingX = LeUtils.getDensityDimen(getContext(), UI_CONTENT_PADDING_LEFT);
		int contentPaddingY = LeUtils.getDensityDimen(getContext(), UI_CONTENT_PADDING_TOP);
		int contentHeight = getMeasuredHeight() - mToolbar.getMeasuredHeight() - mTitleHeight;
		contentHeight -= 2 * contentPaddingY;
		int contentWidth = getMeasuredWidth() - 2 * contentPaddingX;
		LeUtils.measureExactly(mContentView, contentWidth, contentHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetX = 0;
		offsetY = mTitleHeight;

		offsetY = getMeasuredHeight() - mToolbar.getMeasuredHeight();
		LeUtils.layoutViewAtPos(mToolbar, offsetX, offsetY);

		offsetX = LeUtils.getDensityDimen(getContext(), UI_CONTENT_PADDING_LEFT);
		offsetY = mTitleHeight + LeUtils.getDensityDimen(getContext(), UI_CONTENT_PADDING_TOP);
		LeUtils.layoutViewAtPos(mContentView, offsetX, offsetY);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int bgHeight = LeUtils.getResDimen(getContext(), R.dimen.background_height);
		//LeControlCenter.getInstance().getBackground().setBounds(0, 0, getMeasuredWidth(), bgHeight);
		//LeControlCenter.getInstance().getBackground().draw(canvas);

		int titleHeight = LeUtils.getResDimen(getContext(), R.dimen.titlebar_height) - 2;
		mTitleSplitLineDrawable.setBounds(0, titleHeight, getMeasuredWidth(), titleHeight);
		mTitleSplitLineDrawable.draw(canvas);

		int offsetX, offsetY;
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mTitle);
		offsetY = LeTextUtil.calcYWhenAlignCenter(mTitleHeight, mPaint);
		canvas.drawText(mTitle, offsetX, offsetY, mPaint);
	}
	
	@Override
	public void onClick(View v) {
		if (v instanceof LeToolbarTextButton) {
			CaptureActivity.sInstance.restartPreviewAfterDelay(0L);
		}
	}

	public class LeResultContentView extends ViewGroup implements OnClickListener{

		private static final int UI_RESULT_BITMAP_PADDING_TOP = 19;
		private static final int UI_SHADOW_X = 4;
		private static final int UI_TEXT_TITLE_PADDING_X = 22;
		private static final int UI_TEXT_TITLE_PADDING_TOP = 29;
		private static final int UI_TEXTVIEW_PADDING_X = 36;
		private static final int UI_TEXTVIEW_PADDING_TOP = 11;
		private static final int UI_TEXT_SIZE = 15;
		private static final int UI_TEXTVIEW_PADDING_BOTTOM = 30;
		private static final int UI_BUTTON_HEIGHT = 33;
		private static final int UI_BUTTON_PADDING_X = 23;
		private static final int UI_BUTTON_PADDING_BOTTOM = 19;
		
		private TextView mResultTextView;
		private LeTextButton mCopyButton;
		private LeScrollView mTextViewScroll;
		
		private int mShadowX;
		private int mBitmapPaddingTop;
		private int mTextTitlePaddingX;
		private int mTextTitlePaddingTop;
		private int mTextviewPaddingX;
		private int mTextviewPaddingTop;
		private int mTextviewPaddingBottom;
		private int mButtonHeight;
		private int mButtonPaddingX;
		private int mButtonPaddingBottom;
		private int mSplitLinePaddingTop;
		
		private LeSplitLineDrawable mSplitLineDrawable;
		private Paint mTextTitlePaint;
		private String mTextTitle;

		public LeResultContentView(Context context) {
			super(context);
			
			initResources();
			initViews();
			
			setBackgroundResource(R.drawable.unit_bg);
		}
		
		private void initResources() {
			mSplitLineDrawable = new LeSplitLineDrawable(getContext());
			
			mShadowX = LeUtils.getDensityDimen(getContext(), UI_SHADOW_X);
			mBitmapPaddingTop = LeUtils.getDensityDimen(getContext(), UI_RESULT_BITMAP_PADDING_TOP);
			mTextTitlePaddingTop = LeUtils.getDensityDimen(getContext(), UI_TEXT_TITLE_PADDING_TOP);
			mTextTitlePaddingX = LeUtils.getDensityDimen(getContext(), UI_TEXT_TITLE_PADDING_X);
			mTextviewPaddingX = LeUtils.getDensityDimen(getContext(), UI_TEXTVIEW_PADDING_X);
			mTextviewPaddingTop = LeUtils.getDensityDimen(getContext(), UI_TEXTVIEW_PADDING_TOP);
			mButtonPaddingBottom = LeUtils.getDensityDimen(getContext(), UI_BUTTON_PADDING_BOTTOM);
			mTextviewPaddingBottom = LeUtils.getDensityDimen(getContext(), UI_TEXTVIEW_PADDING_BOTTOM);
			mButtonPaddingX = LeUtils.getDensityDimen(getContext(), UI_BUTTON_PADDING_X);
			mButtonHeight = LeUtils.getDensityDimen(getContext(), UI_BUTTON_HEIGHT);
			
			mSplitLinePaddingTop = mBitmapPaddingTop * 2 + mResultBitmap.getWidth();
			
			mTextTitlePaint = new Paint();
			mTextTitlePaint.setTextSize(LeUtils.getDensityDimen(getContext(), UI_TEXT_SIZE));
			mTextTitlePaint.setColor(LeTheme.getColor(getContext(), R.color.common_text));
			
			mTextTitle = getResources().getString(R.string.msg_text);
		}

		private void initViews() {
			mResultTextView = new TextView(getContext());
			mResultTextView.setBackgroundColor(Color.TRANSPARENT);
			mResultTextView.setPadding(mTextviewPaddingX, 0, mTextviewPaddingX, mTextviewPaddingBottom);
			mResultTextView.setTextColor(LeTheme.getColor(getContext(), R.color.common_black));
			mResultTextView.setTextSize(UI_TEXT_SIZE);
			
			mTextViewScroll = new LeScrollView(getContext());
			mTextViewScroll.addView(mResultTextView);
			addView(mTextViewScroll);

			mCopyButton = new LeTextButton(getContext(), R.string.common_copy);
			mCopyButton.setNormalBgDrawable(R.drawable.text_button_bg);
			mCopyButton.setPressBgDrawable(R.drawable.text_button_bg);
			mCopyButton.setNormalColor(Color.WHITE);
			mCopyButton.setTextSize(UI_TEXT_SIZE);
			mCopyButton.setOnClickListener(this);
			addView(mCopyButton);
		}
		
		public void setResultText(String text) {
			mResultTextView.setText(text);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

			mSplitLinePaddingTop = mBitmapPaddingTop * 2 + mResultBitmap.getWidth();
			
			int buttonWidth = measuredWidth - 2 * mButtonPaddingX;
			mCopyButton.measure(buttonWidth, mButtonHeight);
			
			measureMessageView(measuredWidth, measuredHeight);
			
			int scrollHeight = calcScrollHeight(measuredHeight);
			LeUtils.measureExactly(mTextViewScroll, measuredWidth, scrollHeight);
			
			int height = (int) (mButtonHeight + mButtonPaddingBottom + mTextTitlePaint.getTextSize());
			height += scrollHeight+ mSplitLinePaddingTop + mTextTitlePaddingTop + mTextviewPaddingTop;
			setMeasuredDimension(measuredWidth, height);
		}
		
		private int calcScrollHeight(int maxContentHeight) {
			int maxHeight = (int) (maxContentHeight - mSplitLinePaddingTop - mTextTitlePaddingTop - mTextviewPaddingTop);
			maxHeight = (int) (maxHeight - mButtonHeight - mButtonPaddingBottom - mTextTitlePaint.getTextSize());
			return Math.min(maxHeight, mResultTextView.getMeasuredHeight());
		}
		
		private void measureMessageView(int width, int maxHeight) {
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			mResultTextView.measure(widthMeasureSpec, heightMeasureSpec);
		}
		
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int offsetX = (getMeasuredWidth() - mCopyButton.getMeasuredWidth()) / 2;
			int offsetY = getMeasuredHeight() - mButtonPaddingBottom - mButtonHeight;
			LeUtils.layoutViewAtPos(mCopyButton, offsetX, offsetY);
			
			offsetX = 0;
			offsetY = mSplitLinePaddingTop + mTextTitlePaddingTop + mTextviewPaddingTop;
			LeUtils.layoutViewAtPos(mTextViewScroll, offsetX, offsetY);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			int offsetX = (getMeasuredWidth() - mResultBitmap.getWidth()) / 2;
			int offsetY = mBitmapPaddingTop;
			mMatrix.reset();
			mMatrix.postRotate(90, mResultBitmap.getWidth() / 2.0f, mResultBitmap.getHeight() / 2.0f);
			mMatrix.postTranslate(offsetX, offsetY + (mResultBitmap.getWidth() - mResultBitmap.getHeight())
					/ 2.0f);
			canvas.drawBitmap(mResultBitmap, mMatrix, null);
			
			offsetY = mSplitLinePaddingTop;
			mSplitLineDrawable.setBounds(mShadowX, offsetY, getMeasuredWidth() - mShadowX, offsetY);
			mSplitLineDrawable.draw(canvas);
			
			offsetY += mTextTitlePaddingTop;
			offsetX = mTextTitlePaddingX;
			canvas.drawText(mTextTitle, offsetX, offsetY, mTextTitlePaint);
		}

		@Override
		public void onClick(View v) {
			if (v.equals(mCopyButton)) {
				ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(
						Context.CLIPBOARD_SERVICE);
				clipboard.setText(mResultTextView.getText());
				LeUtils.showToast(getContext(), R.string.copy_to_clipboard);
				CaptureActivity.sInstance.finish();
			} else if (v instanceof LeToolbarTextButton) {
				CaptureActivity.sInstance.restartPreviewAfterDelay(0L);
			}
		}
	}
}
