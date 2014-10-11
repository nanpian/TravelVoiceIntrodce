package com.museum.travel.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


public class LeUtils {

	public static final int UI_DEFAULT_STATUSBAR_HEIGHT = 24;

	private static Toast sToast;
	
	/** 澶栭儴鐗堟湰鍙�*/
	public static final String URL_PARAM_OUT_VERSION = "out_version";
	
	private static long sBeginTime;
	private static long sEndTime;
	
	private LeUtils() {}

	/**
	 * 寮哄埗瀵筕iew鍙婂叾瀛愯鍥捐繘琛岄�褰掑埛鏂�	 * 
	 * @param view
	 */
	public static void forceChildrenInvalidateRecursively(View view) {
		if (view instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) view;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceChildrenInvalidateRecursively(childView);
			}
		}
		if (view != null) {
			view.invalidate();
		}
	}

	public static View removeFromParent(View child) {
		if (child != null) {
			View parent = (View) child.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(child);
			}
		}
		return child;
	}

	public static float getDensity(Context context) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}

	public static int getDensityDimen(Context context, int dimen) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return ((int) (dimen * dm.density));
	}
	
	private static SparseArray<Integer> sDimenMap = new SparseArray<Integer>();
	
	public static int getResDimen(Context context, int resId) {
		if (sDimenMap.indexOfKey(resId) > 0) {
			return sDimenMap.get(resId);
		} else {
			int dimen = (int) context.getResources().getDimension(resId);
			sDimenMap.put(resId, dimen);
			return dimen;
		}
	}
	
	public static String getString(Context context, int resourceId) {
		if (context != null) {
			return context.getResources().getString(resourceId);
		}
		return null;
	}
	
	public static Bitmap getBitmap(Context context, int resId) {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
		return drawable.getBitmap();
	}
	
	public static final int centerSize(int total, int inside) {
		return (total - inside) >> 1;
	}

	public static final void layoutChildAbsoluteCenter(ViewGroup parent,
			View child, int tWidth, int tHeight) {
		int w, h, x, y;
		w = child.getMeasuredWidth();
		h = child.getMeasuredHeight();
		x = centerSize(tWidth, w);
		y = centerSize(tHeight, h);
		child.layout(x, y, x + w, y + h);
	}

	public static void layoutViewAtPos(View view, int offsetX, int offsetY) {
		view.layout(offsetX, offsetY, offsetX + view.getMeasuredWidth(), offsetY + view.getMeasuredHeight());
	}

	public static void measureExactly(View view, int width, int height) {
		int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
	}

	public static int getStatusbarHeight(Activity activity) {
		int statusbarHeight;
		try {
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			statusbarHeight = frame.top;
		} catch (Exception e) {
			statusbarHeight = getDensityDimen(activity, UI_DEFAULT_STATUSBAR_HEIGHT);
		}
		return statusbarHeight;
	}

	public static void showToast(Context context, int msgResId) {
		showToast(context, context.getString(msgResId));
	}
	
	public static void showToast(Context context, String msg) {
		final int padding = LeUtils.getDensityDimen(context, 6);
		TextView textView = new TextView(context);
		textView.setPadding(padding, padding, padding, padding);
		textView.setBackgroundColor(0xcc000000);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(14);
		textView.setText(msg);
		showToast(context, textView);
	}
	
	public static void showToast(Context context, View toastView) {
		showToast(context, toastView, Toast.LENGTH_SHORT);
	}
	
	public static void showToast(Context context, View toastView, int duration) {
		if (sToast == null) {
			sToast = new Toast(context);
		}

		if (Build.VERSION.SDK_INT < 14) {
			if (sToast != null) {
				sToast.cancel();
			}
		}
		if (sToast != null) {
			sToast.setView(toastView);
			sToast.setDuration(duration);
			sToast.show();
		}
	}

	public static void showInputMethod(final View anyView) {
		anyView.postDelayed(new Runnable() {
			@Override
			public void run() {
				final InputMethodManager imm = (InputMethodManager) anyView.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 100);
	}

	public static boolean isEmptyCollection(Collection<?> collection) {
		if (collection == null || collection.size() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyString(String str) {
		if (str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("null")) {
			return true;
		}
		return false;
	}

	/** 鍒ゆ柇瀛楃涓叉槸鍚︿负缃戝潃 */
	public static boolean checkStringIsUrl(String input) {
		if (input == null)
			return false;
		if (input.indexOf("tel://") == 0) {
			return true;
		}
		if (input.indexOf("mailto:") == 0 && input.contains("@")) {
			return true;
		}
		if (input.indexOf("wtai://") == 0) {
			return true;
		}
		Pattern pattern = Pattern.compile("(^((https|http|ftp|rtsp|mms)?://)"
				+ "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*"
				+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|"
				+ "(/[0-9a-z\\u4e00-\\u9fa5_!~*'().;?:@&=+$,%#-/]+)+/?)$)|(^file://*)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input.trim());
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static String urlCompletion(String srcUrl) {
		if (srcUrl.contains("://")) {
			return srcUrl;
		}
		srcUrl = "http://" + srcUrl;
		return srcUrl;
	}
	
	/**
	 * 鍒ゆ柇鏄惁涓虹郴缁熷簲鐢�	 * 
	 * need test
	 */
	public static boolean isSystemApplication(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo packageInfo = manager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			if(packageInfo != null && 
				(packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 缁橴RL鎷兼帴鍏叡鍙傛暟
	 */
	public static String processUrl(String url) {
		//TODO 娣诲姞鍏敤鍙傛暟
		return url;
	}
	
	public static boolean isLoading(int progress) {
		if (0 < progress && progress < 100) {
			return true;
		}
		return false;
	}
	
	public static boolean saveJPEGBitmap(Bitmap bmp, String path) {
		return saveBitmap(bmp, path, Bitmap.CompressFormat.JPEG);
	}
	
	public static boolean savePNGBitmap(Bitmap bmp, String path) {
		return saveBitmap(bmp, path, Bitmap.CompressFormat.PNG);
	}
	
	private static boolean saveBitmap(Bitmap bmp, String path, CompressFormat format) {
		if (bmp == null || path == null) {
			return false;
		}
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(format, 100, baos);
			fos.write(baos.toByteArray());
			baos.flush();
			fos.flush();
			baos.close();
			fos.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void markBegin() {
		sBeginTime = System.currentTimeMillis();
	}
	
	public static void markEnd() {
		markEnd("");
	}
	
	public static void markEnd(String tag) {
		sEndTime = System.currentTimeMillis();
	//	LeLog.e(tag + " ellapse:" + (sEndTime - sBeginTime));
	}
	
	public static void dumpTouchEvent(MotionEvent event, String tag) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
	//		LeLog.e(tag + "action down");
			break;
		case MotionEvent.ACTION_MOVE:
	//		LeLog.e(tag + "action move");
			break;
		case MotionEvent.ACTION_UP:
	//		LeLog.e(tag + "action up");
			break;
		case MotionEvent.ACTION_CANCEL:
	//		LeLog.e(tag + "action cancel");
			break;
		default:
			break;
		}
	}
	
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
			if (info != null) {
				ApplicationInfo appInfo = info.applicationInfo;
				appInfo.sourceDir = apkPath;
				appInfo.publicSourceDir = apkPath;

				return appInfo.loadIcon(pm);
			}
		} catch (Exception e) {
		//	LeLog.e("gyy:" + e.getLocalizedMessage());
		}
		return null;
	}
}
