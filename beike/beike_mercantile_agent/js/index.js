(function () {
	// 加载统计数据
    function loadTotal () {
      var userInfo = getLocalStorage('userInfo');
   	  var params = {
   	  	  memberId: userInfo.data.CompanyAccount.account_id,
   	  	  roleName: 'subcorp'
   	  }
   	  $.ajax({
   	  	 url: totalData,
   	  	 type: 'POST',
   	  	 data: {
   	  	 	params: JSON.stringify(params),
   	  	 	service: 'webChatPublic.operate.findPersonProfitInfo'
   	  	 },
   	  	 success: function (reData) {
            var data = reData.data;
            // 今日订单分润
            var today_count = $(".today_count");
            var todayCount = today_count.find(".count");
            var oldCount1 = today_count.find(".old_count");
            todayCount.html(data.currentProfit);
            oldCount1.html(data.yesterdayProfit);
            
            // 累计订单分润
            var total_count = $(".total_count");
            var totalCount = total_count.find(".count");
            
            totalCount.html(data.totalProfit);

            // 今日新增商户
            var today_user = $(".today_user");
            var todayUserCount = today_user.find(".count");
            var oldCount2 = today_user.find(".old_count");
            todayUserCount.html(data.currentStoresNum);   
            oldCount2.html(data.yesterdayStoresNum);

            // 累计商户
            var total_user = $(".total_user");
            var totalUser = total_user.find(".count");
            totalUser.html(data.totalStoresNum);
   	  	 }
   	  })
    }


    loadTotal();
})();