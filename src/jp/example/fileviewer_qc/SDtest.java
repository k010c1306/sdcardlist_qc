package jp.example.fileviewer_qc;

import java.io.InputStream;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileviewer.R;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

public class SDtest extends Activity implements LiveAuthListener, OnClickListener {

	private LiveAuthClient auth;
	private LiveConnectClient client;

	private TextView resultTextView;
	private TextView userInfoTextView;
	private TextView fileListTextView;

	private Button signInButton;
	private Button signOutButton;
	private Button fileListButton;
	private Button userInfoButton;
	private Button folderButton;
	private Button moveButton;
	private Button fileSignInButton;

	private SDtest instance;
	boolean boolean_result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdtest);
		findViews();
		setListeners();
		this.auth = new LiveAuthClient(this, MyConstants.APP_CLIENT_ID);
		instance = this;
	}

	@Override
	protected void onStart() {
		super.onStart();
		//Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic");
		//this.auth.login(instance, scopes, this);
	}

	private void findViews() {
		// TextVIew
		this.resultTextView = (TextView) findViewById(R.id.resultTextView);
		this.userInfoTextView = (TextView) findViewById(R.id.userInfoTextView);

		// Button
		this.signOutButton = (Button) findViewById(R.id.signOutButton);
		this.signInButton = (Button) findViewById(R.id.signInButton);
		this.fileListButton = (Button) findViewById(R.id.fileListButton);
		this.userInfoButton = (Button) findViewById(R.id.userInfoButton);
		this.folderButton = (Button) findViewById(R.id.folderButton);
		this.moveButton = (Button) findViewById(R.id.moveButton);
		this.fileSignInButton = (Button) findViewById(R.id.fileSignInButton);
	}

	private void setListeners() {
		signOutButton.setOnClickListener(this);
		signInButton.setOnClickListener(this);
		fileListButton.setOnClickListener(this);
		userInfoButton.setOnClickListener(this);
		folderButton.setOnClickListener(this);
		moveButton.setOnClickListener(this);
		fileSignInButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == signOutButton) {
			logout();
		} else if (v == signInButton) {
			//boolean tmp = moreScopes();
			//if(tmp)greetUser();
			downloadFile();
		} else if (v == fileListButton) {
			getFileList();
		} else if (v == userInfoButton) {
			greetUser();
		} else if (v == folderButton) {
			readFolder();
		} else if (v == moveButton) {
			moveFile();
		} else if (v == fileSignInButton) {
			moreScopes_file();
		}

	}

	// onAuthComplete, onAuthError
	// ユーザーが正常にサインインし、要求されたスコープに同意したかどうかを調べる
	public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
		if (status == LiveStatus.CONNECTED) {
			this.resultTextView.setText("Signed in.");
			client = new LiveConnectClient(session);
		}
		else {
			this.resultTextView.setText("Not signed in.");
			client = null;
		}
	}

	public void onAuthError(LiveAuthException exception, Object userState) {
		this.resultTextView.setText("Error signing in: " + exception.getMessage());
		client = null;
	}

	/*
	 * ボタンなどの別のコントロールからサインイン プロセスを開始する.
	 * スコープについて
	 * wl.basic 			user基本情報・連絡先に対するアクセス許可
	 * wl.skydrive_update	SkyDriveのファイルに対する読み書きの許可
	 * その他	http://msdn.microsoft.com/ja-jp/library/live/hh243646.aspx
	 *スコープの追加要求も可のはず
	 */

	public boolean moreScopes() {

		auth.login(this, Arrays.asList(new String[] { "wl.basic" }), new LiveAuthListener() {
			public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
				if (status == LiveStatus.CONNECTED) {
					resultTextView.setText("Signed in.");
					client = new LiveConnectClient(session);
					Toast.makeText(SDtest.this, "more", Toast.LENGTH_SHORT).show();
					boolean_result = true;
				} else {
					resultTextView.setText("Not signed in.");
					client = null;
					boolean_result = false;
				}
			}

			public void onAuthError(LiveAuthException exception, Object userState) {
				resultTextView.setText("Error signing in: " + exception.getMessage());
				client = null;
			}
		});
		return boolean_result;
	}

	// SkyDriveのファイルの読み書き許可を取得する

	public boolean moreScopes_file() {

		//auth.login(this, Arrays.asList(new String[] { "wl.skydrive_update" }), new LiveAuthListener() {
		auth.login(this, Arrays.asList(MyConstants.SCOPES), new LiveAuthListener() {
			public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
				if (status == LiveStatus.CONNECTED) {
					resultTextView.setText("Signed in, can Read and Write.");
					client = new LiveConnectClient(session);
					Toast.makeText(SDtest.this, "more", Toast.LENGTH_SHORT).show();
					boolean_result = true;
				} else {
					resultTextView.setText("Not signed in.");
					client = null;
					boolean_result = false;
				}
			}

			public void onAuthError(LiveAuthException exception, Object userState) {
				resultTextView.setText("Error signing in: " + exception.getMessage());
				client = null;
			}
		});
		return boolean_result;
	}

	// サインアウト
	public void logout() {
		this.auth.logout(this);
		Toast.makeText(this, "LOG OUT", Toast.LENGTH_SHORT).show();
	}

	// サインインしたuserの情報を取得する 必要性は不明
	public void greetUser() {
		client.getAsync("me", new LiveOperationListener() {
			public void onComplete(LiveOperation operation) {
				JSONObject result = operation.getResult();
				userInfoTextView.setText("Hello, " + result.optString("name") + "!");
			}

			public void onError(LiveOperationException exception, LiveOperation operation) {
				userInfoTextView.setText("Error getting name: " + exception.getMessage());
			}
		});
	}

	// LiveConnectClient.getAsyncを利用してファイル一覧を取得する
	//TODO かんせいさせる？肝心のリスト取得などが一切ない
	public void getFileList() {
		client.getAsync("me/skydrive/files", new LiveOperationListener() {
			public void onComplete(LiveOperation operation) {
				JSONObject result = operation.getResult();
				//Log.d("FileList json check", result.toString());

				JsonP(result);

				//	fileListTextView.setText("Files List : " + result.toString() + " **** ");
				//fileListTextView.setText("Files List : " + result.optString("upload_location ") + " **** ");

			}

			public void onError(LiveOperationException exception, LiveOperation operation) {
				fileListTextView.setText("Error getting name: " + exception.getMessage());
			}
		});
	}

	// フォルダのプロパティを取得する
	public void readFolder() {
		client.getAsync("folder.3d5fdf36f39f78c5", new LiveOperationListener() {
			public void onComplete(LiveOperation operation) {
				JSONObject result = operation.getResult();
				resultTextView.setText("Folder ID = " + result.optString("id") +
						", name = " + result.optString("name"));
			}

			public void onError(LiveOperationException exception, LiveOperation operation) {
				resultTextView.setText("Error reading folder: " + exception.getMessage());
			}
		});
	}

	// ファイルの移動
	//TODO 未使用・動作未確認
	// folder id が適当であるため動くわけがない
	public void moveFile() {
		client.moveAsync("file.a6b2a7e8f2515e5e.A6B2A7E8F2515E5E!120",
				"folder.a6b2a7e8f2515e5e.A6B2A7E8F2515E5E!145", new LiveOperationListener() {
					public void onComplete(LiveOperation operation) {
						resultTextView.setText("File moved.");
					}

					public void onError(LiveOperationException exception, LiveOperation operation) {
						resultTextView.setText("Error moving file: " + exception.getMessage());
					}
				});
	}

	public void downloadFile() {
		String fileId = "file.3D5FDF36F39F78C5%21104/picture?type=thumnail";
		//	String file = "file.a6b2a7e8f2515e5e.A6B2A7E8F2515E5E!131/picture?type=thumbnail";
		client.downloadAsync(fileId, new LiveDownloadOperationListener() {
			public void onDownloadCompleted(LiveDownloadOperation operation)
			{
				try {
					resultTextView.setText("Picture downloaded.");
					InputStream input = operation.getStream();
					Bitmap bMap = BitmapFactory.decodeStream(input);
					//picture.setImageBitmap(bMap);
					input.close();
				}
				catch (java.io.IOException ex) {
					resultTextView.setText("Error downloading picture: " + ex.getMessage());
				}
			}

			public void onDownloadFailed(LiveOperationException exception, LiveDownloadOperation operation)
			{
				resultTextView.setText(exception.getMessage());
			}

			public void onDownloadProgress(int totalBytes, int bytesRemaining, LiveDownloadOperation operation)
			{
				resultTextView.setText("Downloading picture... " + bytesRemaining + " bytes downloaded " +
						"(" + (bytesRemaining / totalBytes) * 100 + "%)");
			}
		});
	}

	/**
	 * SkyDriveからのJSON解析クラス.
	 * @param fileName 	SkyDrive上のファイル名を入れるString配列
	 * @param rootObject 	SkyDriveから取得するJsonデータ
	 * @param dataArray 	複数Fileの配列が入る
	 * @param dataObject 	1Fileの情報が入る
	 * @author Kenta Nakashima
	 */

	private static void JsonP(JSONObject result) {
		JSONObject rootObject = result;

		String[] fileName = null;
		String[] fileSize = null;
		String[] fileUpdate = null;
		String[] fileType = null;

		try {
			JSONArray dataArray = rootObject.getJSONArray("data");
			// １Fileの情報を入れる予定
			JSONObject[] dataObject = new JSONObject[dataArray.length()];

			for (int i = 0; i < dataArray.length(); i++) {

				dataObject[i] = dataArray.getJSONObject(i);

			//TODO	下記　要修正
			//	fileName[i] = dataObject[i].getString("name");
			//	fileSize[i] = dataObject[i].getString("size");
			//	fileUpdate[i] = dataObject[i].getString("update_time");
			//	fileType[i] = dataObject[i].getString("type");

				//Log.d("debug "+ i + " : ", dataArray.optString(i, "name").toString());
				// １Fileの名前
				Log.d("debug_1 " + i + " : ", dataObject[i].getString("name"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
