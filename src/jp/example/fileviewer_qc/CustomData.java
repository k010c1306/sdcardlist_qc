package jp.example.fileviewer_qc;

public class CustomData {
	public String fileName;
	public String lastmodified;
	public String filePass;
	public String fullfilePass;
	public int fileSize;
	
	

	public void setfileName(String text){
		fileName = text;
	}
	
	public String getfileName(){
		return fileName;
	}
	
	public void setlastmodified(String text) {
		lastmodified = text;
	}

	public String getlastmodified() {
		return lastmodified;
	}

	public void setfileSize(int i) {
		fileSize = i;
	}

	public int getfileSize() {
		return fileSize;
	}
	public void setfilePass(String text){
		filePass = text;
	}
	
	public String getfilePass(){
		return filePass;
	}

	public String setfullfilePass(String path) {
		 fullfilePass=path;
		
		return path;
	}
	public String getfullfilePass(){
		return fullfilePass;
	}
}
