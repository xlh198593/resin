$(function () {
   function loadAgenDetail () {
   	   // 不删 var userInfo = getLocalStorage('userInfo');
   	   var agentItem = getLocalStorage("agentItem");
         var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            memberId: 'e0c25d25002b0ddbd9e3c5f3f9a6373a',
            
            storesId: agentItem.storesId
         }
   	   $.ajax({
   	  	    url: agentDetail,
   	  	    type: 'POST',
   	  	    data: {
               params: JSON.stringify(params),
               service: 'webChatPublic.operate.findStoreDetailInfo'
   	  	    },
   	  	    success: function (reData) {
               var data = reData.data;
               var time =  new Date(data.createdDate).toLocaleDateString();
               $(".headPicPath").attr('src', data.headPicPath);
               $(".storesName").html(data.storesName);
               $(".category").html(data.category);
               $(".address").html(data.address);
               $(".contactPerson").html(data.contactPerson);


               $(".contactTel").html(data.contactTel);
               $(".storeStatus").html(data.storeStatus);
               $(".createdDate").html(time);
               $(".profit").html(data.profit);
               $(".recommendType").html(data.recommendType);
               if (data.recommendType == '自推') {
               	  $(".reommendName_li").hide();
               }
               $(".reommendName").html(data.reommendName);
   	  	    }
   	  })
   }

   loadAgenDetail()
});