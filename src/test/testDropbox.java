package test;
//package org.inria.score.genericCloudStorageAPI;
import genericCloudStorageAPI.GenericFileFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import Dropbox.DropboxAPI;

import com.dropbox.core.DbxException;

public class testDropbox
{
 public static void main (String[] args) {
   //test Dropbox
   DropboxAPI cloudAPI = new DropboxAPI ("dropbox_setting.dat");
   
   try {
     String authorizeUrl = cloudAPI.requestAccessKey ();
     System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
     cloudAPI.login (code);
     cloudAPI.uploadFile ("dummy.txt","/dummy.txt","Nothing to describe", "plain/text");
     cloudAPI.downloadFile ("/dummy.txt","this is the dummy file I download from Dropbox.txt");
     List<GenericFileFormat> listFile = cloudAPI.getStorageInformation ("/");
     for (GenericFileFormat file : listFile) {
       System.out.println ("File name: " + file.getFileName ());
       System.out.println ("Details: " + file.getDescription ());
     }
   } catch (IOException e) {
     e.printStackTrace ();
   } catch (DbxException dbe) {
     dbe.printStackTrace ();
   }
   return;
 }
}