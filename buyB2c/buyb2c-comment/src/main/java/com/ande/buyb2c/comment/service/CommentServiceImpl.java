package com.ande.buyb2c.comment.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.comment.dao.CommentMapper;
import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年2月1日上午9:05:03
 */
@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment> implements ICommentService{
@Resource
private CommentMapper commentMapper;
	@Override
	protected IBaseDao<Comment> getMapper() {
		return commentMapper;
	}
	@Override
	public List<Integer> getCommentCount(Integer goodsId) {
		return commentMapper.getCommentCount(goodsId);
	}
	@Override
	public Integer addBatch(List<Comment> list) {
		return commentMapper.addBatch(list);
	}

}
