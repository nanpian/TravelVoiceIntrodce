package com.museum.travel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MapView extends SurfaceView implements SurfaceHolder.Callback{
    
	TMapActivity activity;
	Bitmap helpBitmap;
	SurfaceHolder surfaceholder;
	
	public MapView(Context mcontext ,Bitmap bitmap ) {
		super(mcontext);
		//this.activity = activity;
		surfaceholder = this.getHolder();
		getHolder().addCallback(this);
		this.helpBitmap = bitmap;
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(helpBitmap, 0, 10 , null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	

}
