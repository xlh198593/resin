package com.ande.buyb2c.advert.entity;

public class AdvertPositionDetail {
    private Integer advertPositionDetailId;

    private String image;

    private String link;

    private Integer advertPositionId;

    public Integer getAdvertPositionDetailId() {
        return advertPositionDetailId;
    }

    public void setAdvertPositionDetailId(Integer advertPositionDetailId) {
        this.advertPositionDetailId = advertPositionDetailId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    public Integer getAdvertPositionId() {
        return advertPositionId;
    }

    public void setAdvertPositionId(Integer advertPositionId) {
        this.advertPositionId = advertPositionId;
    }
}