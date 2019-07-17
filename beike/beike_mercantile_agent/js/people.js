$(function () {
	// 加载用户信息
	function loadUserInfo () {
		// e0c25d25002b0ddbd9e3c5f3f9a6373a
		var userInfo = getLocalStorage("userInfo");
		var data = userInfo.data.CompanyAccount;
		var userImgSrc = data.profile_pic;
		var accountName = data.account_name;
		var iphone = data.login_name;
		var money = data.money;
		if (userImgSrc) {
           $(".user_img").attr("src", userImgSrc)
		}
        if (accountName) {
           $(".user2").html(accountName)
        } else {
           $(".user2").html('未填');
        }
        $(".phone_number").html(iphone);
        $(".money").html(money);
	}
	loadUserInfo()
})