/**
 * 错处理
 * @author Changefeng
 */
var logger = require('./log4js').getLogger('base-router');

module.exports = {

	/**
	 * 参数缺失
	 * res: 客户端响应
	 */
	incompleteParams : function(res) {
		logger.info('incomplete params');
		res.send({
			rsp_code: 'fail',
			error_code: 'incomplete params',
			error_msg: '参数缺失'
		});
		res.end();
	},

	/**
	 * 提现显示
	 * res: 客户端响应
	 */
	depstionshow : function(res) {
		res.send({
			rsp_code: 'succ',
			data:{
				isshow: 'Y',
				data_msg: '提现显示'
			}
			
		});
		res.end();
	},
	/**
	 * 提现隐藏
	 * res: 客户端响应
	 */
	depstionhidden : function(res) {
		res.send({
			rsp_code: 'succ',
			data:{
				isshow: 'N',
				data_msg: '提现隐藏'
			}
			
		});
		res.end();
	},


	/**
	 * 非法参数
	 * res: 客户端响应
	 */
	invalidParams : function(res) {
		logger.info('invalid params');
		res.send({
			rsp_code: 'succ',
			error_code: 'invalid params',
			error_msg: '非法参数'
		});
		res.end();
	},

	/**
	 * 非法签名
	 * res: 客户端响应
	 */
	invalidSign : function(res) {
		res.send({
			rsp_code: 'fail',
			error_code: 'invalid sign',
			error_msg: '非法签名'
		});
		res.end();
	},

	/**
	 * app_token错误
	 * res: 客户端响应
	 */
	appTokenError : function(res) {
		logger.info('invalid app_token');
		res.send({
			rsp_code: 'fail',
			error_code: 'invalid app_token',
			error_msg: '无效的app_token'
		});
		res.end();
	},

	/**
	 * user_token错误
	 * res: 客户端响应
	 */
	userTokenError : function(res) {
		logger.info('invalid user_token');
		res.send({
			rsp_code: 'fail',
			error_code: 'invalid user_token',
			error_msg: '无效的user_token'
		});
		res.end();
	},

	/**
	 * 系统错误
	 * res: 客户端响应
	 */
	systemError : function(res, error) {
		logger.error('SYSTEM ERROR:' + error);
		res.send({
			rsp_code: 'fail',
			error_code: 'system error',
			error_msg: '系统错误'
		});
		res.end();
	},

	renderErrorPage: function(res, error, errorMsg) {
		logger.error('RENDER ERROR PAGE:' + error);
		res.render('error-mobile', {
			message: errorMsg || '访问量激增，服务器君顶不住啦 : ('
		});
	},

	appNotFound : function(res) {
		res.send({
			rsp_code : 'fail',
			error_code : 'app_not_found',
			error_msg : '应用不存在'
		});
		res.end();
	}

}
