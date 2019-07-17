// 弹窗
function confirmL(){
        $.DialogByZ.Close();
        $.DialogByZ.Alert({Title: "提示", Content: '请输入', BtnL:"确定"})
    }
function alerts(){

  $.DialogByZ.Close();
}

function Immediate(){
     alert("取消");
}

// 获得验证码
function getCode (obj) {
	var $this = $(obj);
	var time = 60;
	$this.attr('disabled', 'disabled');
	var set = setInterval(function () {
		if (time > 0) {
			$this.val('还剩'+ time +'秒');
            time--;
		} else {
			time = 0;
			clearInterval(set);
			$this.removeAttr("disabled");
			$this.val('获取验证码');
		}
	}, 1000);
} 






// setLocalStorage
function setLocalStorage (key, data, type) {
    if (type == 'string') {
    	return window.localStorage.setItem(key, data);
    } else {
    	return window.localStorage.setItem(key, JSON.stringify(data));
    }
}

// getLocalStorage
function getLocalStorage (key, type) {
    if (type == 'string') {
    	return window.localStorage.getItem(key);
    } else {
    	return JSON.parse(window.localStorage.getItem(key));
    }
}

// 删除input框值
function clearVal (obj) {
    var $this = $(obj);
    $this.val('');
    $this.removeClass("error");
}

// 回调后msg
function showMsg (msg) {
    var shadow_msg = $(".shadow_msg");
    shadow_msg.html(msg);
    shadow_msg.fadeIn(500);
    setTimeout(function () {
        shadow_msg.fadeOut(500);
    }, 2500)
}