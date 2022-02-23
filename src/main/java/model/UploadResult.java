package model;

/**
 * 上传结果对象
 * @author liguanghui
 *
 */
public class UploadResult{
	int code;
	String absoluteFilePath;
	String fileName;
	long fileSize;
	String extension;
	boolean success;
	public UploadResult(int code,boolean success,String msg){
		this.code=code;
		this.success=success;
		this.msg=msg;
	}
	public UploadResult(int code, String absoluteFilePath, String fileName, long fileSize, String extension,
			boolean success, String msg) {
		this.code = code;
		this.absoluteFilePath = absoluteFilePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.extension = extension;
		this.success = success;
		this.msg = msg;
	}
	String msg;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}
	public void setAbsoluteFilePath(String absoluteFilePath) {
		this.absoluteFilePath = absoluteFilePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}