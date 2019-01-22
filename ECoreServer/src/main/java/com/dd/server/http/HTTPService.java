package com.dd.server.http;

import com.dd.server.annotation.ServiceStart;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.dd.server.services.AbstractService;
import com.dd.server.services.ServiceType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ServiceStart
public class HTTPService extends AbstractService implements IHttpService {

    public HTTPService() {
        super(ServiceType.HTTP);
    }

    protected void initService() throws ServiceInitException {
    }


    protected void startService() throws ServiceStartException {
        SpringApplication.run(HTTPService.class);
    }

    protected void stopService() throws ServiceStopException {
    }
}