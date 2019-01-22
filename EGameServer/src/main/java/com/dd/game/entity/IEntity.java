package com.dd.game.entity;

import com.dd.game.entity.model.IModel;
import com.google.protobuf.Message;

public interface IEntity {
    int OP_NONE = 0;
    int OP_ADD = 1;
    int OP_UPDATE = 2;
    int OP_DEL = 3;
    int OP_LIST = 4;

    IModel getModel();

    void setModel(IModel model);

    void insert(boolean async);

    void update(boolean async);

    void delete(boolean async);

    default Message toProtoBuf(){
        return toProtoBuf(OP_NONE);
    }

    Message toProtoBuf(int op);
}
