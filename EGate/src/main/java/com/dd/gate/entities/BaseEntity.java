package com.dd.gate.entities;

import com.dd.edata.EData;
import com.dd.gate.GateServer;
import com.dd.gate.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEntity implements IEntity {
    private static final Logger logger = LoggerFactory.getLogger(BaseEntity.class);
    protected final EData edata = GateServer.getInstance().getDataService().getEData();

    protected BaseModel model;

    public BaseEntity(BaseModel model) {
        this.model = model;
    }

    @Override
    public BaseModel getModel() {
        return model;
    }

    @Override
    public void setModel(BaseModel model) {
        this.model = model;
    }

    @Override
    public void insert(boolean async) {
        if (async) {
            edata.insertAsync(getModel());
        } else {
            try {
                edata.insert(getModel());
            } catch (Exception e) {
                logger.error("[gate] insert {} error!!", this, e);
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
                logger.error("[gate] update {} error!!", this, e);
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
                logger.error("[gate] delete {} error!!", this, e);
            }
        }
    }
}
