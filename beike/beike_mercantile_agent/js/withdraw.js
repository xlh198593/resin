function confirmL () {
    console.log('aaaaaaaaa')
    $.DialogByZ.Close();
    $.DialogByZ.Alert({Title: "提示", Content: "您要求稍后开通",BtnL:"确定"})
}

function alerts () {
    $.DialogByZ.Close();
}
function Immediate () {
    console.log('bbbbbbbbbb')
    alert("取消");
}

$(".sub_btn").click(function () {
    $.DialogByZ.Alert({Title: "提现申请成功", Content: "系统会在3个工作日内处理<br/> <span style='font-size:12px;color:#999;'>（本月还可提现1次）</span>",BtnL:" <span style='color:#0E9F58;'>知道了</span>",FunL:alerts}) 
})