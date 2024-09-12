package org.user.app.exceptions;

public class NoAvailableDoctorsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NoAvailableDoctorsException(String message) {
        super(message);
    }
}
