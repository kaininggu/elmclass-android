package com.elmclass.elmclass.operation;

/**
 *
 * Created by kgu on 4/11/18.
 */

public class SignInResponseEvent extends BaseEvent{
    SignInResponseEvent(OperationError error) {
        super(error);
    }
}
