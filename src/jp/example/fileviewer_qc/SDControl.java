package jp.example.fileviewer_qc;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;

public class SDControl {
	private File mSDdir;
	public File getSD(){
		mSDdir = Environment.getExternalStorageDirectory();
		return mSDdir;
	}
	
	boolean SDcheck() {

		/*状態を取得Environment.MEDIA_MOUNTED	読み書き可能
		Environment.MEDIA_MOUNTED_READ_ONLY	読み込みのみ可能
		Environment.MEDIA_REMOVED	マウントされていない
		 */
		String SDstatus = Environment.getExternalStorageState();
		boolean result=true;
		// SDカードの（読み書き可能OR読み込みのみか）確認　　
		if (SDstatus.equals(Environment.MEDIA_MOUNTED) == false
				&& SDstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY) == false) {
			
			result =false;
			//出来ないなら
			
		}
		return result;

	}

	
}
