package nirmal.ps.client;

public class Master {

	public static void main(String[] args) {
		
		ThreadManager tm = new ThreadManager();
		//thread to read file contents
		tm.ProcessFileRead();
		//thread to write file contents
		tm.ProcessFileWrite();
	
	}

}
