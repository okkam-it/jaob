package com.yoshtec.owl.marshall;

import com.yoshtec.owl.JaobException;

/**
 * Represents Errors that can occur during unmarshaling
 * 
 * @author Jonas von Malottki
 * @see com.yoshtec.owl.marshall.UnMarshaller
 */
public class UnmarshalException extends JaobException {

	private static final long serialVersionUID = 8386389953011734357L;

	public UnmarshalException() {
		super();
	}

	public UnmarshalException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnmarshalException(String message) {
		super(message);
	}

	public UnmarshalException(Throwable cause) {
		super(cause);
	}

}
