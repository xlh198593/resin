var express = require('express');
const app = express();
var router = express.Router();
var url = require('url');
var request = require('request');

var appConfig = require('../../app-config');
var errHandler = require('../../lib/err-handler');
var bizUtils = require('../../lib/biz-utils');

router.all('/', function(req, res, next) {
     res.render("c/h5share/index");
});

router.all('/getimgs', function(req, res, next) {
	try {
		var requestParams = bizUtils.extend(req.query, req.body);
		var formData = {
			app_token:requestParams.app_token,
			service:"order.consumer.fgShareListFind",
			params:JSON.stringify({})
		};
		request.post(appConfig.opsorderPath, {form: formData}, function(error, response, body) {
			try {
				if(!error && response.statusCode == 200) {
					var body = JSON.parse(body);
					if (body.rsp_code == "succ") {
						var shareList = body.data.list;
						for(var i = 0; i < shareList.length; i++) {
							for(var y = 0; y < shareList[i].pic_info.length; y++) {
								shareList[i].pic_info[y].share_name = shareList[i].share_name;
							}
						}
						res.send(shareList)
						
					} else {
						errHandler.renderErrorPage(res, error);
					}
					
				
				} else {
					errHandler.renderErrorPage(res, error);
				}
			} catch(e) {
				errHandler.renderErrorPage(res, e);
			}
		});
	

    } catch(e) {
		errHandler.systemError(res, e);
		return;
	}

});





module.exports = router;
