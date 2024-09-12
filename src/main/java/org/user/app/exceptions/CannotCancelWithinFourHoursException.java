package org.user.app.exceptions;

public class CannotCancelWithinFourHoursException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public CannotCancelWithinFourHoursException(String message) {
        super(message);
    }
}
