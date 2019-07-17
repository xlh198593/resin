$(function () {
	// 设置用户信息
	function setUserInfo () {
		var memberId  = window.location.href.split("memberId=")[1];
		var params = {"memberId": memberId};
		$.ajax({
			url: loginUrl,
			type: 'POST',
			data: {
               params: JSON.stringify(params),
               service: 'Company.Account.getLoginMemberInfo'
			},
			success: function (reData) {
               setLocalStorage('userInfo', reData);
			}
		})
	}
	setUserInfo()
})