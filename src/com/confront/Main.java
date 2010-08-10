package com.confront;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.*;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Main extends Activity {
	private Button button1,button2;
	private EditText ed1,ed2;
	private TextView tv1;
 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LinearLayout a = new LinearLayout(this);
        a.setBackgroundColor(80);
        
        button1 = (Button) findViewById(R.id.submit);
        button2 = (Button) findViewById(R.id.exit);
        ed1 = (EditText)findViewById(R.id.field_account);
        ed2 = (EditText)findViewById(R.id.field_password);
        button1.setOnClickListener(into);
        button2.setOnClickListener(out);
        tv1=(TextView)findViewById(R.id.result);
    }
    
    //設定送出鈕的動作
    private  Button.OnClickListener into = new OnClickListener(){
    	public void onClick(View v){
    		//建立HttpPost連線
//    		HttpPost hp = new HttpPost("http://192.168.1.4:8080/login.txt");
//    		 hp.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("192.168.1.4",8888));
//    		老師我要怎麼知道getParams()後面可以接setParameter
//    		//宣告一個list集合，將帳號密碼打包放入該陣列
    		//當我要增一個功能時，都要大動干割
//    		List<NameValuePair> lt = new ArrayList<NameValuePair>();
//    		lt.add(new BasicNameValuePair("account",ed1.getText().toString()));
//    		lt.add(new BasicNameValuePair("password",ed2.getText().toString()));   		
//
//    		//將list集合塞進UnsupportedEncodingException，然後發出封包給伺服器
//    		try{
//    		hp.setEntity(new UrlEncodedFormEntity(lt));
//    	
//    		}catch (UnsupportedEncodingException e){e.printStackTrace();}
//    		
//    		
//    		//使用HttpResponse準備接收伺服器資訊
//    		try {
//				HttpResponse hr = new DefaultHttpClient().execute(hp);
//
//		    //若伺服器回應為ok(代號為200)才開始處理資訊
//				if(hr.getStatusLine().getStatusCode()==HttpStatus.SC_OK ){
//					String sr = EntityUtils.toString(hr.getEntity());
//					Toast.makeText(Confront.this, sr, Toast.LENGTH_SHORT).show();
//								}
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				
//				e.printStackTrace();
//				
//			}  catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

    		//使用意圖
    		Intent intent = new Intent();
    		intent.setClass(Main.this, Center.class);
    		startActivity(intent);
    		Main.this.finish();
    	}
    };
    
    //設定離開鈕的動作
    private  Button.OnClickListener out = new OnClickListener(){
    	public void onClick(View v){
    		finish();
    	}
    };
}