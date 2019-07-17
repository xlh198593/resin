function setCookie(c_name,value,expiredays) {
	var exdate=new Date();
	exdate.setDate(exdate.getDate()+expiredays)
	document.cookie=c_name+ "=" +escape(value)+((expiredays==null) ? "" : ";expires="+exdate.toGMTString())
}

function getCookie(c_name) {
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
}

// 数组去重
function uniq(array){
    var temp = []; //一个新的临时数组
    for(var i = 0; i < array.length; i++){
        if(temp.indexOf(array[i]) == -1){
            temp.push(array[i]);
        }
    }
    return temp;
}

var userInfo = null;
var stores_id = null;
var oauth_token = null;
var getUserInfo = getCookie('userInfo');



var userName = null;
if (getUserInfo) {
	userInfo = JSON.parse(getCookie('userInfo'));
	stores_id = userInfo.list.stores_id;
	oauth_token = userInfo.token.oauth_token;
    userName = userInfo.list.stores_name;
}



// 测试路径
//var beikePaht = 'http://store.beeke.vip';
