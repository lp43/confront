package com.confront;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MusicList extends Activity implements AdapterView.OnItemClickListener{
	private ListView listmusic,listexplore;
	private Button mlbtn;
	private TextView mltv;
	private String rootPath=Environment.getExternalStorageDirectory().toString()+"/";
	private Dialog dialog;
	MyAdapter adapter;
	private List<String> items=null;//items是檔案名稱
	private List<String> paths=null;//paths是檔案路徑
	private ArrayList<String> songname,songpath;//被點選的歌曲名稱和歌曲路徑
	File file;
	String setpath;//setpath存放現在的"目前路徑"
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musiclist);
		setTitle("【就愛聽音樂】播放清單");
		
		listmusic=(ListView) findViewById(R.id.list);
		
		
		mltv=(TextView) findViewById(R.id.mltv);
		
		listexplore=(ListView) findViewById(R.layout.file_row);
		
		//為了讓Player跳回MusicList不會又把目前路徑設叵預設值，
		//而印出\mnt\sdcard\的音樂清單
		//這裡設了一個接收，把丟給Player的setpath又收回來
		Bundle bundle = this.getIntent().getExtras();
		if(bundle !=null){		 
			setpath=bundle.getString("setpath");
			
			mltv.setText(setpath);
			dir2MlList(setpath);
			}else{
				mltv.setText(rootPath);
				dir2MlList(rootPath);
			}
		
		setpath = (String) mltv.getText();
		listmusic.setOnItemClickListener(this);
		
		mlbtn=(Button) findViewById(R.id.mlbtn);
		mlbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getAllFile(rootPath);
	   };
	});	
		
	
		
	}
	
	
	//getAllFile()用來取得一個資料層的所有資料，並存成paths和items陣列
	private void getAllFile(String filePath){
		items=new ArrayList<String>();//items放檔案名稱
		paths=new ArrayList<String>();//paths放該檔案的路徑
		file = new File(filePath);
		File[] filelist = file.listFiles();
		
		if(!filePath.equals(rootPath)){//如果傳進來的目錄不是根目錄"\"
			
			//第1筆設為回根目錄
			items.add("b1");  //將圖標物件設為代號b1，MyAdapter會拿去處理，並回傳設定成"回根目錄"的字串
			paths.add(rootPath);  //就將第1格position設為回根目錄的路徑"/"
			
			//第2筆設為回上一層
			items.add("b2");
			paths.add(file.getParent());//getParent()目的是找出該目錄的上一層目錄並回傳String
		}
		
		//底下將File列表陣列filelist依序以getName()和getPath()取出並存放在2個不同的ArrayList
		for(Object o:filelist){
			
				File f=(File)o;
				if(f.isDirectory()){
				items.add(f.getName());
				paths.add(f.getPath());
			}
			
		}
		
		//將上面的items和paths集合設到自己做的MyAdapter裡
		newAlert();
	}	
	
	
	
	
	//創建彈出的檔案總管視窗
	private void newAlert(){
		new AlertDialog.Builder(MusicList.this)
		.setTitle("目前路徑："+file.getPath()+"/")
		.setView(listexplore)
		.setAdapter(new MyAdapter(MusicList.this,items,paths), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				File file = new File(paths.get(which));
				
					if(file.isDirectory()){
						//如果是資料夾就再用getAllFile()取得這一層的全部資料
						getAllFile(paths.get(which));
						
					}else{
					//底下File.separator是取得Android的分隔符號
						 
						mltv.setText(file.getParent()+File.separator+file.getName()+File.separator);
					}		
					
			}
		})
		
		.setPositiveButton("設定並匯入", new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub		
				setpath = file.getPath()+File.separator;
				mltv.setText(file.getPath()+File.separator);
				
				dir2MlList(file.getPath()+File.separator);

			}
		})
		
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub		
			}
		})
		
		.show();
		
	}
	
	//建立Menu清單
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		menu.add(0, 0, 0, "回主選單");
		menu.add(0, 1, 1, "關於");
		return super.onCreateOptionsMenu(menu);
	}

	//建立Menu清單的觸發事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case 0:
				Intent intent = new Intent();
				intent.setClass(MusicList.this, Center.class);
				startActivity(intent);
				MusicList.this.finish();
				break;
			case 1:
				new AlertDialog.Builder(MusicList.this)
				.setMessage("作者：小鰻lp43\nE-mail：lp43simon@gmail.com")
				.setTitle("關於...")
				
				.setPositiveButton("確認", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub				
					}
				})
				.show();
		}
		return super.onOptionsItemSelected(item);
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(MusicList.this, Player.class);
		Bundle bundle = new Bundle();
		bundle.putString("clickedpath", songpath.get(position));
		bundle.putString("clickedname", songname.get(position));
		bundle.putInt("position", position);
		bundle.putStringArrayList("allsongpath",songpath );
		bundle.putStringArrayList("allsongname", songname);
		bundle.putString("setpath", setpath);
		intent.putExtras(bundle);
		startActivity(intent);
		MusicList.this.finish();
	
		
	}
	
	//將指定路徑裡的mp3通通列出在播放清單列表中
	public void dir2MlList(String path){
		File indicatedir= new File(path);
		File[] fileinindicate = indicatedir.listFiles();
		songname = new ArrayList<String>();
		songpath=new ArrayList<String>();
		
		//顯示progressDialog
		ProgressDialog progressdialog=null;
		progressdialog = new ProgressDialog(MusicList.this);
		progressdialog.setMessage("歌曲載入中，請稍候...");
		progressdialog.setIndeterminate(true);
		progressdialog.setCancelable(true);
		progressdialog.show();
		
			
		
		for(int i=0;i<fileinindicate.length;i++){
			File file = fileinindicate[i];
			String filename =file.getName();
			
			if(!file.isDirectory()){
				
			String endfilename =filename.substring(filename.lastIndexOf(".")+1, filename.length());
			
			if(endfilename.equals("mp3")){						
				songname.add(filename);
				songpath.add(file.getPath());
			}
			
			}
			
		}
		
		progressdialog.cancel();
		ListAdapter adapter = new ArrayAdapter<String>(MusicList.this,
				android.R.layout.simple_list_item_1,songname);
		listmusic.setAdapter(adapter);
		
		if(songname.size()==0)Toast.makeText(MusicList.this, R.string.cantfindmusic, Toast.LENGTH_SHORT).show();

	}
	
	
	
}
