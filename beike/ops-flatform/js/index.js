layui.define(['element', 'jquery', 'common', 'url'], function(exports) {
  var element = layui.element;
  var common = layui.common;
  var $ = layui.jquery;
  var requestUrl = layui.url;
  var userInfoStr = common.getCookie('userInfo');
  var userInfo ;
  if (userInfoStr) {

     userInfo = JSON.parse(userInfoStr);
  } else {
    // 没有登录就跳回登录页
      window.location.href = requestUrl.host + '/login.html';
  }
  var token = userInfo.token;
  var userId = userInfo.account_id;
  
  
 
   
  
   
 

  $(".menu").find('a').click(function () {
  	 var $this = $(this);
  	 var url = $this.data('url');
     console.log(url)
  	 var demoAdmin = $("#demoAdmin");
  	 demoAdmin.attr('src', url);
  })

  $(".login_out").click(function () {
     var ip =  common.getStorage('ip', 'string');
      console.log(userInfo)
      console.log(ip)
     $.ajax({
        url: requestUrl.loginPage.loginOut,
        type: 'POST',

        data: {
            token: token,
            account_id: userId,
            client_id: ip
        },
        success: function (reData) {

            common.delCookie('userInfo');
            window.location.href = requestUrl.host + '/login.html';
        }
     })
  })
  exports('index', {})
  
});