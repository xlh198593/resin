// 置空电话
$(".iphone").focus(function () {
    clearVal(this);
});

// 置空验证码
$(".code").focus(function () {
   clearVal(this);
})

// register
$(".sub_btn").click(function () {
	var reg = /^1[34578]\d{9}$/;
	var iphone = $(".iphone");
	var iphoneVal = iphone.val();
	var code = $(".code");
	var codeVal = code.val();
	var shadowBack = $(".shadow");
	var params = {
		mobile: iphoneVal,
		inviteCode: codeVal
	}
	if (!reg.test(iphoneVal)) {
       iphone.addClass("error");
       iphone.val("请输入有效手机号");
       return false;
	}
	if (!codeVal) {
       code.addClass("error");
       code.val('邀请码不能空');
       return false;
	}
	$.ajax({
		url: register,
		type: 'POST',
		data : {
            params: JSON.stringify(params),
            service: 'webChatPublic.operate.registerCpPartner'
		},
		success: function (reData) {
			if (reData.rsp_code == 'succ') {
                shadowBack.show();
			} else {
				showMsg('注册失败');
			}
		}
	})
});


// close   shadow
$(".shadow .text .close").click(function () {
	$(".shadow").hide();
})

