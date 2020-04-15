package nirmal.ps.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class JavaUtil {
	
	private static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
	}
	
	public static String convertJavatoJSON(Object requestObj) {
		String jsonResult="";
		try {
			jsonResult = mapper.writeValueAsString(requestObj);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return jsonResult;
		
	}
	
	public static Object convertJSONtoJava(String requestURL,Object requestObj) {
		Object responseObj=null;
		try {
			responseObj = mapper.readValue(new URL(requestURL), requestObj.getClass());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseObj;
	}

}
