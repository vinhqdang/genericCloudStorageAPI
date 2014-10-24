public enum SERVICE_TYPE
{
 DROPBOX (1),
 GOOGLE_DRIVE (2);
 private int value;
 private SERVICE_TYPE (int value) {
  this.value = value;
 }
 public int getValue () {
  return this.value;
 }
}
