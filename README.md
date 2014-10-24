#Generic APIs to access cloud storage services:

Currently, the following actions are supported:
- Upload a file
- Download a file
- Get files and directories metadata

The following services are supported:
- Dropbox
- Google Drive

##How to use
The APIs are provided in CloudAPI.java.
You need to import all jar files in /libs directory

##How to test
### For Dropbox:
1. Register Dropbox account
2. Go to Dropbox App Console to create a new project: https://www.dropbox.com/developers/apps
3. After creating a new project, you can get APP_KEY and APP_SECRET value
4. Compile and run testDropbox.java file

### For Google Drive:
1. Register Google account
2. Go to Google Developer Console: https://console.developers.google.com/project
3. Creat a new project if needed
4. On the left side, select APISs (under APIs & auth), and make sure that the status of Google Drive API is on
5. On the left side, select Credentials, then under OAuth click to "Create New Client ID". Follow the instruction on the screen. 
6. Replacing the CLIENT_ID and CLIENT_SECRET by the keys Google provided.
7. Compile and run testGoogleDrive.java