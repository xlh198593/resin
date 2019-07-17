$(function () {
   function loadCopartnerDetail () {
   	   // 不删 var userInfo = getLocalStorage('userInfo');
   	   var agentItem = getLocalStorage("agentItem");
         var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            accountId: 'acee59d9076083ddf1a80741106a0b54',
            
            coPartnerMemberId: agentItem.memberId
         }
   	    $.ajax({
   	  	    url: copartnerDetail,
   	  	    type: 'POST',
   	  	    data: {
               params: JSON.stringify(params),
               service: 'webChatPublic.operate.findCoPartnerDetailInfo'
   	  	    },          
   	  	    success: function (reData) {
               var data = reData.data;
               var time = new Date(data.registerdDate).toLocaleDateString();
               $(".profilePic").attr('src', data.profilePic);
               $(".accountName").html(data.accountName);
               $(".mobile").html(data.mobile);
               $(".address").html(data.address);


               $(".registerdDate").html(time);
               $(".recommendType").html(data.recommendType)
               $(".recomStoresNum").html(data.recomStoresNum);
               $(".recomPartnerNum").html(data.recomPartnerNum);
 
               // $(".contactTel").html(data.contactTel);
               // $(".storeStatus").html(data.storeStatus);
               // $(".profit").html(data.profit);
               // $(".recommendType").html(data.recommendType);
               // if (data.recommendType == '自推') {
               // 	  $(".reommendName_li").hide();
               // }
               // $(".reommendName").html(data.reommendName);
   	  	    }
   	  })
   }

   loadCopartnerDetail()
});