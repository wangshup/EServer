#!/usr/bin/python
# -*- coding:UTF-8 -*-


def getParseCode(packageName):
    parseCode = "package " + packageName + ".parser;\r" \
                + r'''
import com.dd.game.core.config.AbstractConfigParser;
import com.dd.game.core.config.XmlAttributes;
import com.dd.game.core.config.entities.@unameConfig;
import com.dd.game.core.config.entities.@unameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class @unameParser extends AbstractConfigParser<@unameConfig> {

    private static final Logger logger = LoggerFactory.getLogger(@unameParser.class);

    public @unameParser() {
        super("@lname.xml");
    }

    @Override
    protected @unameConfig createConfig() {
        return new @unameConfig();
    }

    @Override
    protected void onParseCompleted() {
        super.onParseCompleted();
    }

    @Override
    protected void startElement(String qName, XmlAttributes attributes, Object param) {
        try {
            @unameEntity entity = new @unameEntity();
            for (int i = 0; i < attributes.getLength(); ++i) {
                String fieldName = attributes.getQName(i);
                try {
                    Field f = @unameEntity.class.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(entity, getValue(fieldName, f.getGenericType(), attributes));
                } catch (NoSuchFieldException nfe) {
                    logger.warn("parse {} , not found field with name {}", xmlFile, fieldName);
                }
            }
            this.config.put(entity);
        } catch (Exception e) {
            logger.error("parse element error!", e);
        }
    }
}
                '''
    return parseCode


def getConfigCode(packageName):
    code = "package " + packageName + ".entities;\r" \
           + '''
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class @unameConfig {
   
    private Map<Integer, @unameEntity> map = new LinkedHashMap<>();
    
    public void put(@unameEntity entity) {
        map.put(entity.getId(), entity);
    }
    
    public @unameEntity get(int id) {
        return map.get(id);
    }
    
    public Collection<@unameEntity> getAll@unames() {
        return Collections.unmodifiableCollection(map.values());
    }

    public Map<Integer, @unameEntity> getMap() {
        return Collections.unmodifiableMap(map);
    }
}
           '''

    return code


if __name__ == "__main__":
    print(getConfigCode("com.dd.server"))
