package com.ande.buyb2c.comment.service;

import java.util.List;

import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年2月1日上午9:04:11
 */
public interface ICommentService extends IBaseService<Comment>{
	public List<Integer> getCommentCount(Integer goodsId);
	public Integer addBatch(List<Comment> list);
}
