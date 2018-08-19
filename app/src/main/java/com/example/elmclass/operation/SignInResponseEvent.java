package com.example.elmclass.operation;

/**
 * Created by kgu on 4/11/18.
 */

public class SignInResponseEvent extends BaseEvent{
    public SignInResponseEvent(OperationError error) {
        super(error);
    }
}
