package com.example.elmclass.operation;

/**
 * Created by kgu on 5/23/18.
 */

public class BaseEvent {
    private OperationError mError;

    BaseEvent(OperationError error) {
        mError = error;
    }

    public boolean hasError() { return mError != null; }
    public OperationError getError() { return mError; }
}
