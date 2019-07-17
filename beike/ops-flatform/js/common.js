layui.define(['layer','jquery'], function (exports) {
  var layer = layui.layer;
  var $ = layui.jquery;
  var demoAdmin = $("#demoAdmin");
  var optionFn = {
      setCookie: function (c_name,value,expiredays) {
          var exdate=new Date()
          exdate.setDate(exdate.getDate()+expiredays)
          document.cookie=c_name+ "=" +escape(value)+
          ((expiredays==null) ? "" : ";expires="+exdate.toGMTString())
      },
      getCookie: function (c_name) {
          if (document.cookie.length>0) {
            c_start=document.cookie.indexOf(c_name + "=")
            if (c_start!=-1) { 
                c_start=c_start + c_name.length+1 
                c_end=document.cookie.indexOf(";",c_start)
                if (c_end==-1) c_end=document.cookie.length
                return unescape(document.cookie.substring(c_start,c_end))
              } 
            }
          return ""
      },
      



      delCookie: function (name) { 
          var exp = new Date(); 
          exp.setTime(exp.getTime() - 1); 
          var cval = optionFn.getCookie(name); 
          if(cval != null) 
              document.cookie= name + "="+cval+";expires="+exp.toGMTString(); 
      },
      setStorage: function (key, val) {
          return window.localStorage.setItem(key, val);
      },


      getStorage: function (key, str) {
          if (str == 'string') {
              return window.localStorage.getItem(key)
          } else {
              return JSON.parse(window.localStorage.getItem(key));
          }
           
      },
      ajax: function (option, callback) {


          var userInfo = JSON.parse(this.getCookie('userInfo'));
          var token = userInfo.token;
          var userId = userInfo.account_id;
          var obj = {
              token: token,
              account_id: userId,
          }
          var objData = $.extend(obj, option.data);
          $.ajax({
            url: option.url,
            type: option.type,
            data: objData,
            'Content-Type': 'application/json',
            success: function (reData) {
                callback(reData);
            }
          })
      },
      showBigImg: function (obj) {
          // 查看大图
          var $this =$(obj);
          var srcImg = $this.find('img').attr('src');
          var strHtml =   '<div style="width:330px;height:330px;overflow:hidden;">'+
                              '<img src="'+ srcImg +'" width="100%"/>'+
                          '</div>';
          layer.open({
              title: false,
          type: 1, 
          content: strHtml,
          area: ['330px', '330px'],
          closeBtn: 1,
          shade:  [0.7, '#000'],
          shadeClose: true,
          anim: 1
        });
      },
      clearOldData: function () {
          $.ajax({
             url:'www.haorooms.com',
             dataType:'json',
             data:{},
             beforeSend :function(xmlHttp){ 
                xmlHttp.setRequestHeader("If-Modified-Since","0"); 
                xmlHttp.setRequestHeader("Cache-Control","no-cache");
             },
             success:function(response){
                 //do something
             },
             async:false
          })
      },
      demoAdmin: demoAdmin
  }
   
  $(".date_select").find('li').click(function () {
     var $this = $(this);
     $this.siblings("li").removeClass('on');
     $this.addClass('on');
  })
  exports("common", optionFn)
})