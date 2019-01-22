package com.dd.server.event;

import com.dd.server.exceptions.SystemRequestValidationException;
import com.dd.server.request.Request;

public interface ISystemRequestHandler {
    boolean validate(Request paramRequest) throws SystemRequestValidationException;

    void execute(Request paramRequest) throws Exception;
}