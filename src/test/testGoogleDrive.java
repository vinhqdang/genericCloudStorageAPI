package test;
import genericCloudStorageAPI.GenericFileFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import GoogleDrive.GoogleDriveAPI;

import com.dropbox.core.DbxException;

public class testGoogleDrive 
{
	public static void main (String[] args) {
		//test Google Drive
		GoogleDriveAPI cloudAPI = new GoogleDriveAPI ("google_drive_setting.dat");   

		try {
			String authorizeUrl = cloudAPI.requestAccessKey ();
			System.out.println("1. Go to: " + authorizeUrl);
			System.out.println("2. Click \"Allow\" (you might have to log in first)");
			System.out.println("3. Copy the authorization code.");
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			cloudAPI.login (code);
			
			//TODO: after login with code at the first time, you can get access token and store it into the setting file
			
			//login again but without code
			cloudAPI.login ();
			String fileID = cloudAPI.uploadFile ("dummy.txt","TEST MY API","Nothing to describe", "plain/text");
			cloudAPI.downloadFile (fileID,"this is the dummy file I download from Google Drive.txt");
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