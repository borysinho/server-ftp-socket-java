public class ReceivedData {
  private String fileName = "";
  private byte[] buffer = null;
  private long fileLength = 0;

  ReceivedData() {
    this.fileName = "";
    this.buffer = null;
    this.fileLength = 0;

  }

  ReceivedData(String fileName, byte[] buffer, long fileLength) {
    this.fileName = fileName;
    this.buffer = buffer;
    this.fileLength = fileLength;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setBuffer(byte[] buffer) {
    this.buffer = buffer;
  }

  public byte[] getBuffer() {
    return this.buffer;
  }

  public void setFileLength(long fileLength) {
    this.fileLength = fileLength;
  }

  public long getFileLength() {
    return this.fileLength;
  }

}