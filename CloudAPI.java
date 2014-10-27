/**
 * The class provided accessing APIs to some common cloud storage
 * Version: 1.0
 * Date: 24 - Oct - 2014
 * Author: Vinh Q. Dang
 * Email: quang-vinh.dang@inria.fr
 * Supporting services: 
 ** Dropbox
 ** Google Drive
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.GenericUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;

import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;
import java.util.List;

/**
 * provide access API
 */
public abstract class CloudAPI{
 //private SERVICE_TYPE serviceType;
 
 //for both services
  protected String APP_KEY; // CLIENT_ID in Google Drive case
 protected String APP_SECRET; // CLIENT_SECRET in Google Drive case
 protected String ACCESS_TOKEN;
 
 //for Dropbox
 protected DbxAppInfo dBappInfo;
 protected DbxRequestConfig dBconfig;
 protected DbxWebAuthNoRedirect dBwebAuth;
 //private String dBaccessToken;
 protected DbxClient dBclient;
 
 //for Google Drive
 //private String CLIENT_ID;
 //private String CLIENT_SECRET;
 protected static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
 protected Drive gDservice;
 protected GoogleAuthorizationCodeFlow gDflow;
 protected HttpTransport gDhttpTransport;
 protected JsonFactory gDjsonFactory;
 
 protected void readSetting (String settingFile) {
   BufferedReader reader = null;
   try {
     reader = new BufferedReader(new FileReader(settingFile));
     String line = null;
     try {
       while ((line = reader.readLine()) != null) {
         String[] tokens = line.split ("=");
         if (tokens[0].equals ("KEY")) APP_KEY = tokens[1];
         if (tokens[0].equals ("SECRET")) APP_SECRET = tokens[1];
       }
     } catch (IOException e) {
       System.out.println ("Error while reading setting file");
       return;
     }
   }
   catch (FileNotFoundException e) {
     System.out.println ("settings.dat not found");
     return;
   }
   
   if (APP_SECRET == null || APP_KEY == null) {
     System.out.println ("There is no key or secret setting");
     return;
   }
 }
 
 public void setDropboxAccessToken (String accessToken) {
  this.ACCESS_TOKEN = accessToken;
 }

 /**
  * requires authentication key from user
  * @return the URL user need to go to get the access key
  */
 public abstract String requestAccessKey () throws IOException, DbxException ;
  
 
 public abstract void login (String accessKey) throws IOException, DbxException;

 
 public abstract String uploadFile (String filePath, String title, String description, String mimeType) throws IOException, DbxException; 
 
 
/**
 * @param filePath: filename if Dropbox, file ID if Google Drive
 * @param outputFileName: the name of saved file
 * */
 public abstract boolean downloadFile (String filePath,String outputFileName) throws IOException, DbxException;
 
 public abstract List<GenericFileFormat> getStorageInformation (String path);
 }