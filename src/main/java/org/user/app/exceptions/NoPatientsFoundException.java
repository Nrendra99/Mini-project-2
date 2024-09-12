package org.user.app.exceptions;

public class NoPatientsFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NoPatientsFoundException(String message) {
        super(message);
    }
}
