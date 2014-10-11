package com.museum.travel;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.museum.travel.TListActivity.MyTask;
import com.museum.travel.util.HttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;


public class TMapActivity extends Activity {
	
	private ProgressDialog progressDialog;  
	List<Map<String,Object>> listt ;
	private SurfaceView sf;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tmap);
		sf = (SurfaceView) findViewById(R.id.ditu);
		//ditu.addView(child);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("正在下载....");
		new MyTask().execute(HttpUtil.BASE_URL);
	}
	

	
	
	public class MyTask extends AsyncTask<String, Void, String>{
		private Bitmap ditu;
		private List<Map<String, Object>> list;
		private List<Map<String, Object>> listxx = new ArrayList<Map<String ,Object>>();

		@Override		
		protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();	
		progressDialog.show();
		}

		@Override	
		protected void onPostExecute(String  result) {		
		// TODO Auto-generated method stub		
		super.onPostExecute(result);
		if(result==null)
		{
		   progressDialog.dismiss();
		} else {
			progressDialog.dismiss();
		    initBitmap(ditu , listxx);
		}
		}

		@Override	
		protected String doInBackground(String... params) {		
		// TODO Auto-generated method stub		
		String str = HttpUtil.getRequest(params[0], getApplicationContext());
		String rt = "xxx";
		if ((str==null)||(str.equals("xxxx"))){
		   return null;
		} else {
			try {
				JSONObject jsonObject = new JSONObject(str);
				
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				
				for (int i = 0; i < jsonArray.length(); i++) {
				
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				
				Map<String ,Object> map = new HashMap<String, Object>();
				
				Iterator<String> iterator = jsonObject2.keys();
				
				while (iterator.hasNext()) {
				
				  String key = iterator.next();
				
				  Object value = jsonObject2.get(key);
				
				  map.put(key, value);

			      }

				  listxx.add(map);

			    }
				rt = jsonObject.getString("map_url");
	            String mapurl = rt;
			    ImageLoader imageload = new ImageLoader(getBaseContext());
			    ditu = imageload.getBitmap(mapurl);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
          
		}
	//	list2 = HttpUtil.getRequest2List(str, "summary");	
		return rt;		
		}

	}
	
	class biaojiclass {
		int x;
		int y;
		String summary;
		String code;
		
		public biaojiclass ()
		{
			super();
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	List<biaojiclass> bjArray = new ArrayList<biaojiclass>();

	public void initBitmap(Bitmap ditu, List<Map<String, Object>> listxx) {
		// TODO Auto-generated method stub
		// MapView mapview = new MapView(getApplicationContext(),ditu);
		// LinearLayout ditu2 = (LinearLayout)findViewById(R.id.ditu);
		// ditu2.addView(mapview, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));	
        int count = listxx.size();
        Bitmap biaoji = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.biaoji);
		 SurfaceHolder sfh = sf.getHolder();
		 int w = sf.getWidth();
		 int h = sf.getHeight();
		 Paint p = new Paint(); // 笔触
		 p.setAntiAlias(true); // 反锯齿
		 p.setColor(Color.RED);
		 p.setStyle(Style.STROKE);
		 final Canvas canvas = sfh.lockCanvas();
		 canvas.drawColor(Color.WHITE);//背景
		 RectF rectF = new RectF(0, 0, w, h);   //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高

		 canvas.drawBitmap(ditu, null, rectF, null);

         for (int i=0; i<count; i++) {
        	 Map<String, Object> listdd = listxx.get(i);
        	 String bianhao = listdd.get("code").toString();
        	 String sum     = listdd.get("summary").toString();
        	 int xx = Integer.parseInt(listdd.get("x").toString());
        	 int yy = Integer.parseInt(listdd.get("y").toString());
        	 biaojiclass bjtemp = new biaojiclass();
        	 bjtemp.setCode(bianhao);
        	 bjtemp.setSummary(sum);
        	 bjtemp.setX(xx);
        	 bjtemp.setY(yy);
        	 bjArray.add(bjtemp);
        	 canvas.drawBitmap(biaoji, xx, yy, null);
        	 
         }
         
         sf.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				 int eventaction = event.getAction();    
			        switch (eventaction) {    
			        case MotionEvent.ACTION_DOWN:   
			            int x = (int) event.getX();    
			            int y = (int) event.getY(); 
			            for (int t=0; t<bjArray.size(); t++)  
			            {   if ((x>=12)&&(y>=12)) {
			            	 if ( (x<=(bjArray.get(t).getX()+12 )&& (y<=(bjArray.get(t).getY()+12)) && (x>=(bjArray.get(t).getX()-12)) && (y<=(bjArray.get(t).getY()-12)) )) {
			            		Log.e("zhudw3", "on click ,you can see biaoji clicked!");
			            		canvas.drawText(bjArray.get(t).getCode()+"|"+bjArray.get(t).getSummary(), (float)x, (float)y, null);
			            		
			            		
			            	 }
			                }
			            }
			            break;    
			        case MotionEvent.ACTION_MOVE:    

			            break;    
			    
			        case MotionEvent.ACTION_UP:    
			    
			            break;    
			        }    
			        return false;   } 
        	 
         });
		 
	 //canvas.drawRect(10, 10, 100, 100, p);
	//	canvas.drawBitmap(ditu, 0, 10 , p);
		 sfh.unlockCanvasAndPost(canvas); //提交绘制内容



		
	}





}
