$(".code_btn").click(function () {
	// 支付宝账号
	var pay_number = $("#pay_number");
	var payNumberVal = pay_number.val();
	var uname = $("#uname");
	var unameVal = uname.val();
	var iphone = $("#phone");
	var iphoneVal = iphone.val();

    // 手机号
	var reg = /^1[34578]\d{9}$/;
	if (!reg.test(payNumberVal)) {
        pay_number.val('请输入支付宝账号');
        pay_number.addClass("error");
        return false;
	}

	if (!unameVal) {
		uname.val("请输入与手机号对应的姓名");
		uname.addClass("error");
        return false;
	}

	if (!reg.test(iphoneVal)) {
		iphone.val("请输入手机号码");
		iphone.addClass("error");
        return false;
	}

	// 获得验证码
	getCode(this);

	var params = {
        "login_name": iphoneVal,
        "type_name": 'login'
    }
 	  	
 	$.ajax({
 	  		url: shortMessageUrl,
 	  		type: 'POST',
 	  		data: {
	            params: JSON.stringify(params),
	            service: 'Company.Account.SMS'
	        },
 	  		success: function (reData) {
 	  			if (reData.rsp_code == 'succ') {
                    showMsg('发送成功');
 	  			} else {
 	  				showMsg('发送失败');
 	  			}
                
 	  		}
 	})
});

// 输入框获得焦点的时候置空
$(".input_val").focus(function () {
    clearVal(this);
});

$(".sub_btn").click(function(){
	var paramsMap = {
		member_id: 'e0c25d25002b0ddbd9e3c5f3f9a6373a',
		mobile: '15220214256',
		member_type_key: 'co-partner',
		proposer: '阿毛',
		bank_code: '支付宝',
		bank_account: '13658034195'

	}
	$.ajax({
		url: 'https://t-appportal.meitianhui.com/openapi/finance',
		type: 'POST',
		data: {
			Service: 'finance.consumer.addFdMemberBankInfo',
			paramsMap: JSON.stringify(paramsMap)
		}
	})
   //$.DialogByZ.Confirm({Title: "绑定成功", Content: " 恭喜您，绑定成功。是否立即提现？",FunL:confirmL,FunR:Immediate})
})