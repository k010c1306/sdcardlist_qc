package jp.example.fileviewer_qc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;

public class GDtest extends Activity {
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;

	static List<File> result;
	static List<File> result_parent;

	private static Drive service;
	private static GoogleAccountCredential credential;
	public List<File> fileList;
	private Button btn1, btn2, btn3, btn4;
	private ImageButton imgBtn;

	private static String parsedText = "";
	private static TextView debug_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.gdtest);

		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					service = getDriveService(credential);
					OAuth();
					Toast.makeText(this, "OAuth AP", Toast.LENGTH_SHORT).show();
					try {
						retrieveAllFiles2(service);
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
			break;
		case REQUEST_AUTHORIZATION:
			OAuth();
			Toast.makeText(this, "OAuth finish", Toast.LENGTH_SHORT).show();
			// 認証後の処理追記？
			break;
		}
	}

	protected void OAuth() {
		new Thread(new Runnable() {
			public void run() {
				try {
					String token = credential.getToken();
					Log.i("OAuth_Token:", token);

				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
	}

	File g_Folder;

	// GoogleDrive マイドライブにフォルダ作成
	private void folderCreate() {
		new Thread(new Runnable() {
			public void run() {
				try {
					String token = credential.getToken();
					Log.i("folederCreate_Token:", token);

					// 下記　ファイル名
					File folder = new File();
					folder.setTitle("GoogleDriveTest");
					folder.setDescription("GoogleDriveTest");
					folder.setMimeType("application/vnd.google-apps.folder");

					g_Folder = service.files().insert(folder).execute();
					Permission permission = new Permission();
					permission.setValue("");
					permission.setType("anyone");
					permission.setRole("reader");

					service.permissions().insert(g_Folder.getId(), permission).execute();

				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	//TODO 要修正　エラー
	private void fileCreate() {
		new Thread(new Runnable() {
			public void run() {
				try {
					String token = credential.getToken();
					Log.i("fileCreate_Token:", token);

					ParentReference parent = new ParentReference();
					parent.setId(g_Folder.getId());
					List<ParentReference> parents = new ArrayList<ParentReference>();
					parents.add(parent);

					File file = new File();
					file.setParents(parents);
					file.setTitle("GoogleDriveTest.txt");
					file.setMimeType("text/plain");

					service.files().insert(file).execute();

				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// file create
	private static void insertFile(final Drive service, final String title, final String description,
			String parentId, String mimeType, String filename) {
		new Thread(new Runnable() {
			public void run() {
				try {
					// File's metadata.
					File file = new File();
					file.setTitle(title);
					file.setDescription(description);
					file.setMimeType("application/vnd.google-apps.drive-sdk");

					file = service.files().insert(file).execute();

					// Print the new file ID.
					System.out.println("File ID: %s" + file.getId());

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return ;
	}

	/**
	   * Insert new file.
	   *
	   * @param service Drive API service instance.
	   * @param title Title of the file to insert, including the extension.
	   * @param description Description of the file to insert.
	   * @param parentId Optional parent folder's ID.
	   * @param mimeType MIME type of the file to insert.
	   * @param filename Filename of the file to insert.
	   * @return Inserted file metadata if successful, {@code null} otherwise.
	   */
	  private static File insertFile2(Drive service, String title, String description,
	      String parentId, String mimeType, String filename) {
	    // File's metadata.
	    File body = new File();
	    body.setTitle(title);
	    body.setDescription(description);
	    body.setMimeType(mimeType);

	    // Set the parent folder.
	    if (parentId != null && parentId.length() > 0) {
	      body.setParents(
	          Arrays.asList(new ParentReference().setId(parentId)));
	    }

	    // File's content.
	    java.io.File fileContent = new java.io.File(filename);
	    FileContent mediaContent = new FileContent(mimeType, fileContent);
	    try {
	      File file = service.files().insert(body, mediaContent).execute();

	      // Uncomment the following line to print the File ID.
	      // System.out.println("File ID: %s" + file.getId());

	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }

	private static List<File> retrieveAllFiles2(final Drive service) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				try {
					String dir_Path = "0B9TeIYyMPJkbMFkxRWwwbVJNQVU";

					//List<File> result = new ArrayList<File>();
					result = new ArrayList<File>();
					result_parent = new ArrayList<File>();

					// Google_FileList オブジェクトとして返ってくる
					// https://developers.google.com/drive/v2/reference/files/list
					//TODO 現在、ゴミ箱も含めたすべてのファイル一覧を作成。。。
					Files.List request = service.files().list();
					Files.List request_parent = service.files().list().setQ("q = '" + dir_Path +"'");


					do {
						try {
							FileList files = request.execute();
							FileList files_parent = request.execute();
							result.addAll(files.getItems());
							result_parent.addAll(files_parent.getItems());

							//File driveFile = service.files().get(dir_Path).execute();

							fileResult(result);
							fileResult(result_parent);

							request.setPageToken(files.getNextPageToken());
						} catch (IOException e) {
							System.out.println("An error occurred: " + e);
							request.setPageToken(null);
						}
					} while (request.getPageToken() != null && request.getPageToken().length() > 0);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return result;

	}

	/**
	 * retrieveAllFiles2で取得したJSONをパースする.
	 *
	 */

	/* キーのメモ
	 * downloadUrl 		ダウンロードリンク
	 * fileExtension 	拡張子
	 * fileSize 		ファイルサイズ
	 * modifiedDate 	更新日時
	 */
	private static void fileResult(List<File> result) {
		//int i = 0;
		for (File num : result) {
			//System.out.println("FR " + i + ": " + num);
			//i++;

			System.out.println("file_name: " + num.get("title"));
			String test = (String) num.get("title");

		}
		System.out.println("file name list end");
	}

}