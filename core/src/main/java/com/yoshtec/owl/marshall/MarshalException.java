package com.yoshtec.owl.marshall;

import com.yoshtec.owl.JaobException;

/**
 * Marshal exception is Thrown when the marshalling Process had unforseeable Errors.
 * 
 * @author Jonas von Malottki
 *
 */
public class MarshalException extends JaobException {

  private static final long serialVersionUID = -8098819500026803373L;

  public MarshalException() {}

  public MarshalException(String message) {
    super(message);
  }

  public MarshalException(Throwable cause) {
    super(cause);
  }

  public MarshalException(String message, Throwable cause) {
    super(message, cause);
  }

}
