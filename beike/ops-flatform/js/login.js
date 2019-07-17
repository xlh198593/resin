layui.define(['jquery', 'url', 'layer', 'common'], function (exports) {
    var common = layui.common;
    var $ = layui.jquery;
    var layer = layui.layer;
    var requestUrl = layui.url;

    // 用户唯一id
    function guid() {
      function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
      }
      return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    }
    var ip = guid();
    common.setStorage('ip', ip);


    
    // 获得验证码
    function getCode () {
        var code_img = $(".code_img");
        $.ajax({
            url: requestUrl.loginPage.codeUrl,
            type: "POST",
            data: {
              client_id: ip
            },
            success: function (resData) {
               var veriCode = resData.veriCode;
               code_img.html(veriCode)
            }
        })
    }
    getCode();
    $(".code_img").click(function () {
        getCode();
    })


  $(".sub_btn").click(function () {
    // 登录

    var uname = $(".uname").val();
  	var pword = $(".pword").val();
  	var identifyingCode = $(".identifying_code").val();
  	var msg1 = $(".msg1");
  	var msg2 = $(".msg2");
  	var msg3 = $(".msg3");
    if (!uname) {
    	msg1.addClass('on');
    	return false;
    } else {
    	msg1.removeClass('on');
    }

    if (!pword) {
    	msg2.addClass('on');
    	return false;
    } else {
    	msg2.removeClass('on');
    }


    if (!identifyingCode) {
    	msg3.addClass('on');
    	return false;
    } else {
    	msg3.removeClass('on')
    }

    $.ajax({
            url:  requestUrl.loginPage.loginUrl,
            type: "POST",
            'Content-Type': 'application/json',
            data: {
               login_name: uname,
               login_password: pword,
               veriCode: identifyingCode,
               client_id: ip
            },
            success: function (resData) {
               var status = resData.status;
               var msg = resData.msg;
               if (status == 'succ') {
                    layer.msg('登陆成功');
                    common.setCookie('userInfo', JSON.stringify(resData))
                    setTimeout(function () {
                        window.location.href = requestUrl.host + '/index.html';
                    }, 1500)               
               } else {
                    layer.msg('登陆失败');
               }
            }
    })
  })
  exports('login', null); 
});