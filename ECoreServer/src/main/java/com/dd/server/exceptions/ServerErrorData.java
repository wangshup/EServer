package com.dd.server.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ServerErrorData {
    ServerErrorCode code;
    List<String> params;

    public ServerErrorData(ServerErrorCode code) {
        this.code = code;
        this.params = new ArrayList<>();
    }

    public ServerErrorCode getCode() {
        return this.code;
    }

    public void setCode(ServerErrorCode code) {
        this.code = code;
    }

    public List<String> getParams() {
        return this.params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void addParameter(String parameter) {
        this.params.add(parameter);
    }
}
