package jp.example.fileviewer_qc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class FileControl {

	public Intent FileOpen(File file ,PackageManager pm){
		boolean isIntetntSafe;
		Intent intent=new Intent(Intent.ACTION_VIEW);
		if(getSuffix(file.getName()).equals("txt")){

			intent.setDataAndType(Uri.parse("file://"+file.getPath()),"text/plain");

		}else if(getSuffix(file.getName()).equals("jpg")||
				getSuffix(file.getName()).equals("jpeg")||
				getSuffix(file.getName()).equals("bmp")||
				getSuffix(file.getName()).equals("png")||
				getSuffix(file.getName()).equals("gif")||
				getSuffix(file.getName()).equals("tiff")||
				getSuffix(file.getName()).equals("tif")){
			intent.setDataAndType(Uri.parse("file://"+file.getPath()),"image/*");

		}else if(getSuffix(file.getName()).equals("mp3")||
				getSuffix(file.getName()).equals("m4a")||
				getSuffix(file.getName()).equals("mid")||
				getSuffix(file.getName()).equals("midi")||
				getSuffix(file.getName()).equals("wav")){
			intent.setDataAndType(Uri.parse("file://"+file.getPath()),"audio/*");
		}else if(getSuffix(file.getName()).equals("mpg")||
				getSuffix(file.getName()).equals("mpeg")||
				getSuffix(file.getName()).equals("mp4")||
				getSuffix(file.getName()).equals("avi")||
				getSuffix(file.getName()).equals("wmv")){
			intent.setDataAndType(Uri.parse("file://"+file.getPath()),"video/*");	
		}else{
			return null;
		}

		List<ResolveInfo> activitys = pm.queryIntentActivities(intent, 0);
		isIntetntSafe =activitys.size()>0;
		return intent;
	}
	/**
	 * ファイル名から拡張子を返します。
	 * @param fileName ファイル名
	 * @return ファイルの拡張子
	 */
	public String getSuffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}

	/**
	 * ファイル名から拡張子除いたファイル名を返します。
	 * @param fileName ファイル名
	 * @return 拡張子無しのファイル名
	 */
	public String removeFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return filename;
		} else if (lastDotPos == 0) {
			return filename;
		} else {
			return filename.substring(0, lastDotPos);
		}
	}
	
	
	//Fileサイズ取得用
	public int getfilesize(File file) {
		// ファイル以外は0固定
		if (!file.isFile()) {
			return 0;
		}
		int size = 0;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			size = stream.available();
		} catch (IOException e) {
			//	Log.d("return size error", "a");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return size;
	}
	
	
	
	//ファイルコピー用
	public boolean FileCopy(String INPUT_FILE_PATH,String OUTPUT_FILE_PATH){
		File iFile = new File(INPUT_FILE_PATH);
		File oFile = new File(OUTPUT_FILE_PATH);
		boolean result;
		try {
			//コピー元ファイル
			FileChannel iChannel = new FileInputStream(iFile).getChannel();
			//コピー先ファイル
			FileChannel oChannel = new FileOutputStream(oFile).getChannel();
			iChannel.transferTo(0, iChannel.size(), oChannel);
			iChannel.close();
			oChannel.close();
			result=true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result=false;
		} catch (IOException e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	public boolean Filerename (String Bpath, String Apath){
		boolean result;      
		//変更前ファイル名
		File fileA = new File(Bpath);

		//変更後のファイル名
		File fileB = new File(Apath);


		if(fileA.renameTo(fileB)){
			//ファイル名変更成功
			System.out.println("ファイル名変更成功");
			result=true;
		}else{
			//ファイル名変更失敗
			System.out.println("ファイル名変更失敗");
			result=false;
		}
		return result;
	}





	boolean FileDelete(String path){
		File file = new File(path); 
		boolean result=file.delete();
		return result;
	}
	public boolean mkdir(String path){
		File file = new File(path);
		return file.mkdir();
	}
}
