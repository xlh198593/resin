$(function () {
	function loadUserInfo () {
		// e0c25d25002b0ddbd9e3c5f3f9a6373a
		var userInfo = getLocalStorage("userInfo");
		var data = userInfo.data.CompanyAccount;
		var userImgSrc = data.profile_pic;
		var accountName = data.account_name;
		var in_money = data.in_money;
		// 余额
		var money = data.money;
		// 是否绑定支付宝
		var bank_account = data.bank_account;
		if (bank_account) {
            $(".bank_account").html("（已绑定）");
		} else {
			$(".bank_account").html("（未绑定）");
		}
        $(".use_money").html(money);
        $(".in_money").html(in_money);
	}
	loadUserInfo()
})