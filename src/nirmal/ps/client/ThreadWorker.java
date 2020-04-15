package nirmal.ps.client;

import nirmal.ps.model.EmployeeDetails;

import java.util.List;

import nirmal.ps.model.Employee;
import nirmal.ps.util.JavaUtil;

public class ThreadWorker extends Thread {
	
	//private String readFileName;
	private EmployeeDetails employeeDetails;
	
	public ThreadWorker(String requestFileName) {
		//this.readFileName = requestFileName;
		this.setName(requestFileName);
	}
	
	public List<Employee> getEmployeeDetails() {
		return this.employeeDetails.getFileContents();
	}
		
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		System.out.println("Thread has started reading the file: " + this.getName());
		employeeDetails = 
				(EmployeeDetails)JavaUtil.convertJSONtoJava("http://localhost:3000/api/getFileContents/" + this.getName(), 
						new EmployeeDetails());
		
		System.out.println("Thread has finished reading the file: " + this.getName() + " in " + 
						(System.currentTimeMillis() - startTime) + "ms");		
	}

}
