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
//package org.inria.score.genericCloudStorageAPI;

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
public class CloudAPI{
 private SERVICE_TYPE serviceType;
 
 //for Dropbox
 private String APP_KEY;
 private String APP_SECRET;
 private DbxAppInfo dBappInfo;
 private DbxRequestConfig dBconfig;
 private DbxWebAuthNoRedirect dBwebAuth;
 private String dBaccessToken;
 private DbxClient dBclient;
 
 //for Google Drive
 private String CLIENT_ID;
 private String CLIENT_SECRET;
 private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
 private Drive gDservice;
 private GoogleAuthorizationCodeFlow gDflow;
 private HttpTransport gDhttpTransport;
 private JsonFactory gDjsonFactory;
 
 public CloudAPI (SERVICE_TYPE value) {
  this.serviceType = value;
 }
 
 public void setServiceType (SERVICE_TYPE value) {
  this.serviceType = value;
 }
 
 public void setAppKey (String appKey) {
  this.APP_KEY = appKey;
 }
 
 public void setAppSecret (String appSecret) {
  this.APP_SECRET = appSecret;
 }
 
 public void setDropboxAccessToken (String accessToken) {
  this.dBaccessToken = accessToken;
 }
 
 public void setClientID (String clientID) {
  this.CLIENT_ID = clientID;
 }
 
 public void setClientSecret (String clientSecret) {
  this.CLIENT_SECRET = clientSecret;
 }
 /**
  * requires authentication key from user
  * @return the URL user need to go to get the access key
  */
 public String requestAccessKey () throws IOException, DbxException {
  switch (serviceType) {
    case DROPBOX: {
    dBappInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

    dBconfig = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
    dBwebAuth = new DbxWebAuthNoRedirect(dBconfig, dBappInfo);

    // Have the user sign in and authorize your app.
    return dBwebAuth.start();
    }
   case GOOGLE_DRIVE:
    gDhttpTransport = new NetHttpTransport();
    gDjsonFactory = new JacksonFactory();
      
    gDflow = new GoogleAuthorizationCodeFlow.Builder(
     gDhttpTransport, gDjsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
     .setAccessType("online")
     .setApprovalPrompt("auto").build();
    
    return gDflow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
  }  
  return null;
 }
 
 
 public void login (String accessKey) throws IOException, DbxException {
  switch (serviceType) {
   case DROPBOX:
    DbxAuthFinish authFinish = dBwebAuth.finish(accessKey);
    String accessToken = authFinish.accessToken;
    dBclient = new DbxClient(dBconfig, accessToken);
    break;
   case GOOGLE_DRIVE:
    GoogleTokenResponse response = gDflow.newTokenRequest(accessKey).setRedirectUri(REDIRECT_URI).execute();
    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
    
    //Create a new authorized API client
    gDservice = new Drive.Builder(gDhttpTransport, gDjsonFactory, credential).build();
    break;
  }
 }
 
 public String uploadFile (String filePath, String title, String description, String mimeType) throws IOException, DbxException {
  switch (serviceType) {
   case DROPBOX:
    java.io.File inputFile = new java.io.File(filePath);
    FileInputStream inputStream = new FileInputStream(inputFile);
    try {
     DbxEntry.File uploadedFile = dBclient.uploadFile(title,
      DbxWriteMode.add(), inputFile.length(), inputStream);
    } finally {
     inputStream.close();
     return null;
    }
   case GOOGLE_DRIVE:
    File file = new File ();
    file.setTitle (title);
    file.setDescription (description);
    file.setMimeType (mimeType);
    java.io.File fileContent = new java.io.File(filePath);
    FileContent mediaContent = new FileContent(mimeType, fileContent);
    File uploadFile = gDservice.files().insert(file, mediaContent).execute();
    return uploadFile.getId ();
  }
  return null;
 }
 
 
/**
 * @param filePath: filename if Dropbox, file ID if Google Drive
 * @param outputFileName: the name of saved file
 * */
 public String downloadFile (String filePath,String outputFileName) throws IOException, DbxException {
  switch (serviceType) {
   case DROPBOX:
    FileOutputStream outputStream = new FileOutputStream(outputFileName);
    try {
     DbxEntry.File downloadedFile = dBclient.getFile(filePath, null,
      outputStream);
     return outputFileName;
    } finally {
     outputStream.close();
     return null;
    }
   case GOOGLE_DRIVE:
     File file = null;
     try {
      file = gDservice.files().get(filePath).execute();

      //System.out.println("Title: " + file.getTitle());
      //System.out.println("Description: " + file.getDescription());
      //System.out.println("MIME type: " + file.getMimeType());
    } catch (IOException e) {
      System.out.println("An error occured: " + e);
    }
    //download file
    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
      try {
        HttpResponse resp =
            gDservice.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                .execute();
        outputStream = new FileOutputStream(outputFileName);
 
        int read = 0;
        byte[] bytes = new byte[1024];
        
        while ((read = resp.getContent().read(bytes)) != -1) {
          outputStream.write(bytes, 0, read);
        }
      } catch (IOException e) {
        // An error occurred.
        e.printStackTrace();
        return null;
      } 
    } else {
      // The file doesn't have any content stored on Drive.
      return null;
    }
     break;
  }
  return null;
 } 

 public List<GenericFileFormat> getStorageInformation (String path) {
   List<GenericFileFormat> res = new ArrayList<GenericFileFormat>();
   switch (serviceType) {
     case DROPBOX:
       try {
       DbxEntry.WithChildren listing = dBclient.getMetadataWithChildren(path);
       //System.out.println("Files in the root path:");
       for (DbxEntry child : listing.children) {
         //System.out.println(" " + child.name + ": " + child.toString());
         GenericFileFormat genericFile = new GenericFileFormat ();
         genericFile.setFileName (child.name);
         //genericFile.setLastModifiedTime (child.getModifiedDate());
         genericFile.setDescription (child.toString ());
         res.add (genericFile);
       } 
     }catch (DbxException dbe) {
         dbe.printStackTrace ();
     }
       return res;
     case GOOGLE_DRIVE:
       Files.List request = null;
       List<File> result = null;
       try {
       result = new ArrayList<File>();
       request = gDservice.files().list();
     } catch (IOException e) {
       e.printStackTrace ();
     }
       
       do {
         try {
           FileList files = request.execute();
           
           result.addAll(files.getItems());
           request.setPageToken(files.getNextPageToken());
         } catch (IOException e) {
           System.out.println("An error occurred: " + e);
           request.setPageToken(null);
         }
       } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
     for (File file : result) {
       GenericFileFormat genericFile = new GenericFileFormat ();
       genericFile.setFileName (file.getTitle ());
       genericFile.setLastModifiedTime (file.getModifiedDate());
       genericFile.setMd5Checksum (file.getMd5Checksum());
       res.add (genericFile);
     }
       return res;
   }
   return null;
 }
}