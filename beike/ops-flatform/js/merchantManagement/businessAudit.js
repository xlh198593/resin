layui.define(['jquery', 'layer', 'element', 'common', 'url', 'laytpl'], function (exports) {
    var $ = layui.jquery;
    var layer = layui.layer;
    var element = layui.element;
    var common = layui.common;
    var requestUrl = layui.url;
    var laytpl = layui.laytpl;
    var userInfo = JSON.parse(common.getCookie('userInfo'));
	var token = userInfo.token;
	var userId = userInfo.account_id;
	// 哪个商铺
    var verifyShopInfo = common.getStorage('verifyShop');

    console.log(verifyShopInfo)
    function loadVerifyInfro () {
    	var formView = '';
    	$.ajax({
    		url: '../../views/merchantManagement/businessAudit.html',
    		type: 'get',
    		success: function (resHtml) {
               formView = resHtml;
    		}
    	})
    	$.ajax({
    		url: requestUrl.businessAuditPage.detail,
    		type: 'POST',
    		'Content-Type': 'application/json',
            data: {
            	token: token,
            	account_id: userId,
            	stores_id: verifyShopInfo.stores_id
            },
            success: function (resData) {
                laytpl(formView).render({dataInfro: resData.datas, imgUrl: requestUrl.imgUrl, shopInfo: verifyShopInfo}, function (formHtml) {
                      $(".businessAudit_con").html(formHtml);
                })
            }
    	})
    }

    loadVerifyInfro();
  
    $(document).on('click', '.verify_btn1', function () {
    	var status = $(this).data('status');
        // 审核通过
	    layer.open({
		    type: 1,
		    title: '请再次确认',
		    shade: [0.7, '#333'],
		    shadeClose: true,
		    content: '<div style="width:60%;margin:25px auto 0;">确认后，该商户将自动创建一个店铺，同时具备使用小程序以及商户后台的权限。</div>',
		    btn: ['确定', '取消'],
		    btnAlign: 'c',
		    yes: function (index, layero) {
	            layer.close(index);
	            $.ajax({
	            	url: requestUrl.businessAuditPage.check,
	            	type: 'POST',
	            	'Content-Type': 'application/json',
	            	data: {
		            	token: token,
		            	account_id: userId,
		            	stores_id: verifyShopInfo.stores_id,
		            	audit_status: 'pass'
		            },
		            success: function (resData) {
		            	if (resData.status == 'succ') {
		            		layer.msg('审核已通过');
		            		setTimeout(function () {
                                $(".verify_list_nav").trigger('click');
                            }, 1000)
		            	}  else {
                            layer.msg('审核失败');
		            	}
		            }
	            })
		    },
		    cancel: function (index, layero) {
		    	
		    }
		});
    });
    

    $(document).on('click', '.verify_btn2', function () {
    	var status = $(this).data('status');
       	// 审核拒绝
    	var txt = '请输入拒绝的详细原因，如某资料不符合要求，需重新修改/上传。该原因将通过短信以及小程序发送给该商户。';
	    layer.open({
		    type: 1,
		    title: '请输入拒绝原因',
		    content: '<textarea style="display:block;width:70%;height:100px;margin:25px auto 0;border:1px solid #e5e5e5;padding:10px;resize:none;" placeholder="'+ txt +'" class="remark"></textarea>',
		    btn: ['确定', '取消'],
		    btnAlign: 'c',
		    area: ['330px', ''],
		    yes: function (index, layero) {
		    	layer.close(index);
		    	var remark = $.trim($(".remark").val());
		    	if (!remark) {
                   layer.msg('请输入拒绝原因');
                   return ;
		    	}
	            $.ajax({
	            	url: requestUrl.businessAuditPage.check,
	            	type: 'POST',
	            	'Content-Type': 'application/json',
	            	data: {
		            	token: token,
		            	account_id: userId,
		            	stores_id: verifyShopInfo.stores_id,
		            	audit_status: 'reject',
		            	remark: remark
		            },
		            success: function (resData) {
                        if (resData.status == 'succ') {
		            		layer.msg('审核已拒绝');
		            		setTimeout(function () {
                                $(".verify_list_nav").trigger('click');
                            }, 1000)
		            	}  else {
                            layer.msg('审核拒绝失败');
		            	}  
		            }
	            });
		    },
		    cancel: function () {
                	    	
		    }
		});
    });
 
    // 查看大图
    $('.contai').on('click', '.show_big_img', function () {
    	common.showBigImg(this);
    })
    exports('merchantManagement/businessAudit', {});
});