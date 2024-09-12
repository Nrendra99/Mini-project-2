package org.user.app.exceptions;

public class CannotRescheduleWithinFourHoursException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public CannotRescheduleWithinFourHoursException(String message) {
        super(message);
    }
}