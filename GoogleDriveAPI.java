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

public class GoogleDriveAPI extends CloudAPI 
{
  public GoogleDriveAPI (String settingFile) {
    this.readSetting (settingFile);
  }
  
  public  String requestAccessKey () throws IOException, DbxException {
    gDhttpTransport = new NetHttpTransport();
    gDjsonFactory = new JacksonFactory();
      
    gDflow = new GoogleAuthorizationCodeFlow.Builder(
     gDhttpTransport, gDjsonFactory, APP_KEY, APP_SECRET, Arrays.asList(DriveScopes.DRIVE))
     .setAccessType("online")
     .setApprovalPrompt("auto").build();
    
    return gDflow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
  }
  public void login (String accessKey) throws IOException, DbxException {
    GoogleTokenResponse response = gDflow.newTokenRequest(accessKey).setRedirectUri(REDIRECT_URI).execute();
    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
    
    //Create a new authorized API client
    gDservice = new Drive.Builder(gDhttpTransport, gDjsonFactory, credential).build();
  }
  public String uploadFile (String filePath, String title, String description, String mimeType) throws IOException, DbxException {
    File file = new File ();
    file.setTitle (title);
    file.setDescription (description);
    file.setMimeType (mimeType);
    java.io.File fileContent = new java.io.File(filePath);
    FileContent mediaContent = new FileContent(mimeType, fileContent);
    File uploadFile = gDservice.files().insert(file, mediaContent).execute();
    return uploadFile.getId ();
  }
  
  public boolean downloadFile (String filePath,String outputFileName) throws IOException, DbxException {
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
        FileOutputStream outputStream = new FileOutputStream(outputFileName);
 
        int read = 0;
        byte[] bytes = new byte[1024];
        
        while ((read = resp.getContent().read(bytes)) != -1) {
          outputStream.write(bytes, 0, read);
        }
      } catch (IOException e) {
        // An error occurred.
        e.printStackTrace();
        return false;
      } 
    } else {
      // The file doesn't have any content stored on Drive.
      return false;
    }
    return true;
  }
  public List<GenericFileFormat> getStorageInformation (String path) {
    List<GenericFileFormat> res = new ArrayList<GenericFileFormat>();
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
}
