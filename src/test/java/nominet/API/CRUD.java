package nominet.API;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utilities.PropFileHandler;


public class CRUD {

	String responseBody;
	int statusCode = 0;
	JsonPath jsonPathEvaluator;
	static String id;
	static RequestSpecification httpRequest;
	static Response response;
	Response responseAfterDelete;
	String responseAfterDeleteBody;
	int statusCodeAfterDelete;
	
	@BeforeTest
	public void setupAuth() {
		RestAssured.baseURI = PropFileHandler.readProperty("baseURL");
	
		//BasicAuth 
		PreemptiveBasicAuthScheme authScheme =  new PreemptiveBasicAuthScheme();
		authScheme.setPassword(PropFileHandler.readProperty("token"));

		RestAssured.authentication = authScheme;
		httpRequest = RestAssured.given();
	}
	
	@Test(priority = 1)
	public void createGist() throws IOException {
		String body = "{\r\n"
				+ "  \"description\": \"Testing Gist Creation API\",\r\n"
				+ "  \"public\": true,\r\n"
				+ "  \"files\": {\r\n"
				+ "    \"myFile.txt\": {\r\n"
				+ "      \"content\": \"The is a test Gist\"\r\n"
				+ "        }\r\n"
				+ "    }\r\n"
				+ "}";

		httpRequest.body(body);
		response = httpRequest.request(Method.POST,"/gists");

		responseBody= response.getBody().asPrettyString();
		statusCode = response.getStatusCode();
		System.out.println("StatusCode :" + statusCode);
		System.out.println("Response Body: "+ responseBody);
		
		Assert.assertEquals(statusCode, 201);
		Assert.assertTrue(responseBody.contains("myFile.txt") , "File not created. \n");
		Assert.assertTrue(responseBody.contains("Testing Gist Creation API") , "File not created. \n");

		jsonPathEvaluator = response.jsonPath();
		id = jsonPathEvaluator.get("id");
		System.out.println("ID from Response " + id);
	}

	@Test(priority = 2)
	public void getAllGists() throws IOException {
	response = httpRequest.request(Method.GET,"/gists");
	
	responseBody= response.getBody().asPrettyString();
	statusCode = response.getStatusCode();
	System.out.println("StatusCode :" + statusCode);
	
	Assert.assertEquals(statusCode, 200);
	System.out.println("ID created:-" + id);
	Assert.assertTrue(responseBody.contains("myFile.txt") , "File Name not matched \n");
	Assert.assertTrue(responseBody.contains(id) , "File not created. \n");
	
	}
	
	@Test(priority = 3)
	public void updateGist() throws IOException {
		
	String body = "{\r\n"
			+ "    \"files\": {\r\n"
			+ "        \"myFile.txt\": {\r\n"
			+ "            \"content\": \"Text successfully updated\"\r\n"
			+ "        }\r\n"
			+ "    }\r\n"
			+ "}";
	
    httpRequest.body(body);
	response = httpRequest.request(Method.PATCH,"/gists/" + id);
	
	responseBody= response.getBody().asPrettyString();
	statusCode = response.getStatusCode();
	System.out.println("StatusCode :" + statusCode);
	
	Assert.assertEquals(statusCode, 200);
	Assert.assertTrue(responseBody.contains("Text successfully updated") , "File not updated. \n");
	}
	
	@Test(priority = 4)
	public void deleteGist() throws IOException {
    
	response = httpRequest.request(Method.DELETE,"/gists/" + id);
	
	statusCode = response.getStatusCode();
	System.out.println("StatusCode :" + statusCode);
	
	Assert.assertEquals(statusCode, 204);
	
	responseAfterDelete = httpRequest.request(Method.DELETE,"/gists/" + id);
	responseAfterDeleteBody= responseAfterDelete.getBody().asPrettyString();
	statusCodeAfterDelete = responseAfterDelete.getStatusCode();
	
	System.out.println("StatusCode :" + statusCodeAfterDelete);
	
	Assert.assertEquals(statusCodeAfterDelete, 404);
	Assert.assertTrue(responseAfterDeleteBody.contains("Not Found") , "Gist file not deleted. \n");
	
	}

}
