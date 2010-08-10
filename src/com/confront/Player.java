package com.confront;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.confront.R.drawable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Player extends Activity implements SeekBar.OnSeekBarChangeListener{
	//這個類別裡，變數名稱前面有m開頭的，代表million
	
	
	private MediaPlayer mp; //mp是MediaPlayer實體名稱
	ImageButton ImagePlayButton,ImageStopButton,  //分別為圖形播放鈕、圖形停止鈕
	ImageListButtuon,ImageBeforeButton,ImageNextButton;//圖形清單鈕、圖形往前鈕、圖形往後鈕
	TextView tv1,tv2,tv3;//tv1是文字"歌曲名稱"的TextView物件、tv2是已播放時間的TextView物件、tv3是單曲總時間的TextView物件
	int position;  //position是從MusicList.class來的播放清單位置
	Bundle bundle;  //Bundle是從MusicList.class來的打包物件
	SeekBar seekbar; //seekbar是時間軸
	int mdurationTime,mduTimeInOneProgess, progress,mminOfTotalTime,msecOfTotalTime;  
	//durationTime歌曲總時間、duTimeInOneProgess是時間軸1格所代表的歌曲長度、progress是時間軸的位置、
	//minOfTotalTime是總時間的String分值、secOfTotalTime是總時間的String秒值
	Handler handler;  //handler是管家婆物件名稱
	int heapTime=0;  //heapTime是已播放時間計時器"Thread2"每一秒叫一次郵差，
	//而讓主畫面的管家婆接收郵差來的訊息，不斷+1秒的關鍵物件實體，單位是1秒加總1
	private ListView lrc_listview;
	private String str;//將readLine()存成str
	HashMap<Integer, String> map /*= new HashMap<Integer, String>()*/; //存單純歌詞的map集合
	private String[] onlyLyrics;	//從lrc抓出來的排序後純歌詞
	Object[] key;//將map改存為set陣列集合	
	int mapi;//歌詞的haspmap容量，也是迴圈的變數
	boolean thread3Switch;//控制thread3的開關
	int skipTime;//動態歌詞會從lrc檔選擇休息時間
	Thread t32;//t32是動態歌詞的控制thread
	int currentTime;//currentTime是從getCurrentTime()抓出來的現在時間值
	private ArrayList<String> songname,songpath;//從MusicList綁過來的2個ArrayList
	//songname放了要放的歌的所有檔名，songpath放了要播的歌的所有路徑+檔名
	private String setpath;//setpath放的是從MusicList傳過來的被設定的路徑
	TextView greenbar;
	String pathlrc;//歌詞lrc的完整路徑名
	ImageView coverimage;//顯示cover圖檔
	
	//程式啟動時會啟動的method：onCread
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//將xml設置到playist.xml
		setContentView(R.layout.player);
		//讓xml物件和主UI的實體串聯
		tv1 =(TextView) findViewById(R.id.trackname);
		tv2=(TextView) findViewById(R.id.currenttime);
		tv3 =(TextView) findViewById(R.id.timeview);		
		seekbar = (SeekBar) findViewById(R.id.seebar);
		ImagePlayButton = (ImageButton)findViewById(R.id.play);
		lrc_listview = (ListView) findViewById(R.id.listview);
		greenbar = (TextView) findViewById(R.id.greenbar);
		coverimage = (ImageView) findViewById(R.id.coverimage);
		//指派一個時間軸的監聽器，讓我們隨時可以監聽時間軸的改變
		seekbar.setOnSeekBarChangeListener(this);
		tv1.setBackgroundColor(R.drawable.green);
		setTitle("【就愛聽音樂】播放器");
	
		
		
		//onCreate中宣告的Handler管家婆，用來接收Thread2郵差傳來的message訊息
		handler = new Handler(){
			//讓管家婆有能力收信的method
			 public void handleMessage(Message msg) { 
				 
				 //msg.what代表"郵差送來的東西"
				 switch(msg.what){
				 //檢查msg.what若為2，是我們家的信，就執行heapTime+1，並將值設成已播放時間
				
				 case 1:
					 greenbar.setVisibility(4);//將綠色光棒隱藏
					 break;
					 
				 case 2:
					 if(mp.isPlaying()==true){
						 heapTime++;
						 //setText這件事，永遠只有自己的UI能改，郵差(副執行緒)不能改
					 tv2.setText(convertInt2Time(heapTime));
					
					 }break;
				 
				 case 3:
					 //將Message裡的String[]純歌詞陣列和ListView做關聯
							ListAdapter listadapter = new ArrayAdapter(Player.this, android.R.layout.simple_list_item_1,(String[])msg.obj);
							lrc_listview.setAdapter(listadapter);
							//將可能已打開的cover關閉，才能顯示出歌詞
							
							break;
							
				 case 4:
					//依照Thread3的動態歌詞時間點去位移ListView，達到動態歌詞效果
					 lrc_listview.setSelectionFromTop(mapi, 130);
					 if(mp.isPlaying()==true){
					 greenbar.setVisibility(0);}//將綠色光棒打開
					 break;
					 
				 case 5:
					 coverimage.setAlpha(80);//將cover設成alpha=100
					 break;
					 
				 case 6:
					 coverimage.setAlpha(255);//將cover設成alpha=255
					 break; 
					 
						}				 				          
				 super.handleMessage(msg);				 
			 } 
			 
		};


		
	
		
		
		
		
		//建立MediaPlayer類別
		mp = new MediaPlayer();
		String clickedpath="";
		String clickedname="";
		//從intent中取值，並在這裡抓出來
		bundle = Player.this.getIntent().getExtras();
		
		if(bundle !=null){
			//如果Player被開啟的條件是從MusicList開出來的，那麼就會接收到bundle
			//bundle有從MusicList傳來的setpath(設定的音樂路徑)
			songname = bundle.getStringArrayList("allsongname");
			songpath = bundle.getStringArrayList("allsongpath");
			clickedpath = bundle.getString("clickedpath");
			clickedname = bundle.getString("clickedname");
			position = bundle.getInt("position");
			setpath = bundle.getString("setpath");
			tv1.setText(clickedname);
		
		try {
			mp.setDataSource(clickedpath);
			//播歌前一定要先"準備"好，好像要先放進CD
			
			mp.prepare();			
			tv3.setText(getTotalTime());
			mp.start();
			//一旦播歌了，圖形播歌按鈕馬上變成暫停按鈕
			ImagePlayButton.setImageResource(drawable.pause);
			//計算一格時間軸所代表的時間長度
			mduTimeInOneProgess =mdurationTime/100;
		
		} catch (IllegalStateException e) {
			Toast.makeText(Player.this,e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(Player.this,e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		//準備使用Thread1去做出會動的時間軸
		countBar();
		//呼叫Thread2去計數已播歌時間
		countcurrentTime();
		//呼叫Thread3去叫出動態歌詞
		getLyrics();
			
			}else{	
				tv1.setText("請先選歌...");
			}		 
			
		
		 
		 //音樂播完後的監聽器
		 mp.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				thread3Switch=false;
				mp.stop();
			}
			 
		 });
		 
		 
		
		
		//圖形播放音樂和暫停按鈕的method實作
		ImagePlayButton.setOnClickListener(new OnClickListener(){
    	public void onClick(View v){
    		//初始值讓圖形呈現為圖形播放鈕
    		ImagePlayButton.setImageResource(drawable.play);
    		if(mp.isPlaying()==true){
    			mp.pause();
    			//當暫停鈕啟動後，圖形按鈕馬上變成圖形播放鈕
    			ImagePlayButton.setImageResource(drawable.play);	
			}else{		
				mp.start();
				
				ImagePlayButton.setImageResource(drawable.pause);
	
			}
    	}
    });
		

		
		//下達停止播放指令
		ImageStopButton = (ImageButton)findViewById(R.id.stop);
		ImageStopButton.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){	    		
	    		try {	
	    			mp.stop();
	    			//一旦音樂stop都要用reset()去初始化MediaPlayer實體
	    			mp.reset();
	    			tv1.setText("請至播放清單選歌");
	    			tv2.setText("0:00");
	    			//將時間計數歸0
	    			heapTime=0;
	    			//因已下達停止播放指令，故要將圖形暫停鈕改回圖形播放鈕
	    			ImagePlayButton.setImageResource(drawable.play);
	    		} catch (Exception e) {
	    			Toast.makeText(Player.this,e.toString(), Toast.LENGTH_SHORT).show();
	    			e.printStackTrace();
	    		}	
	    	}
	    });

		
		//返回播放清單
		ImageListButtuon = (ImageButton)findViewById(R.id.list);
		ImageListButtuon.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		//播歌前得先停止播歌，否則程式會出錯
	    		mp.stop();
	    		//趁機將MediaPlayer實體釋放掉
	    		mp.release();
	    		//使用"意圖"將頁面帶回MusicList.class播放清單畫面
	    		Intent intent = new Intent();
	    		
	    		//做一個有無bundle的判斷，否則在Center進去Player，再點播放清單會出錯
	    		if(bundle!=null){
	    		Bundle bundle = new Bundle();
	    		bundle.putString("setpath", setpath);
	    		intent.putExtras(bundle);}
	    		
	    		intent.setClass(Player.this, MusicList.class);
	    		startActivity(intent);
	    		Player.this.finish();		    		
	    	}
	    });
		
		
		//返回前首指令
		ImageBeforeButton = (ImageButton)findViewById(R.id.before);
		ImageBeforeButton.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		//將時間軸位置變數設回0
	    		progress=0;
	    		//再將時間軸拉回0位置
	    		seekbar.setProgress(progress);
	    		//將歌曲清單計算變數-1，好讓歌曲能往前播放
	    		position-=1;
	    		//先把前一首在播的歌停止吧
	    		mp.stop();
	    		//將Thread2的計數器重新歸0
	    		heapTime=0;	    		
	    		//把歌詞lrc_listview打開
	    		lrc_listview.setVisibility(1);
	    		
	   
	    		
	    		//當一直按往前，按到第1首歌的前1首時的判斷
	    		if(position<=-1){	
	    			tv1.setText("已達最前首");
	    			tv2.setText("0:00");
	    			//將歌詞隱藏
	    			lrc_listview.setVisibility(4);
	    			
	    	 		Message m = handler.obtainMessage(1);
		    		handler.sendMessage(m);

	    			}else{
	    		try {
	    			//重設
	    			mp.reset();
	    			//設定MediaPlayer資源
	    			
					mp.setDataSource(songpath.get(position));
					//從陣列裡將設定位置的歌曲名稱設到tv1裡
					tv1.setText(songname.get(position));
					//好的，放入CD
					mp.prepare();
					//先把這歌的總時間算出來吧，反正CD都放進去了，也可以先算一算了
					tv3.setText(getTotalTime());
					//再來開始播歌
		    		mp.start();
		    	
		    		thread3Switch=false;
		    		getLyrics();
		    		mapi=1;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		}
	    	}
	    });
		
		
	
		//往後首指令
		ImageNextButton = (ImageButton)findViewById(R.id.next);
		ImageNextButton.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v){
	    		//將時間軸位置變數設回0
	    		progress=0;
	    		//再將時間軸拉回0位置
	    		seekbar.setProgress(progress);
	    		//將歌曲清單計算變數+1，好讓歌曲能往後播放
	    		position+=1;		
	    		//先把前一首在播的歌停止吧
	    		mp.stop();
	    		//將Thread2的計數器重新歸0
	    		heapTime=0;
	    		//把歌詞lrc_listview打開
	    		lrc_listview.setVisibility(1);
//	    		greenbar.setVisibility(0);
	    		//當一直按往後，按到第3首歌的後1首時的判斷
	    		if(position>=songpath.size()){	
	    				tv1.setText("已是最後首");
	    				tv2.setText("0:00");
	    				//將歌詞隱藏
		    			lrc_listview.setVisibility(4);

		    			Message m = handler.obtainMessage(1);
			    		handler.sendMessage(m);
			    		
	    			}else{
	    		try {
	    			//重設
	    			mp.reset();
	    			mp.setDataSource(songpath.get(position));			
	    			tv1.setText(songname.get(position));
					mp.prepare();
					tv3.setText(getTotalTime());
		    		mp.start();
		    		thread3Switch=false;
		    		getLyrics();
		    		mapi=1;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		}
	    	}
	    });		
	}
	
	
    //將歌詞的[00:00.00]轉換成int
	public int convertTime2Int(String key){
		String strmin = key.substring(key.indexOf("[")+1, key.indexOf("[")+3);
		String strsec = key.substring(key.indexOf(":")+1, key.indexOf(":")+3);
		String strms = key.substring(key.indexOf(".")+1, key.indexOf(".")+3);
		int imin = Integer.parseInt(strmin);
		int isec = Integer.parseInt(strsec);
		int ims  = Integer.parseInt(strms);
		int timeTag = (imin*60+isec)*1000+(ims*10);
		return timeTag;
	}
	

	
	

	
	
	//將已播歌計時器的int值換算成為0:00格式
	public String convertInt2Time(int heaptime){
		//millmin是int型態的微分
		 int millmin=0;
		//millsec是int型態的微秒
		 int millsec=0;
		 //設定strmin為String型態的"分"
		 String strmin="0";
		 //設定strsce為String型態的"秒"
		 String strsce="00";
		 
		 //當thread2的已播歌計數器累計時間小於60前的判斷
		 if(heaptime<60){
			 millmin =0;
			 millsec =heaptime;
		 }else{
			 millmin = heaptime/60;
			 millsec = heaptime%60; 
		 }
		 //將微分轉成String型態的"分"
		 strmin=String.valueOf(millmin);
		
		 //當微秒小於10的判斷
		 if(millsec<10){
			 strsce = "0"+String.valueOf(millsec);
		 }else{
			 strsce = String.valueOf(millsec);	 
		 }
		 return strmin+":"+strsce;
	}

		
	//計算出總歌曲長度
	public String getTotalTime(){
		//將計算時間的method"getDuration()"值存到宣告的變數durationTime裡
		mdurationTime =mp.getDuration();
		//因為durationTime還是微秒型態，需丟入convertMill2Time()方法去算出我們平常習慣的時間格式0:00
		return "/"+convertMill2Time(mdurationTime);
	}
	
	//換算微秒至0:00格式
	public String convertMill2Time(int mdurationTime){
		//一秒是1000微秒，當歌曲長度小於1分鐘時的判斷
		if(mdurationTime<=60000){
			//當歌曲長度小於1分鐘，將分設為0
			mminOfTotalTime=0;
		}else{
			//若大於1分鐘，將String算出來
			mminOfTotalTime =mdurationTime/1000/60;
		}
		//計算出String秒
		msecOfTotalTime = (mdurationTime/1000)%60;
		//如果String秒小於10，要在秒前再+"0"，好讓秒呈現出2位數00
		if (msecOfTotalTime<10){return mminOfTotalTime+":0"+msecOfTotalTime;
		}else return mminOfTotalTime+":"+msecOfTotalTime;
	}
	
	
	//搜尋條規定的3個函數，時間軸總共設成100格(在xml裡設的)
	@Override//搜尋條在跑中的動作
	public void  onProgressChanged  (SeekBar  seekBar, int progress, boolean fromUser){
		this.progress=progress;
	  }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub		
		//設定時間軸影響動態歌詞的連結關係
		t32.interrupt();
//		thread3Switch=false;

	}

	@Override//搜尋條拉放停止後的動作
	public void onStopTrackingTouch(SeekBar seekBar) {

		//當下的時間軸格所指到的時間設位
		int progressSTime=this.progress*mduTimeInOneProgess;		
		//將時間軸拖曳動作結束，將音樂設到"當下的時間軸那一格*1秒所代表時間"的位置
		mp.seekTo(progressSTime);
		//將時間軸被拖的位置換算成0:00格式，設到tv2裡
		tv2.setText(convertMill2Time(progressSTime));
		//時間軸拖動了，Thread2的計時器的heapTime時間累積也要改掉才行！
		heapTime=mminOfTotalTime*60+msecOfTotalTime;
		

		/*
		 * 使用binarySearch方法在map的keyset視圖裡找出現在被選取的時間軸millisecond的位置
		 * 但因為binarySearch是從索引0開始計算而歌詞從1才開始，搜尋結果若為正數，代表我們拉軸的停放位置剛好對到歌詞的啟始時間，
		 * key[i]是從0開始，而binarySearch也是從0開始，
		 *所以mapi=result
		 */
		int result=Arrays.binarySearch(key, progressSTime);
		if(result>0){mapi=result;
		/*
		 * 如果使用binarySerach方法在map的keyset視圖裡找出來的值為負數，代表現在所拉的時間軸位置在map的key[i]裡找不到完全符合的時間點
		 *result會為-(應該被插入之索引值)-1，key[i]是從0開始，而binarySearch也是從0開始，
		 *所以mapi=-(result)-2
		 */
		}else {mapi=-(result)-2;}
		/*
		 * 因為拉的軸隨時都可能在2個map.key[i]的中間，前面的公式都將listview.position設在result的前一個索引值
		 * 所以會遇到當下的時間軸距離下一個map.key[i]，在Thread3-2裡skipTime時間變得更短的問題
		 * 所以lastTime不能用Thread3-2裡的公式，而是要將lastTime設成我們現在拉的時間點progressSTime，
		 * 將這個點丟回Thread3-2的公式中，才有可能從中間的點繼續帶回Thread3-2的公式去計算接下來的公式跑法
		 */

		
		
//		thread3Switch=true;
//		t32.start();
		
		lrc_listview.setSelectionFromTop(mapi, 130);
		currentTime=mp.getCurrentPosition();

	}

	
	
	
	
	
	//時間軸計時器"Thread1"
	public void countBar(){		
		
		new Thread(new Runnable(){
			public void run(){
				while(true){				
				try {
					//讓Thread1睡"時間軸1格所代表的時間"長度
					Thread.sleep(mduTimeInOneProgess);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//如果Playist.class頁面針測到被結束掉了(譬如回到播放清單去了)，終結Thread1，避免程式出錯
				if(Player.this.isFinishing()==true){
					return;
					}
				else if(mp.isPlaying()==true){
					//持續將時間軸往右1格1格慢慢加(時間軸總共100格)
					progress++;
					//每往右跑1格，時間軸圖形就要往右拉一格
					seekbar.setProgress(progress);
				}									
				}
			}
		}).start();
	}	
	

	//已播放時間計時器"Thread2"
	public void countcurrentTime(){
		tv2.setText("0:00");
		
		new Thread(new Runnable(){
			public void run(){
			
				while(true){
	
					
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				//如果Playist.class畫面被結束掉了，順便終結Thread2，避免程式出錯
				if(Player.this.isFinishing()==true){
					return;
					}
				
				//建立起郵差物件訊息Message，門牌號碼是2
				Message message = handler.obtainMessage(2);
				 //請主畫面的管家婆hr收我寄出的訊息
				 Player.this.handler.sendMessage(message);	
				 
			
				}						
			}
		}).start();
		
	}
	
	
	
	//Thread3用來載入動態歌詞
	public void getLyrics(){
		String s =(String) tv1.getText();
		String justname = s.substring(0, s.lastIndexOf("."));
	    pathlrc = setpath+justname+".lrc";
		File f = new File(pathlrc);
		
		
		//載入cover封面
		Bitmap cover =BitmapFactory.decodeFile(setpath+"cover.jpg");
		coverimage.setImageBitmap(cover);
		Bitmap Cover =BitmapFactory.decodeFile(setpath+"Cover.jpg");
		coverimage.setImageBitmap(Cover);
		
		
		
		
		if(f.exists()==false){
			
			
			
			Message m = handler.obtainMessage(6);
			Message m2 = handler.obtainMessage(1);
			handler.sendMessage(m);
			handler.sendMessage(m2);
			
		}else{
			
	new Thread(new Runnable(){
		
			public void  run(){
				
		
				try{	
				
					 FileReader fr = new FileReader(pathlrc);	          
			          BufferedReader br = new BufferedReader(fr);	

			          
			      	  map = new HashMap<Integer, String>();
			     while((str=br.readLine())!= null){
			    	 	//很怪，在if判斷式時，[居然是在位置1而非0，0被一個空值占去了
			           if(str.indexOf("a")==2 || str.indexOf("t")==2 || str.indexOf("b")==2){   	
			        	   //前面4個非動態歌詞的標籤，為了讓綠色區塊成真，加入1個索引0的map集合
			        	   map.put(0,"");
			       }else{
			            switch(str.lastIndexOf("]")){
			            	case 90:
			            	case 89:map.put(convertTime2Int(str.substring(str.indexOf("[",80),str.indexOf ("]",80)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			            	case 80:
			            	case 79:map.put(convertTime2Int(str.substring(str.indexOf("[",70),str.indexOf ("]",70)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 70:
			                case 69:map.put(convertTime2Int(str.substring(str.indexOf("[",60),str.indexOf ("]",60)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 60:
			                case 59:map.put(convertTime2Int(str.substring(str.indexOf("[",50),str.indexOf ("]",50)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 50:
			                case 49:map.put(convertTime2Int(str.substring(str.indexOf("[",40),str.indexOf ("]",40)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 40:
			                case 39:map.put(convertTime2Int(str.substring(str.indexOf("[",30),str.indexOf ("]",30)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 30:
			                case 29:map.put(convertTime2Int(str.substring(str.indexOf("[",20),str.indexOf ("]",20)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 20:
			                case 19:map.put(convertTime2Int(str.substring(str.indexOf("[",10),str.indexOf ("]",10)+1)),str.substring(str.lastIndexOf("]")+1, str.length()));
			                case 10:
			                case 9: map.put(convertTime2Int(str.substring(str.indexOf("["),str.indexOf ("]")+1)),str.substring(str.lastIndexOf("]")+1, str.length())); 		                	
			            }			            
			            }
			       }
			            br.close();
			     }
				catch(Exception e){ e.printStackTrace();}
			        key = map.keySet().toArray();
			        Arrays.sort(key);
			     onlyLyrics=new String[key.length];
			        for(int i = 0; i < key.length; i++){
			        	
			     
			        onlyLyrics[i] = map.get(key[i]);        
			        
				Message m3 = handler.obtainMessage(3, onlyLyrics);
				handler.sendMessage(m3);
			    	}
				thread3Switch=true;
				animLyrics();
				t32.start();
			
			}
		}).start();
	//開啟歌詞動態捲動功能

	Message m = handler.obtainMessage(5);
	handler.sendMessage(m);
	
	
	
		}
	}
	
	
	
	public void animLyrics(){
		
		//Thread3-2用來載入動態歌詞
			
	 t32=	new Thread(new Runnable(){
			
				public void  run(){
					
					Looper.prepare();
					//以下按照歌曲時間做出動態歌詞轉動效果	
					int z=0;
					 for(mapi=0;mapi<key.length;mapi++){
						 if(thread3Switch==true){
							 
							  if(Player.this.isFinishing()==true){
									return;
									}else{
										Message m4=handler.obtainMessage(4, mapi, 1);
										handler.sendMessage(m4);
										
										/*程式若執行到map的最後一筆還繼續map+1
										 * 會出現IndexOutOfBoundException，所以要做判斷
										 */
										if(mapi+1==key.length){
										//若下一筆已經到達總key數，就結束迴圈
											return;
										}else{
											
											 

											if(z+1!=mapi){
												skipTime=-(currentTime-(Integer)key[mapi+1]);}																					
											else{
												skipTime=-((Integer)key[mapi]-(Integer)key[mapi+1]);}
										
										
										}
										
										try {
											Thread.sleep(skipTime);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}	
										
									
										
									}
							 
						 }else{
					    	   return;
						       }
						 z=mapi;
						 
						 
					 }
				       Looper.loop();
					
	}
		});
		

	}

}