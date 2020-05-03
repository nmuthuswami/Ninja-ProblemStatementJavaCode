package nirmal.ps.client;

import nirmal.ps.model.EmployeeDetails;

import java.util.List;

import nirmal.ps.model.Employee;
import nirmal.ps.util.*;

public class ThreadWorker extends Thread {
	
	//private String readFileName;
	private EmployeeDetails employeeDetails;
	private List<Employee> employeesList;
	private boolean isForRead = false;
	
	public ThreadWorker(String requestFileName, boolean isRead, List<Employee> requestEmployeeDetails) {
		//this.readFileName = requestFileName;
		this.setName(requestFileName);
		this.isForRead = isRead;
		this.employeesList = requestEmployeeDetails;
	}	

	public List<Employee> getEmployeeDetails() {
		return this.employeeDetails.getFileContents();
	}
		
	@Override
	public void run() {
		if(isForRead == true) {
			long startTime = System.currentTimeMillis();
			System.out.println("Thread has started reading the file: " + this.getName());
			employeeDetails = 
					(EmployeeDetails)JavaUtil.convertJSONtoJava("http://localhost:3000/api/getFileContents/" + this.getName(), 
							new EmployeeDetails());
			
			System.out.println("Thread has finished reading the file: " + this.getName() + " in " + 
						(System.currentTimeMillis() - startTime) + "ms");
		}
		else {
			long startTime = System.currentTimeMillis();
			System.out.println("Thread has started writing files into database: " + this.getName());		
			String requestValue = JavaUtil.convertJavatoJSON(this.employeesList);
			String requestKey = this.getName();
			
			try {
				ApiUtil.WriteApi(requestKey, requestValue);
				System.out.println("Thread has finished writing files into database: " + this.getName() + " in " + 
						(System.currentTimeMillis() - startTime) + "ms");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Thread has aborted while writing files into database: " + this.getName() + " in " + 
						(System.currentTimeMillis() - startTime) + "ms");
			}
			
		}
	}

}
