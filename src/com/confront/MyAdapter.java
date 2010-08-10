package com.confront;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	/*
	 * Icon1:回根目錄的圖標
	 * Icon2:回上一層的圖標
	 * Icon3:資料夾的圖標
	 * Icon4:檔案的圖標
	 */
	
private LayoutInflater Inflater;
private Bitmap Icon1;//Icon1是回根目錄圖標
private Bitmap Icon2;//Icon2是回上一層圖標
private Bitmap Icon3;//Icon3是資料夾圖標
private Bitmap Icon4;//Icon4是檔案圖標
private List<String> items;//items是檔案名稱
private List<String> paths;//paths是路徑名稱

public MyAdapter(Context context, List<String> items, List<String> paths){
	//此建構子裡做了該類別所用到的所有變數的初始值宣告
	Inflater = LayoutInflater.from(context);//這行若沒有宣告會出現NullPointException
	this.items=items;
	this.paths=paths;
	this.Icon1=BitmapFactory.decodeResource(context.getResources(),R.drawable.back01 );
	this.Icon2=BitmapFactory.decodeResource(context.getResources(), R.drawable.back02);
	this.Icon3=BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
	this.Icon4=BitmapFactory.decodeResource(context.getResources(), R.drawable.doc);
}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}
	

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView ==null){
			convertView = Inflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		File f = new File(paths.get(position).toString());
		//比對listview裡的點選位置的值若為"b1"，就做以下設定
		if(items.get(position).toString().equals("b1")){
			holder.text.setText("回根目錄");
			holder.icon.setImageBitmap(Icon1);
		//比對listview裡的點選位置的值若為"b2"，做以下設定	
		}else if(items.get(position).toString().equals("b2")){
			holder.text.setText("回上一層..");
			holder.icon.setImageBitmap(Icon2);
		}else{
			holder.text.setText(f.getName());
			if(f.isDirectory()){
				holder.icon.setImageBitmap(Icon3);
			}else{
				holder.icon.setImageBitmap(Icon4);
			}
		}
		return convertView;
	}

}

//定義tag類別
class ViewHolder{
	TextView text;
	ImageView icon;
}