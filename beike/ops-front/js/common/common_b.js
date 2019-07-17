layui.define(['layer', 'jquery'], function (exports) {
	var layer = layui.layer;
	var $ = layui.jquery;
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
		uniq: function (array){
			// 数组去重
		    var temp = []; //一个新的临时数组
		  
		    for(var i = 0; i < array.length; i++){
		        if(temp.indexOf(array[i]) == -1){
		            temp.push(array[i]);
		        }
		    }
		    return temp;
		},
		ajax: function (option, callback) {



			var getUserInfo = this.getCookie('userInfo');
		    var userInfo = null;
			var obj = {
				stores_id: '',
				oauth_token: '', 
				login_type: 2
			}

			if (getUserInfo) {
				userInfo = JSON.parse(getUserInfo);
				obj.stores_id = userInfo.list.stores_id;
				obj.oauth_token = userInfo.token.oauth_token;
			}
			var data = $.extend(obj, option.data);
			$.ajax({
				url: option.url,
				type: option.type,
				data: data,
				success: function (reData) {
                   callback(reData)
				}
			})
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
        setStorage: function (key, val) {
            if (typeof val == 'object') {
            	return window.localStorage.setItem(key, JSON.stringify(val));
            } else if (typeof val == 'string') {
            	return window.localStorage.setItem(key, val);
            }
        },
        getStorage: function (key, type) {
        	var val = window.localStorage.getItem(key);
        	if (type && type == 'string') {
        		return val;
        	} else {
        		val = JSON.parse(val);
        		return val;
        	}
        },
        showBigImg: function (obj) {
        	var $this = $(obj);
        	var imgSrc = $this.parents('.uploadimg_con').find('.layui-upload-img').attr('src');
        	var str = '<div style="width:100%;height:100%;overflow:hidden;">'+
        	             '<img src="'+ imgSrc +'" width="100%"/>'+
        	          '</div>'
            layer.open({
              title: '查看大图',
			  type: 1, 
			  content: str,
			  area: ['330px', '300px'],
			  shade:  [0.6, '#333'],
			  shadeClose : true,
			  anim: 3,
			  isOutAnim: true
			});
        }
    }

    exports("common/common_b", optionFn);
})