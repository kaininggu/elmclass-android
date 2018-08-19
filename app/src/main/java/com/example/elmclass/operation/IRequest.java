package com.example.elmclass.operation;

import java.util.Map;

/**
 * Created by kgu on 5/21/18.
 */

public interface IRequest {
    static final String[] REQUEST_METHOD_TO_STRING = {"GET","POST","PUT","DELETE","HEAD","OPTIONS","TRACE","PATCH"};

    int getMethod();
    String getEndpoint();
    Map<String, String> getParams();
}
