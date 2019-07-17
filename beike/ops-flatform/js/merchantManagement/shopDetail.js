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

    function loadVerifyInfro () {
    	var formView = '';
    	$.ajax({
    		url: '../../views/merchantManagement/shopDetail.html',
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
                    $(".shop_detail_con").html(formHtml);
                })
            }
    	})
    }
    
    loadVerifyInfro()
    

    // 启用店铺
    $(".shop_detail_con").on('click', '.verify_btn1', function () {
        var status = $(".shop_status").attr('status');
        var sysStatus = null;
        if (status == 'normal') {
           sysStatus = 'delete';
        } else if (status == 'delete') {
           sysStatus = 'normal'
        }
        layer.open({
          type: 1,
          title: '请确认是否恢复店铺？',
          content: '<div style="width:70%;margin:10px auto 0;">确认后，商户将恢复店铺的使用权限，同时具备使用小程序以及商户后台的权限。</div>',
          area: ['330px', ''],
          btn: ['确定', '取消'],
          yes: function (index, layero) {
              layer.close(index)
              $.ajax({
                url: requestUrl.shopDetailPage.stopShop,
                type: 'POST',
                'Content-Type': 'application/json',
                data: {
                    token: token,
                    account_id: userId,
                    stores_id: verifyShopInfo.stores_id,
                    sys_status: sysStatus,
                    remark: ''
                },
                success: function (reData) {
                    layer.msg('店铺已启用');
                    setTimeout(function () {
                        $(".shop_list_nav").find('span').trigger('click');
                    }, 1000)
                }
            })
          }
        });
    });


    // 停用店铺
    $(".shop_detail_con").on('click', '.verify_btn2', function () {
        var status = $(".shop_status").attr('status');
        var sysStatus = null;
        if (status == 'normal') {
           sysStatus = 'delete';
        } else if (status == 'delete') {
           sysStatus = 'normal'
        }
        // 审核拒绝
        var txt = '请输入停用的详细原因，如某店铺违规、商品违规等。原因将通过短信以及小程序发送给商户。';
        
        layer.open({
            type: 1,
            title: '请输入停用原因',
            content: '<textarea style="display:block;width:70%;height:100px;margin:25px auto 0;border:1px solid #e5e5e5;padding:10px;resize:none;" placeholder="'+ txt +'" class="stop_use"></textarea>',
            btn: ['确定', '取消'],
            btnAlign: 'c',
            area: ['330px', ''],
            yes: function (index, layero) {
                layer.close(index)
                var stopMsg = $(".stop_use").val();
                if (!stopMsg) {
                    layer.msg('请输入停用原因');
                    return ;
                }
                $.ajax({
                    url: requestUrl.shopDetailPage.stopShop,
                    type: 'POST',
                    'Content-Type': 'application/json',
                    data: {
                        token: token,
                        account_id: userId,
                        stores_id: verifyShopInfo.stores_id,
                        sys_status: sysStatus,
                        remark: stopMsg
                    },
                    success: function (reData) {
                        layer.msg('店铺已停用');
                        setTimeout(function () {
                            $(".shop_list_nav").find('span').trigger('click');
                        }, 1000)
                    }
                })
            },
            cancel: function () {
                console.log('bbbbb')                
            }
        });
    })

    // 查看大图
    $('.contai').on('click', '.show_big_img', function () {
        common.showBigImg(this);
    })

    
    exports('merchantManagement/shopDetail', null);
});