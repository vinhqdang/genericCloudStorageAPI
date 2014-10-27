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

public class DropboxAPI extends CloudAPI 
{
  public DropboxAPI (String settingFile) {
    this.readSetting (settingFile);
  }
  
  public  String requestAccessKey () throws IOException, DbxException {
    dBappInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

    dBconfig = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
    dBwebAuth = new DbxWebAuthNoRedirect(dBconfig, dBappInfo);

    // Have the user sign in and authorize your app.
    return dBwebAuth.start();
  }
  public void login (String accessKey) throws IOException, DbxException {
    DbxAuthFinish authFinish = dBwebAuth.finish(accessKey);
    String accessToken = authFinish.accessToken;
    dBclient = new DbxClient(dBconfig, accessToken);
  }
  public String uploadFile (String filePath, String title, String description, String mimeType) throws IOException, DbxException {
    java.io.File inputFile = new java.io.File(filePath);
    FileInputStream inputStream = new FileInputStream(inputFile);
    try {
     DbxEntry.File uploadedFile = dBclient.uploadFile(title,
      DbxWriteMode.add(), inputFile.length(), inputStream);
    } finally {
     inputStream.close();
     return filePath+"/"+title;
    }
  }
  
  /**
   * @return true downloaded file successfully
   * @return false downloaded file fail
   * */
  public boolean downloadFile (String filePath,String outputFileName) throws IOException, DbxException {
    FileOutputStream outputStream = new FileOutputStream(outputFileName);
    try {
     DbxEntry.File downloadedFile = dBclient.getFile(filePath, null,
      outputStream);
     return true;
    } 
    catch (DbxException dbe) {
      dbe.printStackTrace ();
      return false;
    }
    catch (IOException e) {
      e.printStackTrace ();
      return false;
    }
    finally {
     outputStream.close();
     //return true;
    }
  }
  
  public List<GenericFileFormat> getStorageInformation (String path) {
    List<GenericFileFormat> res = new ArrayList<GenericFileFormat>();
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
  }
}