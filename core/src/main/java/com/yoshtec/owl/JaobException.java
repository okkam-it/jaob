package com.yoshtec.owl;

/**
 * Base Exception for all Exceptions in the JAOB Framework.
 * 
 * @author Jonas von Malottki
 *
 */
public class JaobException extends Exception {

  private static final long serialVersionUID = -2410622468343669884L;

  public JaobException() {}

  public JaobException(String message) {
    super(message);
  }

  public JaobException(Throwable cause) {
    super(cause);
  }

  public JaobException(String message, Throwable cause) {
    super(message, cause);
  }

}
