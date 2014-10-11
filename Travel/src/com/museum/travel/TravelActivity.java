package com.museum.travel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @Author yizhantong
 */
public class TravelActivity extends Activity {
	
	ImageView travelbtn;
	ImageView onlinebtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel);
		travelbtn = (ImageView) findViewById(R.id.travel);
		onlinebtn = (ImageView) findViewById(R.id.online);
		travelbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent list = new Intent();
				list.setClass(TravelActivity.this, TListActivity.class);
				startActivity(list);
			}
			
		});
		
		onlinebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent map = new Intent();
				map.setClass(TravelActivity.this, TMapActivity.class);
				startActivity(map);
			}
			
		});
	
	}
}
