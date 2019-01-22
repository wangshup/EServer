package com.dd.game.core.config;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

public class XmlAttributes {
    private Attributes xmlAttributes;

    public XmlAttributes(Attributes xmlAttributes) {
        this.xmlAttributes = xmlAttributes;
    }

    public String[] getStringArray(String qName) {
        return getStringArray(qName, ",");
    }

    public String[] getStringArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        return StringUtils.split(val, separatorChars);
    }

    public int[] getIntegerArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        String[] array = StringUtils.split(val, separatorChars);
        int[] rs = new int[array.length];
        for (int i = 0; i < rs.length; i++)
            rs[i] = Integer.parseInt(array[i]);
        return rs;
    }

    public Integer getIntValue(String qName, Integer defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Integer.parseInt(val);
    }

    public Short getShortValue(String qName) {
        return getShortValue(qName, null);
    }

    public Short getShortValue(String qName, Short defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Short.parseShort(val);
    }

    public Integer getIntValue(String qName) {
        return getIntValue(qName, null);
    }

    public String getStringValue(String qName) {
        return xmlAttributes.getValue(qName);
    }

    public Long getLongValue(String qName) {
        return getLongValue(qName, null);
    }

    public Long getLongValue(String qName, Long defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Long.parseLong(val);
    }

    public long[] getLongArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        String[] array = StringUtils.split(val, separatorChars);
        long[] rs = new long[array.length];
        for (int i = 0; i < rs.length; i++)
            rs[i] = Long.parseLong(array[i]);
        return rs;
    }

    public Double getDoubleValue(String qName, Double defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Double.parseDouble(val);
    }

    public double[] getDoubleArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        String[] array = StringUtils.split(val, separatorChars);
        double[] rs = new double[array.length];
        for (int i = 0; i < rs.length; i++)
            rs[i] = Double.parseDouble(array[i]);
        return rs;
    }

    public Float getFloatValue(String qName) {
        return getFloatValue(qName, null);
    }

    public Float getFloatValue(String qName, Float defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Float.parseFloat(val);
    }

    public float[] getFloatArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        String[] array = StringUtils.split(val, separatorChars);
        float[] rs = new float[array.length];
        for (int i = 0; i < rs.length; i++)
            rs[i] = Float.parseFloat(array[i]);
        return rs;
    }

    public Boolean getBooleanValue(String qName, Boolean defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(val);
    }

    public boolean[] getBooleanArray(String qName, String separatorChars) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        String[] array = StringUtils.split(val, separatorChars);
        boolean[] rs = new boolean[array.length];
        for (int i = 0; i < rs.length; i++)
            rs[i] = Boolean.parseBoolean(array[i]);
        return rs;
    }

    public Byte getByteValue(String qName, Byte defaultValue) {
        String val = xmlAttributes.getValue(qName);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }
        return Byte.parseByte(val);
    }

    public int getLength() {
        return xmlAttributes.getLength();
    }

    public String getLocalName(int index) {
        return xmlAttributes.getLocalName(index);
    }

    public String getQName(int index) {
        return xmlAttributes.getQName(index);
    }

    public String getValue(int index) {
        return xmlAttributes.getValue(index);
    }

    public int getIndex(String qName) {
        return xmlAttributes.getIndex(qName);
    }

    public String getValue(String qName) {
        return xmlAttributes.getValue(qName);
    }

    public boolean contains(String qName) {
        return xmlAttributes.getIndex(qName) >= 0;
    }
}
