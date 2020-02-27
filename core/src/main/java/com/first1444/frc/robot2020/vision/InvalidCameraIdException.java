package com.first1444.frc.robot2020.vision;

public class InvalidCameraIdException extends Exception {
    public InvalidCameraIdException() {
    }

    public InvalidCameraIdException(String message) {
        super(message);
    }

    public InvalidCameraIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCameraIdException(Throwable cause) {
        super(cause);
    }

    public InvalidCameraIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
