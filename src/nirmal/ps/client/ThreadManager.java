package nirmal.ps.client;

import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

//import nirmal.ps.model.Employee;
//import nirmal.ps.model.EmployeeDetails;
import nirmal.ps.model.FileDetails;
import nirmal.ps.model.FilesCollection;
import nirmal.ps.util.JavaUtil;

public class ThreadManager {
	
	private FilesCollection responseFilesCollection = null;
	private int actualFileCount = 0, statusCount = 0;
	private StringBuilder responseStatus;
	private ThreadWorker threadWorker = null;
	private List<ThreadWorker> threadPool = null;
		
	public ThreadManager() {
		responseFilesCollection = 
				(FilesCollection)JavaUtil.convertJSONtoJava("http://localhost:3000/api/getDirDetails", new FilesCollection());	
		
		if(responseFilesCollection.getFileDetails()!=null) {
			//update job status: NotYetStarted
			UpdateJobStatus(ThreadJobStatus.NotYetStarted.toString());
			//get the actual files count
			actualFileCount = responseFilesCollection.getFileDetails(). size();
		}
		
		
	}
	
	public String getJobStatus(){		
		
		responseStatus = new StringBuilder();
		//check job status for 'NotYetStarted'
		getJobWiseCount(ThreadJobStatus.NotYetStarted.toString());
		
		//check job status for 'InProgress'
		getJobWiseCount(ThreadJobStatus.InProgress.toString());
		
		//check job status for 'Completed'
		getJobWiseCount(ThreadJobStatus.Completed.toString());
		
		//check job status for 'Error'
		getJobWiseCount(ThreadJobStatus.Error.toString());		
		
		return responseStatus.toString();
	}
	
	private void getJobWiseCount(String requestStatus) {
		statusCount =  
				(int) responseFilesCollection.getFileDetails().stream().filter(p->p.getStatus().equals(requestStatus)).count();
		responseStatus.append(requestStatus + " jobs running " + (statusCount) + " out of " + (actualFileCount));
		responseStatus.append("\n");
	}
	
	public void ProcessFileRead() {
		try {
			threadPool = new ArrayList<ThreadWorker>();
			for(FileDetails processFile: this.responseFilesCollection.getFileDetails()) {
				processFile.setStatus(ThreadJobStatus.InProgress.toString());
				
				threadWorker = new ThreadWorker(processFile.getFileName());
				threadWorker.start();
				threadPool.add(threadWorker);
			}
			System.out.println(getJobStatus());
			Thread.sleep(10); //
			updateReadFileContents();	
			System.out.println(getJobStatus());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void updateReadFileContents() {
		while(threadPool.stream().filter(p->!p.isAlive() && p.getName() != "").count() > 0) {
			var workerObj = threadPool.stream().filter(f->!f.isAlive() && f.getName() != "").findFirst();			
			if(workerObj.isPresent())
			{		
				threadWorker = workerObj.get();
				this.responseFilesCollection.updateFileEmployeeDetails(threadWorker.getName(), 
						threadWorker.getEmployeeDetails(), ThreadJobStatus.Completed.toString());
				threadWorker.setName("");
			}
		}
		System.out.println("**Over All Process Completed**");
	}

	public List<FileDetails> getJobDetails() {
		return responseFilesCollection.getFileDetails();
	}	
		
	private void UpdateJobStatus(String jobStatus) {
		for(FileDetails updateFileDetails: this.responseFilesCollection.getFileDetails()) {
			updateFileDetails.setStatus(jobStatus);
		}
	}
	
	public enum ThreadJobStatus{
		NotYetStarted,
		InProgress,
		Completed,
		Error
	}
}


