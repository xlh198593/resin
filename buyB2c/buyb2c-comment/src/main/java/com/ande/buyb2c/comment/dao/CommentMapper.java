package com.ande.buyb2c.comment.dao;

import java.util.List;

import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.common.util.IBaseDao;

public interface CommentMapper extends IBaseDao<Comment>{
	public List<Integer> getCommentCount(Integer goodsId);

	public Integer addBatch(List<Comment> list);
	
}