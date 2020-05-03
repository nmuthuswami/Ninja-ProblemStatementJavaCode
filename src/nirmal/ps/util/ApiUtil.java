package nirmal.ps.util;

import java.io.IOException;

import okhttp3.*;

public class ApiUtil {
	
	
	private static final OkHttpClient httpClient = new OkHttpClient();
	
	public static void WriteApi(String requestKey, String requestValue) throws Exception {
		
		RequestBody formBody = new FormBody.Builder()
				.add(requestKey, requestValue)
				.build();
		
		Request request = new Request.Builder()
									 .url("http://localhost:3000/api/writeContent")
									 .addHeader("test","test write api")
									 .post(formBody)
									 .build();
		
		try(Response response = httpClient.newCall(request).execute()){
			if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			
			System.out.println(response.body().string());
		}
		
	}

}
