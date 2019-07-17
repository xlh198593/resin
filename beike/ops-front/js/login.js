layui.use(['form', 'jquery', 'layer'], function(){
		    var form = layui.form;
		    var $ = layui.jquery;
            var layer = layui.layer;
		    // 输入框获得焦点删除报错
			function removeErr (obj) {
	    	   var msg = $(obj).parents(".input_con").siblings('.msg');
	           msg.removeClass('on');
	    	}
		
		    $(".phone_number").focus(function () {
	         	removeErr(this)
	        })

            $(".identifying_code").focus(function () {
	         	removeErr(this)
	         })


		    // 获取验证码
		    $(".identifying_code_btn").click(function () {
		    	 var time = 60;
		    	 var _this = $(this);
		    	 var phoneNumber = $(".phone_number").val();
		    	 var reg = /^1[345789]\d{9}$/;
		    	 if (!reg.test(phoneNumber)) {
                    var phone_msg = $(".phone_msg");
                    phone_msg.addClass("on").html('请输入有效手机号');
		            return false;
		    	 }
		    	 _this.attr('disabled',"disabled");
		    	 var set = setInterval(function () {
		    	 	if (time > 0) {
	                    time--;
	                    _this.val('还剩' + time + '秒');
		    	 	} else {
		    	 		clearInterval(set);
		    	 	    _this.removeAttr('disabled');
		    	 		_this.val('获取验证码');
		    	 	}
		    	 }, 1000)
			  	 $.ajax({
			  	 	url: identifyingCodeUrl,
			  	 	type: 'POST',
			  	 	data: {
			  	 		mobile: phoneNumber,
			  	 		msg_type: 1,
			  	 		login_type: 2
			  	 	},
			  	 	success: function (res) {
			  	 		var resData = JSON.parse(res)
                        layer.msg(resData.msg)
			  	 		if (resData.code == 0) {
 		                   
			  	 		}
			  	 	}
			  	 })
		    })
            



	     

		 
            // 登录 
			$(".sub_btn").click(function () {
			  	  	var phoneNumber = $(".phone_number").val();
			  	  	// 验证码
				  	var identifyingCode = $(".identifying_code").val();
				  	var phone_msg = $(".phone_msg");
				  	var identifyingCode_msg = $(".identifyingCode_msg");

		            var reg = /^1[345789]\d{9}$/;

				  	if (!reg.test(phoneNumber)) {
		               phone_msg.addClass("on").html('请输入有效手机号');
		               return false;
				  	} 
			        if (!identifyingCode) {
		               identifyingCode_msg.addClass("on").html('请输入验证码');
		               return false;
			        }
		
			        $.ajax({
			        	url: loginUrl,
			        	type: "POST",
			        	data: {
                            mobile: phoneNumber,
                            auth_code: identifyingCode,
                            login_type: 2
			        	},
			        	success: function (res) {
			        		var resData =  eval("(" + res + ")");
			        		var userData = JSON.stringify(resData.data);    
			        		var code = resData.code;
			        		if (code == 0) {
			        		    setCookie('userInfo', userData);
			        			window.location.href = localPath + '/main.html';
			        		}
			        		layer.msg(resData.msg);
			        	}
			        })
			  })
		});