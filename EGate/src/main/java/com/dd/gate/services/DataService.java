package com.dd.gate.services;

import com.dd.edata.EData;
import com.dd.gate.exceptions.ServiceInitException;
import com.dd.gate.exceptions.ServiceStartException;
import com.dd.gate.exceptions.ServiceStopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataService extends AbstractService {
    private static Logger logger = LoggerFactory.getLogger(DataService.class);
    private EData edata;

    public DataService() {
        super(ServiceType.DATA);
    }

    @Override
    protected void initService() throws ServiceInitException {
        edata = EData.start(0, "com.dd.gate.model", getClass().getClassLoader(), getConfigProperties());
    }

    @Override
    protected void startService() throws ServiceStartException {
    }

    @Override
    protected void stopService() throws ServiceStopException {
        edata.shutdown();
    }

    public EData getEData() {
        return edata;
    }

    private Properties getConfigProperties() {
        try (InputStream is = new FileInputStream("config/global_db.properties")) {
            Properties configProperties = new Properties();
            configProperties.load(is);
            return configProperties;
        } catch (IOException e) {
            logger.error("[gate] read config properties error", e);
        }
        return null;
    }
}
