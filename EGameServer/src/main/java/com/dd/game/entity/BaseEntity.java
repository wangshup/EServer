package com.dd.game.entity;

import com.dd.edata.EData;
import com.dd.game.core.GameEngine;
import com.dd.game.entity.model.IModel;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEntity implements IEntity {
    private static final Logger logger = LoggerFactory.getLogger(BaseEntity.class);
    protected static final EData edata = GameEngine.getEData();

    protected IModel model;

    public BaseEntity(IModel model) {
        this.model = model;
    }

    @Override
    public IModel getModel() {
        return model;
    }

    @Override
    public void setModel(IModel model) {
        this.model = model;
    }


    @Override
    public Message toProtoBuf() {
        return null;
    }

    @Override
    public void insert(boolean async) {
        if (async) {
            edata.insertAsync(getModel());
        } else {
            try {
                edata.insert(getModel());
            } catch (Exception e) {
                logger.error("insert {} error!!", this, e);
            }
        }
    }

    @Override
    public void update(boolean async) {
        if (async) {
            edata.updateAsync(getModel());
        } else {
            try {
                edata.update(getModel());
            } catch (Exception e) {
                logger.error("update {} error!!", this, e);
            }
        }
    }

    @Override
    public void delete(boolean async) {
        if (async) {
            edata.deleteAsync(getModel());
        } else {
            try {
                edata.delete(getModel());
            } catch (Exception e) {
                logger.error("delete {} error!!", this, e);
            }
        }
    }
}
