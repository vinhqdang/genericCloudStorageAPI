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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;
import java.util.List;

public class testGoogleDrive 
{
  public static void main (String[] args) {
    //test Google Drive
   CloudAPI cloudAPI = new CloudAPI (SERVICE_TYPE.GOOGLE_DRIVE);
   cloudAPI.setClientID ("YOUR ID");
   cloudAPI.setClientSecret ("YOUR SECRET");
   
   try {
     String authorizeUrl = cloudAPI.requestAccessKey ();
     System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
     cloudAPI.login (code);
     //String fileID = cloudAPI.uploadFile ("dummy.txt","TEST MY API","Nothing to describe", "plain/text");
     //cloudAPI.downloadFile (fileID,"this is the dummy file I download from Google Drive.txt");
     List<GenericFileFormat> listFile = cloudAPI.getStorageInformation ("");
     for (GenericFileFormat file : listFile) {
       System.out.println ("File name: " + file.getFileName ());
       System.out.println ("MD5: " + file.getMd5Checksum ());
       System.out.println ("Modified time: " + file.getLastModifiedTime ());
     }
   } catch (IOException e) {
     e.printStackTrace ();
   } catch (DbxException dbe) {
     dbe.printStackTrace ();
   }
   return;
  }
}