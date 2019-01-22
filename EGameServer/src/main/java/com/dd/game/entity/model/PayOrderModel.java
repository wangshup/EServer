package com.dd.game.entity.model;

import com.dd.edata.db.annotation.*;

import java.util.Calendar;
import java.util.Date;

@Table(name = "payment_order")
@TablePrimaryKey(members = { "id" })
@TableIndices({ @TableIndex(name = "idx_playerid", members = { "playerId" }) })
public class PayOrderModel {
    @Column(type = "char", len = 36, isNull = false)
    private String id;

    @Column(type = "varchar", len = 36)
    private String itemId;

    @Column(type = "varchar", len = 36)
    private String productId;

    @Column
    private long playerId;

    @Column
    private int type;

    @Column
    private double price;

    @Column
    private Date payTime;

    @Column(type = "char", len = 24)
    private String ip;

    @Column(type = "varchar", len = 36)
    private String gaid;

    @Column(type = "char", len = 10)
    private String country;

    @Column(type = "varchar", len = 36)
    private String platform;

    @Column(type = "varchar", len = 128)
    private String deviceId;

    public PayOrderModel(long playerId, String orderId, String itemId, String productId, double price, int type,
                         String ip, String gaid, String country, String platform, String deviceId) {
        this.playerId = playerId;
        this.id = orderId;
        this.itemId = itemId;
        this.productId = productId;
        this.type = type;
        this.price = price;
        this.ip = ip;
        this.gaid = gaid;
        this.country = country;
        this.platform = platform;
        this.deviceId = deviceId;
        this.payTime = Calendar.getInstance().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGaid() {
        return gaid;
    }

    public void setGaid(String gaid) {
        this.gaid = gaid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PaymentOrder [id=").append(id).append(", itemId=").append(itemId).append(", productId=")
                .append(productId).append(", playerId=").append(playerId).append(", type=").append(type)
                .append(", price=").append(price).append(", payTime=").append(payTime).append(", ip=").append(ip)
                .append(", gaid=").append(gaid).append(", country=").append(country).append(", platform=")
                .append(platform).append(", deviceId=").append(deviceId).append("]");
        return builder.toString();
    }
}
