package com.museum.travel;

import com.museum.travel.util.Player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


public class VoiceActivity extends Activity {
	
	private Button btnPause, btnPlayUrl, btnStop,btnReplay;  
    private SeekBar skbProgress;  
    private Player player;  
    private EditText file_name_text;  
    private TextView tipsView;  
    private static int pause = 1;
    ImageView playbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivty_voice);
		Intent intent = this.getIntent();//得到用于激活它的意图  
		String code = intent.getStringExtra("bianhao");
		String summary = intent.getStringExtra("summary");
		String mp3url  = intent.getStringExtra("audioaddr");
		playbtn = (ImageView)findViewById(R.id.play);  
		TextView ItemTitle = (TextView)findViewById(R.id.ItemTitle);
		TextView ItemText  = (TextView)findViewById(R.id.ItemText);
		ItemTitle.setText(code);
		ItemText.setText(summary);
	          
        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);    
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());    
          
        player = new Player(mp3url,skbProgress);    
          
        TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
        telephonyManager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);  
	        
		playbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (pause == 1) {
				  player.play();
				  pause = 0;
				  playbtn.setImageResource(R.drawable.pause2);
				} else {
				  player.pause();
				  pause = 1;
				  playbtn.setImageResource(R.drawable.bofang);
				}
				
			}
			
		});
	}
	
	
	
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

    

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		player.stop();
	}



	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {    
	        int progress;    
	        @Override    
	        public void onProgressChanged(SeekBar seekBar, int progress,    
	                boolean fromUser) {    
	            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()    
	            this.progress = progress * player.mediaPlayer.getDuration()    
	                    / seekBar.getMax();    
	        }    
	    
	        @Override    
	        public void onStartTrackingTouch(SeekBar seekBar) {    
	    
	        }    
	    
	        @Override    
	        public void onStopTrackingTouch(SeekBar seekBar) {    
	            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字    
	            player.mediaPlayer.seekTo(progress);    
	        }    
	    }  
	
	 class ClickEvent implements OnClickListener {    
	        @Override    
	        public void onClick(View arg0) {    
	            if (arg0 == btnPause) {    
	                boolean pause=player.pause();    
	                if (pause) {  
	                    btnPause.setText("继续");  
	                    tipsView.setText("音乐暂停播放...");    
	                }else{  
	                    btnPause.setText("暂停");  
	                    tipsView.setText("音乐继续播放...");    
	                }  
	            } else if (arg0 == btnPlayUrl) {    
	                player.play();  
	                tipsView.setText("音乐开始播放...");  
	            } else if (arg0 == btnStop) {    
	                player.stop();    
	                tipsView.setText("音乐停止播放...");    
	            } else if (arg0==btnReplay) {  
	                player.replay();  
	                tipsView.setText("音乐重新播放...");    
	            }  
	        }    
	    }    
	
    /** 
     * 只有电话来了之后才暂停音乐的播放 
     */  
    private final class MyPhoneListener extends android.telephony.PhoneStateListener{  
        @Override  
        public void onCallStateChanged(int state, String incomingNumber) {  
            switch (state) {  
            case TelephonyManager.CALL_STATE_RINGING://电话来了  
                player.callIsComing();  
                break;  
            case TelephonyManager.CALL_STATE_IDLE: //通话结束  
                player.callIsDown();  
                break;  
            }  
        }  
    }  

}
