package Dropbox;
import genericCloudStorageAPI.CloudAPI;
import genericCloudStorageAPI.GenericFileFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

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
		//DbxAuthFinish authFinish = dBwebAuth.finish(accessKey);
		//String accessToken = authFinish.accessToken;
		dBclient = new DbxClient(dBconfig, "cS4Yoa7-2QIAAAAAAAAM3N1aaxYt3B866zb41HmyeNwXP9bpBZCM3OmutFOICfRD");
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