# Transact.Integration.Workato
A repository to integrate Ephesoft Transact to workato. This repository is to be used with the Ephesoft Workato connector.

## Architecture  
This solution ueses the [Workato custom connector](https://www.workato.com/custom_adapters/19399/details?community=true) to get information into Ephesoft Transact. To get information back into Workato from Transact, webhooks are used to trigger Workato recipies. To trigger the Workato webhooks the Ephesoft Custom Export plugin is used to configure the webhook payload with a json object. A export script is used to tigger the webhook and move the payload to Workato. Only the Transact extraction metadata is transported to Workato. If you you need to export images from Transact to Workato it is recommended to use a online file repsitory such as Box.com or Amazon S3. 

## Prerequisites
- Ephesoft Transact with a Ephesoft Webservice License (webserivice is need to work with the the workato custom connector)
- Ephesoft version that support the [Custom Export Plugin Product documentation](https://ephesoft.com/docs/products/transact/features-and-functions/administrator/moduleplugin-configuration/export-module/custom-export-plugin-configuration-and-user-guide/)
## Artifacts:
- ExportScript.java


## Installation
1) Move the ScriptExport.java and be sure to name the file **ScriptExport.java** file to the Scripts folder for your chosen batch class, at [Ephesoft_Directory]\Shared-Folders\<your batch class>\Scripts.

## Configuratrating the Custom Export plugin and Export Script.java
1)  Add the follwoing two plugins to the batch classes export module **CUSTOM_EXPORT_PLUGIN** and **EXPORT_SCRIPTING_PLUGIN**
> **Note:** the order of the plugns are important. The CUSTOM_EXPORT_PLUGIN needs exicute before the EXPORT_SCRIPTING_PLUGIN.
2) Configure the **CUSTOM_EXPORT_PLUGIN** for each document type you wish to export from the batch class to a Workato. This will be the JSON payload that will be submited to the Workato recipt. 
    - Use the information in the [Custom Export Plugin Product documentation](https://ephesoft.com/docs/products/transact/features-and-functions/administrator/moduleplugin-configuration/export-module/custom-export-plugin-configuration-and-user-guide/) to build your custom JSON object
    - **Important** you must use the following configuration for the **File Name** for the Export Metadata File.  **\~BI:Identifier\~-\~DOC:Identifier\~** The Export Script we use will use this naming convention. 
    - **Important** The **Folder Path** must match the configuration that you configure in the Export Script in the 3rd step. 
  ![customconnector Image](/screenshots/1.png)
  
3) The Workato recipt must be triggered with a webhook see the below screenshot for an example of the configuration. The Start Guided Setup makes it easy to capture the request from the Transact script that will be setup in the next step
![customconnector Image](/screenshots/2.png)  

4) There is configuration within the Export Script that will be coverd here. 

        //This is the path that the custom Export files will be exported to. This script will use the data populated in that folder
	   public static String CUSTOM_EXPORT_FILE_PATH = "C:\\Ephesoft\\SharedFolders\\final-drop-folder\\CustomExport\\";
> **Note:** The Folder path in the EXPORT_SCRIPT must match with the CUSTOM_EXPORT_PLUGIN Folder Path from setp 2
5) In this step we will add Ephesoft document types that should be mapped to any webhooks that are needed to be trigered

        	DOCTYPE_WEBHOOK_MAPPING.put("Statement", "https://www.workato.com/webhooks/rest/xxxxxxx-xxxxx-xxxx-xxxxxx/mywebhook-Doc1");
			DOCTYPE_WEBHOOK_MAPPING.put("w2", "https://www.workato.com/webhooks/rest/xxxxxxx-xxxxx-xxxx-xxxxxx/mywebhook-Doc2");
>**Note:** copy each line above if you need to support more then one document type.

# License
Ephesoft Labs is licensed under the Ephesoft Source Code License. 