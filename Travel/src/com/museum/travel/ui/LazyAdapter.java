package com.museum.travel.ui;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;
import java.util.Map;

import com.museum.travel.ImageLoader;
import com.museum.travel.R;
import com.museum.travel.TListActivity;

import android.app.Activity;  
import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageView;  
import android.widget.TextView;  
  
public class LazyAdapter extends BaseAdapter {  
      
    private Activity activity;  
    private List<Map<String, Object>> listdata;  
    private static LayoutInflater inflater=null;  
    public ImageLoader imageLoader; //用来下载图片的类，后面有介绍  
	private Context mcontext;
    public LazyAdapter(Context mcontext ) {
    	this.mcontext = mcontext;

    	inflater = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
    }
    public LazyAdapter(Activity a, ArrayList<Map<String, Object>> d) {  
        activity = a;  
        listdata=d;  
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        imageLoader=new ImageLoader(activity.getApplicationContext());  
    }  
  
    public int getCount() {  
        return listdata.size();  
    }  
  
    public Object getItem(int position) {  
        return position;  
    }  
  
    public long getItemId(int position) {  
        return position;  
    }  
      
    public View getView(int position, View convertView, ViewGroup parent) {  
        View vi=convertView;  
        if(convertView==null)  
            vi = inflater.inflate(R.layout.onlinelist, null);  
  
        TextView title = (TextView)vi.findViewById(R.id.ItemTitle); // 标题  
        TextView artist = (TextView)vi.findViewById(R.id.ItemText); // 内容 
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // 缩略图  
          
        HashMap<String, Object> listdata1 = new HashMap<String, Object>();  
        listdata1 = (HashMap<String, Object>) listdata.get(position);  
         
        // 设置ListView的相关值  
        title.setText("编号: "+listdata1.get(TListActivity.KEY_TITLE).toString());  
        artist.setText(listdata1.get(TListActivity.KEY_TEXT).toString());   
        //imageLoader.DisplayImage(listdata1.get(TListActivity.KEY_THUMB_URL).toString(), thumb_image);  
        return vi;  
  }

	public void setData(List<Map<String, Object>> result) {
		// TODO Auto-generated method stub
		this.listdata = result;
	}  
	
	
}

