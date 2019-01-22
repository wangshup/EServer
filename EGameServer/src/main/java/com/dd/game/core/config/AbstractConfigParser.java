package com.dd.game.core.config;

import com.dd.game.exceptions.IllegalXMLConfigException;
import com.dd.game.utils.CommonUtils;
import com.dd.game.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractConfigParser<T> extends DefaultHandler implements IConfigParser<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigParser.class);
    protected T config;
    protected String xmlFile;
    private Object params = null;
    private String rootElement = Constants.XML_ELEMENT_NAME;
    private String resourceBaseDir;

    public AbstractConfigParser(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    private String getFile(String xmlFile) {
        if (xmlFile.startsWith(resourceBaseDir) || xmlFile.startsWith(File.separator)) {
            return xmlFile;
        }
        return resourceBaseDir + xmlFile;
    }

    protected abstract T createConfig();

    @Override
    public String getConfigFile() {
        return getFile(xmlFile);
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (isValidElement(qName)) {
            startElement(qName, new XmlAttributes(attributes), params);
        }
    }

    @Override
    public void setResourceBaseDir(String resourceBaseDir) {
        this.resourceBaseDir = resourceBaseDir;
        if (StringUtils.isNotBlank(this.resourceBaseDir) && !StringUtils.endsWith(this.resourceBaseDir, File.separator)) {
            this.resourceBaseDir = this.resourceBaseDir + File.separator;
        }
    }

    protected boolean isValidElement(String qName) {
        return StringUtils.equalsAnyIgnoreCase(qName, rootElement, Constants.XML_ELEMENT_NAME);
    }

    protected void onParseCompleted() {
    }

    abstract protected void startElement(String qName, XmlAttributes attributes, Object param);

    @Override
    public void validate() throws Exception {
    }

    @Override
    public void parse(Object param) {
        File file = new File(getFile(xmlFile));

        if (!file.exists()) {
            logger.error("config file {} not exists!!!!", file.getPath());
            return;
        }
        this.config = createConfig();
        this.params = param;
        try (InputStream inputStream = new FileInputStream(file)) {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(inputStream, this);
            onParseCompleted();
        } catch (FileNotFoundException e) {
            logger.error("config file {} not found", xmlFile, e);
            throw new IllegalXMLConfigException(xmlFile + "," + param);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            logger.error("parse xml file fail {}", xmlFile, e);
            if (e.getMessage() == null || !e.getMessage().equals("find")) {
                throw new IllegalXMLConfigException(xmlFile + "," + param);
            }
        }
    }

    public Object getValue(String name, Type type, XmlAttributes attributes) {
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return attributes.getIntValue(name);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return attributes.getLongValue(name);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return attributes.getDoubleValue(name, 0d);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return attributes.getFloatValue(name);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return attributes.getBooleanValue(name, false);
        } else if (type.equals(int[].class)) {
            return attributes.getIntegerArray(name, "\\|");
        } else if (type.equals(long[].class)) {
            return attributes.getLongArray(name, "\\|");
        } else if (type.equals(double[].class)) {
            return attributes.getDoubleArray(name, "\\|");
        } else if (type.equals(float[].class)) {
            return attributes.getFloatArray(name, "\\|");
        } else if (type.equals(boolean[].class)) {
            return attributes.getBooleanArray(name, "\\|");
        } else if (type.equals(String[].class)) {
            return attributes.getStringArray(name, "\\|");
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType().equals(Map.class)) {
                Type[] pTypes = pType.getActualTypeArguments();
                return CommonUtils.string2map(attributes.getValue(name), (Class<?>) pTypes[0], (Class<?>) pTypes[1]);
            }
        }

        return attributes.getValue(name);
    }
}
