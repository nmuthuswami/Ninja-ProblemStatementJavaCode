package nirmal.ps.client;

public class Master {

	public static void main(String[] args) {
		
		ThreadManager tm = new ThreadManager();
		/*String checkStatus = tm.getJobStatus();
		System.out.println(checkStatus);*/
		tm.ProcessFileRead();
	}

}
