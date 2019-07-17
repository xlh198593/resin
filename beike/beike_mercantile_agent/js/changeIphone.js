$(".code_btn").click(function () {
	// 获得验证码
	getCode(this);
});


$(".sub_btn").click(function(){
   $.DialogByZ.Confirm({Title: "绑定成功", Content: " 恭喜您，绑定成功。是否立即提现？",FunL:confirmL,FunR:Immediate})
})