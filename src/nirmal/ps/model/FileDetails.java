package nirmal.ps.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class FileDetails implements Serializable{
	
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private List<Employee> fileContents;

	public List<Employee> getFileContents() {
		return fileContents;
	}

	public void setFileContents(List<Employee> fileContents) {
		this.fileContents = fileContents;
	}	
		
	private String status;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
