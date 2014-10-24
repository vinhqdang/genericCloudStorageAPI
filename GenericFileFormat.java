import com.google.api.client.util.DateTime;

public class GenericFileFormat 
{
  private String fileName;
  private String md5Checksum;
  private DateTime lastModifiedTime;
  private long version;
  private String description;
  
  public void setFileName (String fileName) {
    this.fileName = fileName;
  }
  
  public void setMd5Checksum (String md5) {
    this.md5Checksum = md5;
  }
  
  public void setLastModifiedTime (DateTime time) {
    this.lastModifiedTime = time;
  }
  
  public void setVersion (long version) { this.version = version;}
  
  public void setDescription (String description) {this.description = description;}
  
  public String getFileName () { return fileName;}
  public String getMd5Checksum () { return md5Checksum;}
  public DateTime getLastModifiedTime () {return lastModifiedTime;}
  public long getVersion () {return version;}
  public String getDescription () {return description;}
}