package com.ande.buyb2c.content.service;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.content.entity.Content;

/**
 * @author chengzb
 * @date 2018年1月27日下午2:12:02
 */
public interface IContentService extends IBaseService<Content> {

	Content getConentByType(String type);

}
