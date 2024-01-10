package cn.keking;

public class FileTypeNotSupportedException extends RuntimeException {

  public FileTypeNotSupportedException() {
  }

  public FileTypeNotSupportedException(String message) {
    super(message);
  }

  public FileTypeNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }

  public FileTypeNotSupportedException(Throwable cause) {
    super(cause);
  }

  public FileTypeNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
