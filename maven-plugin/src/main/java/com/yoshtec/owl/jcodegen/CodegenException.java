package com.yoshtec.owl.jcodegen;

import com.yoshtec.owl.JaobException;

public class CodegenException extends JaobException {

    private static final long serialVersionUID = 3706412228298352363L;

    public CodegenException() {
    }

    public CodegenException(String message) {
        super(message);
    }

    public CodegenException(Throwable cause) {
        super(cause);
    }

    public CodegenException(String message, Throwable cause) {
        super(message, cause);
    }

}
