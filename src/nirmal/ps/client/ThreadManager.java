package nirmal.ps.client;

import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

//import nirmal.ps.model.Employee;
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
		//api call to get the directory details
		responseFilesCollection = 
				(FilesCollection)JavaUtil.convertJSONtoJava("http://localhost:3000/api/getDirDetails", new FilesCollection());	
		
		if(responseFilesCollection.getFileDetails()!=null) {
			//update job status: NotYetStarted
			UpdateJobStatus(ThreadJobStatus.NotYetStarted.toString());
			//get the actual files count
			actualFileCount = responseFilesCollection.getFileDetails(). size();
		}
		
		
	}
	
	//To check each status of each thread (read/write)
	public String getJobStatus(boolean isRead){		
		
		responseStatus = new StringBuilder();
		responseStatus.append("\n");
		responseStatus.append("***OVERALL JOB STATUS***");
		responseStatus.append("\n");
		//check job status for 'NotYetStarted'
		getJobWiseCount(ThreadJobStatus.NotYetStarted.toString());
		
		if(isRead == true) {
			//check job status for 'ReadingInProgress'
			getJobWiseCount(ThreadJobStatus.ReadingInProgress.toString());
			
			//check job status for 'ReadingCompleted'
			getJobWiseCount(ThreadJobStatus.ReadingCompleted.toString());
			
			//check job status for 'ReadError'
			getJobWiseCount(ThreadJobStatus.ReadError.toString());		
		}
		else {
			//check job status for 'WritingInProgress'
			getJobWiseCount(ThreadJobStatus.WritingInProgress.toString());
			
			//check job status for 'WritingCompleted'
			getJobWiseCount(ThreadJobStatus.WritingCompleted.toString());
			
			//check job status for 'WriteError'
			getJobWiseCount(ThreadJobStatus.WriteError.toString());	
		}
		responseStatus.append("\n");
		return responseStatus.toString();
	}
	
	//Get job-wise count based on the status
	private void getJobWiseCount(String requestStatus) {
		statusCount =  
				(int) responseFilesCollection.getFileDetails().stream().filter(p->p.getStatus().equals(requestStatus)).count();
		responseStatus.append(requestStatus + " jobs running " + (statusCount) + " out of " + (actualFileCount));
		responseStatus.append("\n");
	}
	
	//Read file content and store the details into employee collection.
	public void ProcessFileRead() {
		try {
			threadPool = new ArrayList<ThreadWorker>();
			for(FileDetails processFile: this.responseFilesCollection.getFileDetails()) {
				processFile.setStatus(ThreadJobStatus.ReadingInProgress.toString());
				
				threadWorker = new ThreadWorker(processFile.getFileName(), true, null);				
				threadWorker.start();
				threadPool.add(threadWorker);
			}
			System.out.println(getJobStatus(true));
			updateReadFileContents(true);	
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//write employee collection into redis db.
	public void ProcessFileWrite() {
		try {
			threadPool = new ArrayList<ThreadWorker>();
			for(FileDetails processFile: this.responseFilesCollection.getFileDetails()) {
				processFile.setStatus(ThreadJobStatus.WritingInProgress.toString());
				threadWorker = new ThreadWorker(processFile.getFileName(), false, processFile.getFileContents());
				threadWorker.start();
				threadPool.add(threadWorker);
			}
			System.out.println(getJobStatus(false));
			updateReadFileContents(false);	
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void updateReadFileContents(boolean isRead) throws InterruptedException {
		//System.out.println("Test Read: " + threadPool.stream().filter(p->!p.isAlive() && p.getName() == "").count());	
		if(threadPool.stream().filter(p->!p.isAlive() && p.getName() == "").count() == actualFileCount) {
			System.out.println(getJobStatus(isRead));
			System.out.println("**Over All " + ((isRead)? "Read" : "Write") + " Process Completed**");
		}
		else {
			//System.out.println("Test Read: " + threadPool.stream().filter(p->!p.isAlive() && p.getName() != "").count());
			while(threadPool.stream().filter(p->!p.isAlive() && p.getName() != "").count() > 0) {
				var workerObj = threadPool.stream().filter(f->!f.isAlive() && f.getName() != "").findFirst();			
				if(workerObj.isPresent())
				{					
					threadWorker = workerObj.get();
					if(isRead == true) {
						this.responseFilesCollection.updateFileEmployeeDetails(threadWorker.getName(), 
								threadWorker.getEmployeeDetails(), 
								ThreadJobStatus.ReadingCompleted.toString());
					}
					else {
						this.responseFilesCollection.updateFileDetailsStatusOnly(threadWorker.getName(), ThreadJobStatus.WritingCompleted.toString());
					}
					threadWorker.setName("");				
				}
			}
			Thread.sleep(10);
			updateReadFileContents(isRead);//recursive
		}
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
		ReadingInProgress,
		ReadingCompleted,
		WritingInProgress,
		WritingCompleted,
		ReadError,
		WriteError
	}
}


