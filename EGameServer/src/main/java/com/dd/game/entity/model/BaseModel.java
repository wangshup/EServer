package com.dd.game.entity.model;

import com.dd.edata.db.annotation.Column;

public abstract class BaseModel implements IModel{

    @Column(isNull = false)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
