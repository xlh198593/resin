package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdMemberAsset;
import org.apache.ibatis.annotations.Param;

/**
 * <pre> 会员资产数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdMemberAssetDAO {

    /**
     * 根据主键查询
     *
     * @param assetId 资产ID
     * @return 资产记录
     */
    FdMemberAsset selectByPrimaryKey(String assetId);

    /**
     * 根据参数查询
     *
     * @param memberTypeKey 会员分类，可选值：consumer（消费者/用户）、stores（门店/商家）、supplier（供应商/企业）、company（公司/运营）、assistant（店东助手）
     * @param memberId      会员ID
     * @return 资产记录
     */
    FdMemberAsset selectByMemberInfo(@Param("memberTypeKey") String memberTypeKey, @Param("memberId") String memberId);

    /**
     * 根据主键修改，金额为+传入的金额
     *
     * @param record 资产记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(FdMemberAsset record);

}