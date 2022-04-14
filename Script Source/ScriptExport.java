import com.ephesoft.dcma.script.IJDomScript;
import com.ephesoft.dcma.util.logger.EphesoftLogger;
import com.ephesoft.dcma.util.logger.ScriptLoggerFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.io.IOException;
import java.util.List;

/**
 * @author ephesoft-cmac
 * @version 2.0
 */
public class ScriptExport implements IJDomScript {

	public static String WEBHOOK_MAPPING_BATCH_URL = "https://www.workato.com/webhooks/rest/xxxxxxx-xxxxx-xxxx-xxxxxx/ephesoftbatchexport";
	private final static EphesoftLogger LOGGER = ScriptLoggerFactory.getLogger(ScriptExport.class);

	public Object execute(Document documentFile, String methodName, String docIdentifier) {
		Exception exception = null;
		try {
			LOGGER.info("*************  Inside Script Export.");
			LOGGER.info("*************  Start execution of the Script Export to Workato recipe.");
			//Remove Extra XML tags for a simple XML structure
			removeXMLNodes(documentFile);
			//Get the XML String
			XMLOutputter xmlOutputter = new XMLOutputter();
			xmlOutputter.setFormat(Format.getPrettyFormat());
			String xmlString = xmlOutputter.outputString(documentFile);
			String JSON;
			//Convert XML to JSON and remove null values
			JSON = convertXMLToJSON(xmlString);
			//Call Webhook
			callWebHook(JSON,WEBHOOK_MAPPING_BATCH_URL);

		} catch (Exception e) {
			LOGGER.error("*************  Error occurred in scripts." + e.getMessage());
			exception = e;
		}
		return exception;
	}


	private static void callWebHook(String CustomExportJSONBody, String WebhookAddress) throws Exception {

		try (CloseableHttpClient defaultClient = HttpClients.createDefault()) {
			final HttpPost request = new HttpPost(WebhookAddress);
			request.setHeader(HttpHeaders.ACCEPT, "text/plain");
			request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			StringEntity entity = new StringEntity(CustomExportJSONBody);
			request.setEntity(entity);
			final CloseableHttpResponse response = defaultClient.execute(request);
			if(response.getStatusLine().getStatusCode()==200){
				LOGGER.info(EntityUtils.toString(response.getEntity()));
			}
			if (HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()) {
				LOGGER.info(EntityUtils.toString(response.getEntity()));
			}
			if (response.getStatusLine().getStatusCode()==404) {
				LOGGER.error("Could not find a Running Recipe for " + WEBHOOK_MAPPING_BATCH_URL);
				throw new Exception("Could not find a Running Recipe for " + WEBHOOK_MAPPING_BATCH_URL);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public static String convertXMLToJSON(String xmlString) throws JSONException {
		//converting xml to json
		JSONObject obj = XML.toJSONObject(xmlString);
		String JSONString = obj.toString();

		JSONString= JSONString.replaceAll("(?<=\"Value\":)\\{\\}","null");
		JSONString= JSONString.replaceAll("\"DocumentLevelFields\":\\{\\},","");
		//LOGGER.info(JSONString);
		return JSONString;
	}


	private static void removeXMLNodes(Document document){
		//Gets the document root element.
		Element docRoot = document.getRootElement();
		//Remove all the Unused Elements from the XML file
		Element BatchLocalPath = docRoot.getChild("BatchLocalPath");
		removeElement(BatchLocalPath);
		Element BatchClassVersion = docRoot.getChild("BatchClassVersion");
		removeElement(BatchClassVersion);
		Element BatchSource = docRoot.getChild("BatchSource");
		removeElement(BatchSource);
		Element BatchClassDescription = docRoot.getChild("BatchClassDescription");
		removeElement(BatchClassDescription);
		Element UNCFolderPath = docRoot.getChild("UNCFolderPath");
		removeElement(UNCFolderPath);
		Element BatchDescription = docRoot.getChild("BatchDescription");
		removeElement(BatchDescription);
		Element BatchPriority = docRoot.getChild("BatchPriority");
		removeElement(BatchPriority);
		Element BatchClassIdentifier = docRoot.getChild("UNCFolderPath");
		removeElement(BatchClassIdentifier);
		Element BatchStatus = docRoot.getChild("BatchStatus");
		removeElement(BatchStatus);

		//Get and traverse through documents list.
		List<Element> docList = docRoot.getChild("Documents").getChildren("Document");
		for (Element doc : docList)
		{
			Element Confidence = doc.getChild("Confidence");
			removeElement(Confidence);
			Element ConfidenceThreshold = doc.getChild("ConfidenceThreshold");
			removeElement(ConfidenceThreshold);
			Element Valid = doc.getChild("Valid");
			removeElement(Valid);
			Element Reviewed = doc.getChild("Reviewed");
			removeElement(Reviewed);
			Element ErrorMessage = doc.getChild("ErrorMessage");
			removeElement(ErrorMessage);
			Element DocumentDisplayInfo = doc.getChild("DocumentDisplayInfo");
			removeElement(DocumentDisplayInfo);
			Element ReviewedBy = doc.getChild("ReviewedBy");
			removeElement(ReviewedBy);
			Element ValidatedBy = doc.getChild("ValidatedBy");
			removeElement(ValidatedBy);


			//Get and traverse through document level fields list.
			List<Element> dlfList = doc.getChild("DocumentLevelFields").getChildren("DocumentLevelField");
			for (Element dlf: dlfList)
			{
				Element Type = dlf.getChild("Type");
				removeElement(Type);
				Element CoordinatesList = dlf.getChild("CoordinatesList");
				removeElement(CoordinatesList);
				Element Page = dlf.getChild("Page");
				removeElement(Page);
				Element FieldOrderNumber = dlf.getChild("FieldOrderNumber");
				removeElement(FieldOrderNumber);
				Element ForceReview = dlf.getChild("ForceReview");
				removeElement(ForceReview);
				Element FieldValueChangeScript = dlf.getChild("FieldValueChangeScript");
				removeElement(FieldValueChangeScript);
				Element ConfidenceDLF = dlf.getChild("Confidence");
				removeElement(ConfidenceDLF);
				Element OcrConfidence = dlf.getChild("OcrConfidence");
				removeElement(OcrConfidence);
				Element OcrConfidenceThresholdDLF = dlf.getChild("OcrConfidenceThreshold");
				removeElement(OcrConfidenceThresholdDLF);
				Element ExtractionName = dlf.getChild("ExtractionName");
				removeElement(ExtractionName);
				Element AlternateValues = dlf.getChild("AlternateValues");
				removeElement(AlternateValues);
				Element Category = dlf.getChild("Category");
				removeElement(Category);
				Element ExtractionType = dlf.getChild("ExtractionType");
				removeElement(ExtractionType);
				Element FieldValueOptionList = dlf.getChild("FieldValueOptionList");
				removeElement(FieldValueOptionList);
			}

			Element Pages = doc.getChild("Pages");
			removeElement(Pages);
			Element DataTables = doc.getChild("DataTables");
			removeElement(DataTables);

		}
		Element ETextMode = docRoot.getChild("ETextMode");
		removeElement(ETextMode);
		Element DocumentClassificationTypes = docRoot.getChild("DocumentClassificationTypes");
		removeElement(DocumentClassificationTypes);
		Element SourceFiles = docRoot.getChild("SourceFiles");
		removeElement(SourceFiles);

	}

	private static void removeElement(Element remove){
		if(remove!=null) {
			remove.detach();
		}
	}
}