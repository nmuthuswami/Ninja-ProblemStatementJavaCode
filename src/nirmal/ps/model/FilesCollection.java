package nirmal.ps.model;

import java.util.List;

public class FilesCollection {
	private List<FileDetails> fileDetails;

	public List<FileDetails> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(List<FileDetails> fileDetails) {
		this.fileDetails = fileDetails;
	}	
	
	public void updateFileEmployeeDetails(String requestFileName,List<Employee> requestEmployeeDetails, String requestStatus) {
		FileDetails fileDetails = this.fileDetails.stream().filter(f->f.getFileName() == requestFileName).findFirst().get();
		fileDetails.setFileContents(requestEmployeeDetails);
		fileDetails.setStatus(requestStatus);
		System.out.println(fileDetails.getFileName() + ", employee details updated successfully.");
	}
	
	public void updateFileDetailsStatusOnly(String requestFileName, String requestStatus) {
		System.out.println("Test: " + requestFileName);
		FileDetails fileDetails = this.fileDetails.stream().filter(f->f.getFileName() == requestFileName).findFirst().get();
		fileDetails.setStatus(requestStatus);
		System.out.println(fileDetails.getFileName() + ", employee details updated successfully.");
	}
}
