package com.museum.travel.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.museum.travel.util.INoProGuard;

public class LeBitmapUtil implements INoProGuard{
	
	private LeBitmapUtil() {}
	
	public static Bitmap getBitmap(Context context, int resId) {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
		return drawable.getBitmap();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// �?drawable 的长�? 
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// �?drawable 的颜色格�? 
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap  
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(w, h, config);
		} catch (OutOfMemoryError e) {
			//LeLog.w("bitmap outofmemory error");
		} catch (Exception e) {
			//LeLog.w("unknow exception");
		}
		// 建立对应 bitmap 的画�? 
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// �?drawable 内容画到画布�? 
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getSnapBitmap(View view) {
		/*if (LeMachineHelper.isHighSpeedPhone()) {
			return getSnapBitmap(view, Config.ARGB_8888);
		} else {*/
			return getSnapBitmap(view, Config.RGB_565);
		//}
	}

	public static Bitmap getSnapBitmap(View view, Config config) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), config);
			Canvas c = new Canvas(snapcache);
			c.drawColor(Color.WHITE);
			view.draw(c);
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			//LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			//LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return createDefaultBitmap();
	}

	public static Bitmap getSnapBitmapWithAlpha(View view) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth() + 1, view.getMeasuredHeight(),
					Config.ARGB_8888);

			Canvas c = new Canvas(snapcache);
			view.draw(c);
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			//LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			//LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return null;
	}

	public static Bitmap getShadowSnapBitmap(View view, int shadowSize) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth() + shadowSize * 2,
					view.getMeasuredHeight() + shadowSize * 2, Config.ARGB_8888);

			Canvas c = new Canvas(snapcache);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(0x00000000);
			paint.setShadowLayer(shadowSize, 0, 0, 0x66000000);
			c.drawRect(shadowSize, shadowSize, shadowSize + view.getMeasuredWidth(),
					shadowSize + view.getMeasuredHeight(), paint);

			c.save();
			c.translate(shadowSize, shadowSize);
			c.clipRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			view.draw(c);

			c.restore();
			c.clipRect(0, 0, c.getWidth(), c.getHeight());
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			//LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			//LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return null;
	}

	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null) {
		//	if (Build.VERSION.SDK_INT <= LeMachineHelper.RECYCLE_MAX_VERSION)
				bitmap.recycle();
		}
	}

	/**
	 * 依据colorPercent�?得到降低Alpha的画笔�?
	 * 
	 * @param alphaPercent
	 * @param backgroundColor
	 * @param paint
	 * @return
	 */
	public static Paint getDecendAlphaPainter(float alphaPercent, int backgroundColor, Paint paint) {
		int R = (backgroundColor & (0x00ff0000)) >> 16;
		int G = (backgroundColor & (0x0000ff00)) >> 8;
		int B = backgroundColor & (0x000000ff);
		final float[] array = new float[] { alphaPercent, 0, 0, 0, (1 - alphaPercent) * R, 0,
				alphaPercent, 0, 0, (1 - alphaPercent) * G, 0, 0, alphaPercent, 0,
				(1 - alphaPercent) * B, 0, 0, 0, 1, 0 };

		if (paint == null) {
			paint = new Paint();
		}
		paint.setColorFilter(new ColorMatrixColorFilter(array));
		return paint;
	}

	public static Bitmap getViewScreenShot(View view, Bitmap bitmap, int width, int height) {

		if (view == null) {
			return null;
		}

		if (bitmap != null
				&& (bitmap.getWidth() != view.getWidth() || bitmap.getHeight() != view.getHeight())) {
			recycleBitmap(bitmap);
			bitmap = null;
		}

		try {
			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
			}
			Canvas c = new Canvas(bitmap);
			c.translate(-view.getScrollX(), -view.getScrollY());
			view.draw(c);
		} catch (Exception e) {
			//LeLog.e(e.getMessage());
			return null;
		} catch (Error e) {
			//LeLog.e(e.getMessage());
			return null;
		}

		return bitmap;
	}
	
	private static Bitmap createDefaultBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		return bitmap;
	}
	
	/**
	 * 获取恰好等比覆盖�?��区域的图�?	 */
	public static Bitmap getSuitCoverBitmap(final Bitmap srcBitmap, final int width, final int height) {
		Bitmap desBitmap = srcBitmap;
		if (desBitmap != null) {
			int bWidht = srcBitmap.getWidth();
			int bHeight = srcBitmap.getHeight();
			if ((width > bWidht || height > bHeight) ||
				(width < bWidht && height < bHeight)) {
				float hScale = (float)width / bWidht;
				float vScale = (float)height / bHeight;
				float scale = (hScale > vScale) ? hScale : vScale;
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				desBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, bWidht, bHeight, matrix, true);
			}
		}
		return desBitmap;
	}
	
	/**
	 * 设置恰好等比覆盖的区�?	 */
	public static void setSuitCoverRect(Rect rect, int tWidth, int tHeight, int bWidht, int bHeight, float leftScale, float topScale) {
		float hScale = (float) tWidth / bWidht;
		float vScale = (float) tHeight / bHeight;
		if (hScale < vScale) {
			int suitWidth = (int) (bWidht * hScale / vScale);
			int offsetX = (int) ((bWidht - suitWidth) * leftScale);
			rect.set(offsetX, 0, offsetX + suitWidth, bHeight);
		} else {
			int suitHeight = (int) (bHeight * vScale / hScale);
			int offsetY = (int) ((bHeight - suitHeight) * topScale);
			rect.set(0, offsetY, bWidht, offsetY + suitHeight);
		}
	}
	
	/**
	 * 设置恰好等比覆盖的区�?	 */
	public static void setSuitCoverRect(Rect rect, int tWidth, int tHeight, int bWidht, int bHeight) {
		float hScale = (float) tWidth / bWidht;
		float vScale = (float) tHeight / bHeight;
		if (hScale < vScale) {
			int suitWidth = (int) (bWidht * hScale / vScale);
			rect.set(0, 0, suitWidth, bHeight);
		} else {
			int suitHeight = (int) (bHeight * vScale / hScale);
			rect.set(0, 0, bWidht, suitHeight);
		}
	}
	
	private static void gc() {
		System.gc();
	}
}
