package com.ande.buyb2c.goods.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class Goods {
    private Integer goodsId;

    private Integer platformTypeId;

    private Integer platformTypeParentId;

    private Integer platformTypeGrandParentId;

    private Integer goodTypeId;

    private Integer goodTypeParentId;

    private Integer goodTypeGrandParentId;

    private String goodsName;
    private String pinyinGoodsName;

    private String goodsNo;

    private BigDecimal goodsPrice;

    private String goodsImageUrls;

    private Date createTime;

    private Date updateTime;

    private Integer adminId;

    private String delState;
private String saleState;
    private String goodsDetail;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date upSaleTime;
    
    private String mainImage;
    
    public String getPinyinGoodsName() {
		return pinyinGoodsName;
	}

	public void setPinyinGoodsName(String pinyinGoodsName) {
		this.pinyinGoodsName = pinyinGoodsName;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Date getUpSaleTime() {
		return upSaleTime;
	}

	public void setUpSaleTime(Date upSaleTime) {
		this.upSaleTime = upSaleTime;
	}

	private List<GoodsAttribute> goodsAttributeList;
public String getSaleState() {
		return saleState;
	}

	public void setSaleState(String saleState) {
		this.saleState = saleState;
	}

public List<GoodsAttribute> getGoodsAttributeList() {
		return goodsAttributeList;
	}

	public void setGoodsAttributeList(List<GoodsAttribute> goodsAttributeList) {
		this.goodsAttributeList = goodsAttributeList;
	}

	//冗余字段
    private Integer columnId;
    
    public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}

	public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getPlatformTypeId() {
        return platformTypeId;
    }

    public void setPlatformTypeId(Integer platformTypeId) {
        this.platformTypeId = platformTypeId;
    }

    public Integer getPlatformTypeParentId() {
        return platformTypeParentId;
    }

    public void setPlatformTypeParentId(Integer platformTypeParentId) {
        this.platformTypeParentId = platformTypeParentId;
    }

    public Integer getPlatformTypeGrandParentId() {
        return platformTypeGrandParentId;
    }

    public void setPlatformTypeGrandParentId(Integer platformTypeGrandParentId) {
        this.platformTypeGrandParentId = platformTypeGrandParentId;
    }

    public Integer getGoodTypeId() {
        return goodTypeId;
    }

    public void setGoodTypeId(Integer goodTypeId) {
        this.goodTypeId = goodTypeId;
    }

    public Integer getGoodTypeParentId() {
        return goodTypeParentId;
    }

    public void setGoodTypeParentId(Integer goodTypeParentId) {
        this.goodTypeParentId = goodTypeParentId;
    }

    public Integer getGoodTypeGrandParentId() {
        return goodTypeGrandParentId;
    }

    public void setGoodTypeGrandParentId(Integer goodTypeGrandParentId) {
        this.goodTypeGrandParentId = goodTypeGrandParentId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo == null ? null : goodsNo.trim();
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsImageUrls() {
        return goodsImageUrls;
    }

    public void setGoodsImageUrls(String goodsImageUrls) {
        this.goodsImageUrls = goodsImageUrls == null ? null : goodsImageUrls.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState == null ? null : delState.trim();
    }

    public String getGoodsDetail() {
        return goodsDetail;
    }

    public void setGoodsDetail(String goodsDetail) {
        this.goodsDetail = goodsDetail == null ? null : goodsDetail.trim();
    }
}