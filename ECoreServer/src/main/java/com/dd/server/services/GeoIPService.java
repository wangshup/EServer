package com.dd.server.services;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.server.Server;
import com.dd.server.annotation.ServiceStart;
import com.dd.server.exceptions.ServiceInitException;
import com.dd.server.exceptions.ServiceStartException;
import com.dd.server.exceptions.ServiceStopException;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

@ServiceStart
public class GeoIPService extends AbstractService {
    private static Logger logger = LoggerFactory.getLogger(GeoIPService.class);
    private DatabaseReader reader = null;
    private String filePath;
    private long databaseFileModifyTime = -1;

    public GeoIPService() {
        super(ServiceType.GEOIP);
    }

    private void init(String filePath) {
        this.filePath = filePath;
        try {
            File file = new File(filePath);
            reader = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
            if (!file.exists()) {
                logger.error("ip2country error: file {} not exists", filePath);
            } else {
                databaseFileModifyTime = file.lastModified();
            }
        } catch (Exception e) {
            logger.error("load ip2country error", e);
            throw new IllegalArgumentException("geoIp file " + filePath + " invalid");
        }
    }

    @SuppressWarnings("deprecation")
    public void reloadCountryCode() {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("ip2country error: file {} not exists", filePath);
            return;
        }

        if (databaseFileModifyTime >= file.lastModified()) {
            return;
        }

        databaseFileModifyTime = file.lastModified();
        DatabaseReader oldReader = null;
        try {
            DatabaseReader newReader = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
            oldReader = reader;
            reader = newReader;
        } catch (Exception e) {
            logger.error("ip2country error: reload file {}", filePath, e);
        } finally {
            IOUtils.closeQuietly(oldReader);
        }
    }

    public String getCountryCode(String ip) {
        if ("127.0.0.1".equals(ip)) {
            return "cn";
        }
        String countryCode = null;
        if (reader != null) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                CountryResponse response = reader.country(ipAddress);
                Country country = response.getCountry();
                countryCode = country.getIsoCode();
            } catch (Exception e) {
                logger.error("GeoIP DatabaseReader not recognized ip:{}", ip);
            }
        } else {
            logger.info("GeoIP DatabaseReader is null");
        }
        return countryCode;
    }

    @Override
    protected void initService() throws ServiceInitException {
        String geoFile = Server.getInstance().getConfiguration().getString("geoip.db.file",
                "config/GeoLite2-Country.mmdb");
        this.init(geoFile);
    }

    @Override
    protected void startService() throws ServiceStartException {
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void stopService() throws ServiceStopException {
        IOUtils.closeQuietly(reader);
    }
}
