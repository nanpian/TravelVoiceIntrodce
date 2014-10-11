package com.museum.travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.museum.travel.scanner.CaptureActivity;
import com.museum.travel.ui.LazyAdapter;
import com.museum.travel.util.HttpUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class TListActivity extends Activity {
	
	// 所有的静态变量  
	private Button listbtn;
	private Button mapbtn;
	private ImageView gobtn;
	private ImageView picbtn;
	private ImageView gogobtn;
	List<Map<String,Object>> listt ;	
  
    // XML 节点  
    static final String KEY_SONG = "song"; // parent node  
    static final String KEY_ID = "id";  
    public static final String KEY_TITLE = "code";  
    public final static String KEY_TEXT = "title";  
    public final static String KEY_AUDIOADDR = "audio_address";  
    public final static String KEY_THUMB_URL = "thumb_url"; 
    ListView list;  
    LazyAdapter adapter;
	private ProgressDialog progressDialog;  
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tlist);
		listbtn = (Button) findViewById(R.id.listbtn);
		listbtn.setTextColor(getResources().getColor(R.color.blue));
		mapbtn = (Button) findViewById(R.id.mapbtn);
		gobtn  =  (ImageView)findViewById(R.id.bofang);
		picbtn =  (ImageView)findViewById(R.id.pic);
		gogobtn = (ImageView)findViewById(R.id.gogo);
		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>(); 
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("正在下载....");
		ArrayList<Map<String, Object>> listd = new ArrayList<Map<String, Object>>();
		adapter = new LazyAdapter(this);
		listview = (ListView)findViewById(R.id.listView1);
		picbtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent map = new Intent();
				map.setClass(TListActivity.this, CaptureActivity.class);
				startActivity(map);
			}
			
		});

		new MyTask().execute(HttpUtil.BASE_URL);
	}
	
	
	public class MyTask extends AsyncTask<String, Void, List<Map<String,Object>>>{
		@Override		
		protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();	
		progressDialog.show();
		}

		@Override	
		protected void onPostExecute(List<Map<String, Object>> result) {		
		// TODO Auto-generated method stub		
		super.onPostExecute(result);
		if(listt==null)
		{
		   progressDialog.dismiss();
		} else {
			adapter.setData(result);		
			listview.setAdapter(adapter);		
			adapter.notifyDataSetChanged();		
			progressDialog.dismiss();	
			listview.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
				      Log.v("zhudw3", "你点击了ListView条目" + arg2);//在LogCat中输出信息 
				      int count = arg2+1;
				      HashMap<String, Object> listdata1 = new HashMap<String, Object>();  
				      listdata1 = (HashMap<String, Object>) listt.get(arg2);  
				         
				        // 设置ListView的相关值  
				      String bianhao = listdata1.get(TListActivity.KEY_TITLE).toString(); 
				      String summary = listdata1.get(TListActivity.KEY_TEXT).toString();
                      String mp3url  = listdata1.get(TListActivity.KEY_AUDIOADDR).toString();
				      Intent intent = new Intent(TListActivity.this, VoiceActivity.class); 
				      Bundle bundle = new Bundle();  
				      bundle.putString("bianhao", bianhao);   
				      bundle.putString("summary", summary); 
				      bundle.putString("audioaddr", mp3url);
				      intent.putExtras(bundle);  //把打包好的bundle放入intent
				      startActivityForResult(intent, 10);  // 携带bundle跳转到secondActivty
				}
				
			});
		}
		}

		@Override	
		protected List<Map<String, Object>> doInBackground(String... params) {		
		// TODO Auto-generated method stub		
		String str = HttpUtil.getRequest(params[0], getApplicationContext());	
		if ((str==null)||(str.equals("xxxx"))){
		   return null;
		} else {
		   listt = HttpUtil.getRequest2List(str, "result");	
		}
	//	list2 = HttpUtil.getRequest2List(str, "summary");	
		return listt;		
		}

	}

}
