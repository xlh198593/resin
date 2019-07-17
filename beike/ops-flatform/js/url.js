layui.define(function (exports) {
	// 测试请求接口路径
	var requestPath = 'http://plm.meitianhui.com';
    var optionUrl = {
    	// 测试访问网址路径
    	host: 'http://platform.meitianhui.com',
        imgUrl: 'http://beeke-static.huigujia.cn',
    	loginPage: {
    		codeUrl: requestPath + '/index.php?r=login/index',
    		loginUrl: requestPath + '/index.php?r=login/log',
            loginOut: requestPath + '/index.php?r=login/out'
    	},
    	verifyListPage: {
    		verifyList: requestPath + '/index.php?r=store/index'
    	},
    	businessAuditPage: {
    		detail: requestPath + '/index.php?r=store/detail',
    		check: requestPath +  '/index.php?r=store/check'
    	},
        shopDetailPage: {
            stopShop: requestPath + '/index.php?r=store/stop'
        },
        goodsVerifyDetailPage: {
            checkUrl: requestPath + '/index.php?r=goods/check'
        },
    	goodsListPage: {
    		goodsList: requestPath + '/index.php?r=goods/index',
            goodDetail: requestPath + '/index.php?r=goods/detail'
    	},
        goodDetailPage: {
            // 上下架，删除
            goodChangstatus : requestPath + '/index.php?r=goods/changstatus'
        }
    }
    exports('url', optionUrl);
})