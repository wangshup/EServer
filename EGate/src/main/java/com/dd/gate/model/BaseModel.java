package com.dd.gate.model;

import com.dd.edata.db.annotation.Column;

public abstract class BaseModel {
    @Column(isNull = false)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
