package org.user.app.exceptions;

public class NoAvailableAppointmentsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NoAvailableAppointmentsException(String message) {
        super(message);
    }
}