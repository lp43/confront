package com.confront;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Setting extends Activity {
	
	private EditText ed1;
	private Button button1;
	private Dialog dialog;
	ListView lv1;
	MyAdapter adapter;
	private List<String> items=null;//items是檔案名稱
	private List<String> paths=null;//paths是檔案路徑
	private String rootPath=Environment.getExternalStorageDirectory().toString()+"/";
	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		setTitle("【就愛聽音樂】設定");
		ed1=(EditText) findViewById(R.id.EditText01);
		lv1=(ListView) findViewById(R.layout.file_row);
		ed1.setText(rootPath);
		
		//當choice按鈕按下去後的動作
		button1=(Button)findViewById(R.id.Button01);
		button1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getAllFile(rootPath);
	   };
	});
	}
	

	//創建彈出的檔案總管視窗
	private void newAlert(){
		new AlertDialog.Builder(Setting.this)
		.setTitle("目前位置："+file.getPath()+"/")
		.setView(lv1)
		.setAdapter(new MyAdapter(Setting.this,items,paths), new 

DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				File file = new File(paths.get(which));
				
					if(file.isDirectory()){
						//如果是資料夾就再用getAllFile()取得這一層的全部資料
						getAllFile(paths.get(which));
						
					}else{
					//底下File.separator是取得Android的分隔符號
						ed1.setText(file.getParent

()+File.separator+file.getName());
					}		
					
			}
		})
		
		.setPositiveButton("設為預設值", new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub		
				ed1.setText(rootPath);
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
			items.add(f.getName());
			paths.add(f.getPath());
		}
		
		//將上面的items和paths集合設到自己做的MyAdapter裡
		newAlert();
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
				intent.setClass(Setting.this, Center.class);
				startActivity(intent);
				Setting.this.finish();
				break;
			case 1:
				new AlertDialog.Builder(Setting.this)
				.setMessage("作者：小鰻lp43\nE-mail：lp43simon@gmail.com")
				.setTitle("關於...")
				
				.setPositiveButton("確認", new 

DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int 

which) {
						// TODO Auto-generated method stub		

		
					}
				})
				.show();
		}
		return super.onOptionsItemSelected(item);
	}

	
}
