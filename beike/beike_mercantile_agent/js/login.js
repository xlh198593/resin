(function () {
  // 获得验证码
	function getCode (obj) {
      var count = 60;
      var $this = $(obj);
      var iphone = $(".iphone").val();
      var set = setInterval(function () {
        if (count > 0) {
           $this.attr("disabled", "disabled");
           $this.val('还剩'+ count +'秒');
           count--;
        } else {
           clearInterval(set);
           $this.val('获取验证码');
           $this.removeAttr("disabled");
        }
      }, 1000);
      var params = {
        "login_name": iphone,
        "type_name": 'login'
      }
 	  	$.ajax({
 	  		url: shortMessageUrl,
 	  		type: 'POST',
 	  		data: {
          
          params: JSON.stringify(params),
          service: 'Company.Account.SMS'
        },
 	  		success: function (reData) {
            
 	  		}
 	  	})
 	}
  $(".code_btn").click(function () {
 	  getCode(this);
  })


  // function login (obj) {
  //    var iphone = $(".iphone").val();
  //    var code = $(".code").val();
  //    var params = {
  //       login_name: iphone,
  //       check_code: code 
  //    }

  //    $.ajax({
  //       url: loginUrl,
  //       type: 'POST',
  //       data: {
  //          params: JSON.stringify(params),
  //          service: 'Company.Account.Login'
  //       },
  //       success: function (reData) {
  //             if (reData.rsp_code == 'succ') {
  //                setLocalStorage('userInfo', reData)
  //                $.DialogByZ.Autofade({Content: "登陆成功"});
  //                // setTimeout(function () {
  //                //     window.location.href = "./index.html";
  //                // }, 2500);
  //             } else {
  //                $.DialogByZ.Autofade({Content: "登陆失败"});
  //             }   
  //       }
  //    })
  // }





  $(".iphone").focus(function () {
      clearVal(this);
  });


  $(".code").focus(function () {
      clearVal(this);
  })

  // 登录
  $(".sub_btn").click(function () {
     //login(this)
      var reg = /^1[34578]\d{9}$/;
      var iphone = $(".iphone");
      
      var iphoneVal = iphone.val();
      var code = $(".code");
      var codeVal = code.val();
      var params = {
          login_name: iphoneVal,
          check_code: codeVal 
      }
      
      if (!reg.test(iphoneVal)) {
          iphone.addClass("error");
          iphone.val('请输入有效手机号');
          return false;
      }

      if (!codeVal) {
          code.addClass("error");
          code.val('验证码不能空');
          return false;
      }

      $.ajax({
        url: loginUrl,
        type: 'POST',
        data: {
           params: JSON.stringify(params),
           service: 'Company.Account.Login'
        },
        success: function (reData) {
              if (reData.rsp_code == 'succ') {
                 setLocalStorage('userInfo', reData)
                 showMsg('登陆成功')
                 // setTimeout(function () {
                 //     window.location.href = "./index.html";
                 // }, 2500);
              } else {
                showMsg('登陆失败')
              }   
        }
      })
  })
})() 