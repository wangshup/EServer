package com.dd.gate.entities;

import com.dd.gate.model.BaseModel;

public interface IEntity {
    int OP_NONE = 0;
    int OP_UPDATE = 1;
    int OP_DEL = 2;
    int OP_LIST = 3;

    BaseModel getModel();

    void setModel(BaseModel model);

    void insert(boolean async);

    void update(boolean async);

    void delete(boolean async);
}
