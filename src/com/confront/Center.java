package com.confront;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Center extends Activity {
	ImageButton ImageButton1,ImageButton2,ImageButton3,ImageButton4;
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center);
        setTitle("【就愛聽音樂】媒體中心");
        
        ImageButton1 = (ImageButton) findViewById(R.id.listen);
        ImageButton1.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	Intent intent = new Intent();
        	intent.setClass(Center.this, Player.class);
        	startActivity(intent);
        	Center.this.finish();
        	}
        });
        
        ImageButton2 = (ImageButton) findViewById(R.id.centerlist);
        ImageButton2.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	Intent intent = new Intent();
        	intent.setClass(Center.this, MusicList.class);
        	startActivity(intent);
        	Center.this.finish();
        	}
        });
        
        
        ImageButton3 = (ImageButton) findViewById(R.id.choicedata);
        ImageButton3.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	Intent intent = new Intent();
        	intent.setClass(Center.this, Setting.class);
        	startActivity(intent);
        	Center.this.finish();
        	}
        });
        
        ImageButton4 = (ImageButton) findViewById(R.id.door);
        ImageButton4.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        	Center.this.finish();
        	}
        });
    }
}
