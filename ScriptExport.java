import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ephesoft.dcma.script.IJDomScript;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
/**
 * @author Ephesoft
 * @version 1.0
 */
public class ScriptExport implements IJDomScript {

	//This is the path that the custom Export files will be exported to. This script will use the data populated in that folder
	public static String CUSTOM_EXPORT_FILE_PATH = "C:\\Ephesoft\\SharedFolders\\final-drop-folder\\CustomExport\\";
	public static HashMap<String, String> DOCTYPE_WEBHOOK_MAPPING = new HashMap<String, String>();
	//set the CLEAN_CUSTOMEXPORT_DIRECTORY flag to true if you wish to clean the custom export folder
	public static Boolean CLEAN_CUSTOM_EXPORT_DIRECTORY  = true;

	public Object execute(Document documentFile, String methodName, String docIdentifier) {
		try {
			System.out.println("*************  Inside ScriptTemplate scripts.");
			System.out.println("*************  Start execution of the ScriptTemplate scripts.");

			//use this HashMap to map Ephesoft Transact document types to different webhook addresses
			//Ephesoft Document time is the key and the webhook address is the value, these are case sensitive
			DOCTYPE_WEBHOOK_MAPPING.put("Statement", "https://www.workato.com/webhooks/rest/xxxxxxx-xxxxx-xxxx-xxxxxx/mywebhook");
			DOCTYPE_WEBHOOK_MAPPING.put("w2", "https://www.workato.com/webhooks/rest/xxxxxxx-xxxxx-xxxx-xxxxxx/mywebhook");
			//Copy the line above and add more webhooks and document types should you need to configure more document type releases.
			

			//Iterate through the Ephesoft Batch XML file
			methodTemplate(documentFile);

		} catch (Exception e) {
			System.out.println("*************  Error occurred in scripts." + e.getMessage());
		}
		return null;
	}

	private static String getFileContents(String FileName){

		String FilePath = CUSTOM_EXPORT_FILE_PATH +FileName;
		String fileContents;
		try {
			fileContents = FileUtils.readFileToString(new File(FilePath),"utf-8");

			return fileContents;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	private static void callwebHook(String CustomExportJSONBody, String WebhookAddress, String FileName) {
		try (CloseableHttpClient defaultClient = HttpClients.createDefault()) {
			final HttpPost request = new HttpPost(WebhookAddress);
			request.setHeader(HttpHeaders.ACCEPT, "application/json");

			StringEntity entity = new StringEntity(CustomExportJSONBody);

			request.setEntity(entity);

			final CloseableHttpResponse response = defaultClient.execute(request);
			if(response.getStatusLine().getStatusCode()==200){
				if(CLEAN_CUSTOM_EXPORT_DIRECTORY){
					 FileUtils.forceDelete(new File(CUSTOM_EXPORT_FILE_PATH +FileName));
				}
			}
			if (HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()) {
				System.out.println(EntityUtils.toString(response.getEntity()));
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Basic scripting template that will traverse through a document list and its document level fields.
	 * Includes instantiated string variables containing the most relevant information from batch xml tags.  
	 * Users of this script should change the class name for the appropriate module/plugin, as well as the 
	 * this method's name. 
	 */

	private void methodTemplate(Document document)
	{
		//Gets the document root element.
		Element docRoot = document.getRootElement();
		//String variables containing information regarding the current batch class excuting this script.
		String batchInstanceId = docRoot.getChildText("BatchInstanceIdentifier");
		System.out.println(batchInstanceId);
		//Get and traverse through documents list.
		List<Element> docList = docRoot.getChild("Documents").getChildren("Document");
		for (Element doc : docList)
		{
			//String variables containing information regarding the individual documents in the documents list.
			String docType = doc.getChildText("Type");	
			String docID = doc.getChildText("Identifier");	
			//Check if doc type is in mapping config
			if(DOCTYPE_WEBHOOK_MAPPING.containsKey(docType)){
				System.out.println(docType);
				//get the webhook payload from file
				String customExportFileName = batchInstanceId +"-"+docID+".txt";
				String webhookPayload = getFileContents(customExportFileName);
				//call the webhook
				callwebHook(webhookPayload,DOCTYPE_WEBHOOK_MAPPING.get(docType),customExportFileName);
			}
		}
	}
}