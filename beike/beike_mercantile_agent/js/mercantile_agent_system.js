$(function () {
	
	function onBridgeReady () {
	    var params = {
	        // 不删 memberId: userInfo.data.CompanyAccount.account_id
	        memberId: 'e0c25d25002b0ddbd9e3c5f3f9a6373a',
	        
	        totalFee: 10000
	    }
	    $.ajax({
	    	url: invitePay,
	    	type: 'POST',
	    	data: {
	    		URL: invitePay,
	    		service: 'finance.partnerPay',
	    		params: JSON.stringify(params)
	    	},
	    	success: function (reData) {
	    		var data = reData.data;
	    		var urlStr = data.redirect_url.split('prepay_id');
	    		console.log(urlStr[0])
	            window.location.href = urlStr
	    	}
	    })
	    
	}
	 



	$(".pay_btn").click(function () {
		onBridgeReady ()
	})
})