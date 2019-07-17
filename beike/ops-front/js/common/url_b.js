layui.define(function (exports) {
	// 测试请求接口路径
	var requestUrl = 'http://test.beeke.vip';
 
	// //预发布请求接口路径
	// var requestUrl = 'http://pretest.meitianhui.com';


    var optionUrl = {
    	// 测试访问网址
    	host: 'http://store.beeke.vip',

    	// // 预发布访问网址
     //    host: 'https://prestore.meitianhui.com',
        merchandise_control: {
        	merchandise_warehouse_edit: {
        		detail: requestUrl + '/index.php?r=goods/look-goods',
                edit: requestUrl + '/index.php?r=goods/edit-goods'
        	}
        }
    }
 
    exports('common/url_b', optionUrl);
}) 