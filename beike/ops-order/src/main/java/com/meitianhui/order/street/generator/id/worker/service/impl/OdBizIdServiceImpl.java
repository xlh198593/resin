package com.meitianhui.order.street.generator.id.worker.service.impl;

import com.meitianhui.order.street.generator.id.worker.dao.OdBizIdDAO;
import com.meitianhui.order.street.generator.id.worker.entity.OdBizId;
import com.meitianhui.order.street.generator.id.worker.service.OdBizIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <pre> 数据库编号生成器业务操作实现类 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 11:13
 */
@Service
public class OdBizIdServiceImpl implements OdBizIdService {

    @Autowired
    private OdBizIdDAO odBizIdDAO;

    /**
     * 根据主键获取OdBizId实体，并更新max_id
     *
     * @param bizTag 业务标志
     * @return OdBizId实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OdBizId obtainOdBizId(String bizTag) {
        OdBizId odBizId = odBizIdDAO.selectByPrimaryKey(bizTag);
        if (odBizId == null) {
            odBizId = assembleOdBizId(bizTag);
        }

        odBizId.setMaxId(odBizId.getMaxId() + odBizId.getStep());
        odBizId.setUpdateTime(new Date());

        //修改编号
        odBizIdDAO.updateByPrimaryKeySelective(odBizId);
        return odBizId;
    }

    /**
     * 组装OdBizId实体
     *
     * @param bizTag 业务标志
     * @return OdBizId实体
     */
    private OdBizId assembleOdBizId(String bizTag) {
        return OdBizId.builder().bizTag(bizTag).maxId(0L).step(1000).desc(bizTag).build();
    }

}
