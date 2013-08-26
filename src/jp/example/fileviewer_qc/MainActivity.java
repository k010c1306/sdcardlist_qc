package jp.example.fileviewer_qc;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.fileviewer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {


	private TextView mCurDirTextView;
	private File mFile;
	private String mWarning = "警告";
	private String mGoUp = ".. (上へ)";
	private String CurPath;
	private File mSDdir;
	private ListView mListView;
	private ListView select_file;
	private RowAdapter adapter;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private Button UpButton;

	private ImageButton CloudButton;
	private ImageButton SortButton;
	private ImageButton SearchButton;
	private ImageButton SelectButton;
	private ImageButton Select_returnButton;
	private ImageButton CopyButton;
	private ImageButton CutButton;
	private ImageButton PasteButton;

	private ArrayList<String> path = new ArrayList<String>();
	private SparseBooleanArray checked;

	private List<CustomData> myData;

	private HorizontalScrollView HView01;
	private HorizontalScrollView HView02;

	private AlertDialog.Builder builder; //SD確認用

	private boolean Cutflag; //切り取りの時に1、コピーの時は0
	private boolean sdcheck;//SDのRead,Writeチェック

	private FileControl fcon = new FileControl();
	private SDControl scon	= new SDControl();
	private Sort sort = new Sort();

	private AlertDialog.Builder alertDialogBuilder;
	final static String[] service = { "GoogleDrive", "SkyDrive", "クラウド3" };

	private static MainActivity instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
            // ここで2秒間スリープし、スプラッシュを表示させたままにする。
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        // 通常時のテーマをセットする。
        //setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		builder = new AlertDialog.Builder(MainActivity.this);
		findViews();
		setListeners();

		//SDのFileインスタンスを取得
		mSDdir = scon.getSD();
		scon.SDcheck();
		if(sdcheck=false){
			setBilder();
		}
		updateList(mSDdir);
		instance = this;
	}


	private void setListeners() {
		UpButton.setOnClickListener(this);
		SortButton.setOnClickListener(this);
		SearchButton.setOnClickListener(this);
		CloudButton.setOnClickListener(this);
		SelectButton.setOnClickListener(this);
		Select_returnButton.setOnClickListener(this);
		CopyButton.setOnClickListener(this);
		CutButton.setOnClickListener(this);
		PasteButton.setOnClickListener(this);
		builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
			}
		});
		mListView.setOnItemClickListener(this);
	}

	private void findViews() {
		mCurDirTextView = (TextView) findViewById(R.id.current_dir);
		mListView = (ListView) findViewById(R.id.select_file);
		UpButton = (Button) findViewById(R.id.Up);
		CloudButton = (ImageButton) findViewById(R.id.Cloud);
		SortButton = (ImageButton) findViewById(R.id.Sort);
		SearchButton = (ImageButton) findViewById(R.id.Search);
		SelectButton = (ImageButton) findViewById(R.id.Select);
		select_file = (ListView) findViewById(R.id.select_file);
		alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		Select_returnButton = (ImageButton)findViewById(R.id.Select_return);
		CopyButton =(ImageButton)findViewById(R.id.Copy);
		CutButton =(ImageButton)findViewById(R.id.Cut);
		PasteButton =(ImageButton)findViewById(R.id.Paste);
		HView01 =(HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
		HView02 =(HorizontalScrollView)findViewById(R.id.horizontalScrollView2);
	}

	final private FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {

			/**
			 * TODO 今後必要不可欠 if (pathname.isDirectory() == true) { return true; }
			 * else if(pathname.getName().endsWith(".xml") == true){ return
			 * true; }
			 */

			// TODO 修正予定(仮)
			if (1 == 1) {
				return true;
			}
			return false;
		}
	};

	private void updateList(File dir) {
		// EditText に現在選択されたファイル名/フォルダ名のPathをセット
		mCurDirTextView.setText(dir.getAbsolutePath());
		CurPath = dir.getAbsolutePath();
		//データを渡してrow.xmlにセットする。
		RowAdapter adapter = new RowAdapter(this, getData(dir.listFiles(filter), dir.getParentFile()));
		//listViewに
		mListView.setAdapter(adapter);
	}

	private List getData(File[] files, File parent) {
		CustomData data = new CustomData();
		myData = new ArrayList<CustomData>();
		if (parent != null
				&& mSDdir.getParent().equals(parent.getAbsolutePath()) == false) {

			//data.setfileName(mGoUp);
			//data.setfilePass(parent.toString());
			//myData.add(data);
		}

		if (files != null) {
			for (File file : files) {
				data = new CustomData();
				data.setlastmodified(dateFormat.format(file.lastModified()));
				data.setfilePass(file.getPath());
				data.setfullfilePass(parent+file.getPath());

				if (file.isDirectory()) {
					data.setfileName(file.getName() + "/");

				} else if(file.isFile()){
					data.setfileName(file.getName());
					data.setfileSize(fcon.getfilesize(file));
				}
				myData.add(data);
			}
		}

		Collections.sort(myData, sort.sDisplayNameComparator);

		return myData;
	}



	//arg0：クリックされたAdapterのビューとなる	arg1：実際にクリックされたビュー	arg2アダプターの何番目がクリックされたか	arg3：クリックされた列のid
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CustomData data = (CustomData) arg0.getItemAtPosition(arg2);
		//Fileを作成
		mFile = new File(data.getfilePass());
		//tmpに作成したFileのPassを入れる
		String tmp = mFile.getAbsolutePath();

		if(select_file.getChoiceMode() == 2){
			path.add(tmp);

			//for(int i=1; i < path.size(); i++){
			//Log.d("example", "そのキー" + path);
			//}
		}
		else{
			//Directoryかファイルかを判断する
			boolean b = mFile.isAbsolute();
			if (mFile.isDirectory() && select_file.getChoiceMode() == 0) {
				// mButton.setEnabled(false);
				updateList(mFile);
			} else {
				// ファイルだったら、ボタンを押せるようにする
				PackageManager pm = getPackageManager();
				Intent intent = fcon.FileOpen(mFile,pm);
				if( intent != null ){
					//Intent intent = new Intent(con.FileOpen(mFile,pm));
					startActivity(intent);
				}
				else
				{

				}

				// mButton.setEnabled(true);
			}
		}

	}

	/**
	 * 戻るボタンが押された時の処理.
	 *
	 */
	//適当に追記した	nakashima
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// viewFlipper.showPrevious();
			// TODO 上の階層に戻るようにする？
			Toast.makeText(this, "戻れません", Toast.LENGTH_SHORT).show();

			String path = mCurDirTextView.getText().toString();

			if (!path.equals("/")) {
				updateList(new File(mCurDirTextView.getText().toString()).getParentFile());
			} else if (path.equals("/")) {

			}

			return true;
		}
		return false;
	}

	public class RowAdapter extends ArrayAdapter<CustomData> {
		private LayoutInflater layoutInflater_;

		//			public RowAdapter(Context context, int textViewResourceId,List<CustomData> objects) {
		//					super(context, textViewResourceId, objects);
		public RowAdapter(Context context, List<CustomData> objects) {
			super(context, 0, objects);
			layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 特定の行(position)のデータを得る
			CustomData item = (CustomData) getItem(position);
			// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
			if (null == convertView) {
				convertView = layoutInflater_.inflate(R.layout.row, null);
			}

			// CustomDataのデータをViewの各Widgetにセットする

			TextView textView1;
			textView1 = (TextView) convertView.findViewById(R.id.textView1);
			textView1.setText(item.getfileName());

			TextView textView2;
			textView2 = (TextView) convertView.findViewById(R.id.textView2);
			textView2.setText(item.lastmodified);

			TextView textView3;
			textView3 = (TextView) convertView.findViewById(R.id.textView3);
			if (item.getfileSize() != 0) {
				textView3.setText(String.valueOf(item.getfileSize() + " Byte"));
			} else {
				textView3.setText("");
			}

			//	Log.d("size", String.valueOf(item.getfileSize()) + "Byte");
			return convertView;

		}

	}

	public void onClick(View v) {

		if (v.getId() == R.id.Up) {
			//上のディレクトリに戻る処理
			String path = mCurDirTextView.getText().toString();

			if (!path.equals("/")) {
				updateList(new File(mCurDirTextView.getText().toString()).getParentFile());
			} else if (path.equals("/")) {

			}

		}
		else if (v.getId() == R.id.Search) {
			//検索処理
		}
		else if (v.getId() == R.id.Sort) {
			//並び替え処理
		}
		else if (v.getId() == R.id.Cloud) {
			//利用するクラウドを選択させる
			// ダイアログを表示するだけ
			//alertDialogBuilder.create().show();
			MyDialogFragment dialog = new MyDialogFragment();
			dialog.show(getFragmentManager(), "dialog");
		}else if(v.getId() == R.id.Select){
			HView01.setVisibility(View.GONE);
			HView02.setVisibility(View.VISIBLE);
			select_file.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}
		else if(v.getId() == R.id.Select_return){
			HView02.setVisibility(View.GONE);
			HView01.setVisibility(View.VISIBLE);
			select_file.setChoiceMode(ListView.CHOICE_MODE_NONE);
			for (int i = 0; i <path.size(); i++) {
				Log.d("example", "選択されている項目:" + path.get(i).toString());
			}
			path.clear();

		}
		else if(v.getId() == R.id.Copy){
			Cutflag = false;
			/*ListViewクラスのgetCheckedItemPositionsメソッドを用いて、
			 * 何番目のアイテムが選択されているかという情報を持ったリストを取得しています。
			(例えば3番目の項目が選択されていた場合には、リストの3番目に「true」が入っています。)
			 */
			checked = select_file.getCheckedItemPositions();
			for(int i=0; i<myData.size(); i++){
				//data = (CustomData) select_file.getItemAtPosition(i);
				if(checked.valueAt(i) == true){
					String ans= myData.get(i).getfilePass();
					path.add(ans);
					// 選択しているファイルのパスをログに出力
					Log.d("checked", path.get(i));
				}
			}
			if(path.isEmpty()){

			}
			else{
				HView02.setVisibility(View.GONE);
				HView01.setVisibility(View.VISIBLE);
				select_file.setChoiceMode(ListView.CHOICE_MODE_NONE);
			}
		}
		else if(v.getId() == R.id.Cut){

		}
		else if(v.getId() == R.id.Paste){
			if(path.isEmpty()){
				Toast.makeText(this,"選択されていません",Toast.LENGTH_SHORT).show();
			}
			else{
				for(int i =0; i<path.size(); i++){
					//＠＠ファイル元,作成するファイルの名前
					fcon.FileCopy(path.get(i),CurPath+"/TEST3.txt");
				}
				if(Cutflag == true){
					for(int i=0; i<path.size(); i++){
						fcon.FileDelete(path.get(i));
					}
				}

				//終わったらclearメソッドでリストを空にする
				path.clear();

			}
		}
	}

	public static class MyDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("クラウドの選択");
			builder.setItems(service,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// リスト選択時の処理
					// which は、選択されたアイテムのインデックス
					if (which == 0) {
						//Log.d("dialog test", "google ");
						instance.cloudIntent(GDtest.class);
					} else if (which == 1) {
						//Log.d("dialog test", "microsoft");
						instance.cloudIntent(SDtest.class);

					} else if (which == 2) {
						instance.cloudIntent(SDtest.class);

					}
				}
			});

			return builder.create();
		}
	}

	public void setBilder(){

		builder.setTitle(mWarning);
		builder.setMessage("SDカードがセットされていません。");
		builder.show();
	}

	// cloud intent
	public void cloudIntent(Class<?> cloud) {
		Intent intent = new Intent(this, cloud);
		startActivity(intent);

	}

}
