layui.define(['form', 'element', 'jquery', 'url', 'laytpl', 'common', 'layer'], function (exports) {
    var form = layui.form;
    var $ = layui.jquery;
    var layer = layui.layer;
    var requestUrl = layui.url;
    var laytpl = layui.laytpl;
    var common = layui.common;
	var userInfo = JSON.parse(common.getCookie('userInfo'));
	var token = userInfo.token;
	var userId = userInfo.account_id;
 

    // 哪个商铺
    var goodVerifyInfo = common.getStorage('goodVerify');
 

    // 加载详情数据
    function getDoodDetail () {
    	var goodId = window.localStorage.getItem('goodVerifyId');
    	var detailView = '';
    	$.ajax({
    		url: '../../views/goodsManagement/goodDetail.html',
    		type: 'get',
    		success: function (resView) {
                detailView = resView;
    		}
    	})
    	$.ajax({
    		url: requestUrl.goodsListPage.goodDetail,
    		type: 'POST',
            data: {
	            token: token,
    			account_id: userId,
    			goods_id: goodVerifyInfo.goods_id
            },
            success: function (resData) {
            	var data = resData.datas;
                laytpl(detailView).render({goodDetailData: data, imgUrl: requestUrl.imgUrl, goodVerifyInfo: goodVerifyInfo}, function (detailHtml) {
                	$("#goodDetail_con").html(detailHtml);
                })
            }
    	})
    }
   

    getDoodDetail()
    
    
    $("#goodDetail_con").on('click', '.sub_btn1', function () {
        var status = $(".status_val").val();
        var statusStr = null;
        var statusChange = null;
        if (status == '已上架') {
            statusStr = '下架';
            statusChange = 'off_shelf';
        } else {
            statusStr = '上架';
            statusChange = 'on_shelf';
        }
        layer.open({
              type: 1, 
              content: '<div style="width:80%;margin:25px auto;text-align:center;">确定'+ statusStr +'吗?</div>',
              area: ['250px', ''],
              btn: ['确定', '取消'],
              btnAlign: 'c',
              yes: function (index, layero) {
                 layer.close(index);
                 $.ajax({
                    url: requestUrl.goodDetailPage.goodChangstatus,
                    type: 'POST',

                    data: {
                        token: token,
                        account_id: userId,
                        goods_id: goodVerifyInfo.goods_id,
                        status: statusChange
                    },
                    success: function (reData) {
                        if (reData.status == 'succ') {
                            layer.msg(statusStr + '成功')
                        } else {
                            layer.msg(statusStr + '失败');
                        }
                        setTimeout(function () {
                            $(".good_list_nav").trigger('click');
                        }, 1000)
                    }
                 })
              },
              cancel: function () {

              }

        });
    })

    $("#goodDetail_con").on('click', '.sub_btn2', function () {
        var msg = '请输入删除的详细原因，如某资料不符合要求，需重新修改/上传。原因将通过短信以及小程序发送给商户。';
        layer.open({
            title: '请输入删除原因',
            type: 1, 
            content: '<textarea placeholder="'+ msg +'" style="display:block;width:80%;height:100px;padding:10px;border:1px solid #e5e5e5;margin:25px auto 0;" class="delete_msg"></textarea>',
            
            area: ['330px', ''],
            btn: ['确定', '取消'],
            btnAlign: 'c',
            yes: function (index, layero) {
                layer.close(index);
                var deleteMsg = $(".delete_msg").val();
                $.ajax({
                    url: requestUrl.goodDetailPage.goodChangstatus,
                    type: 'POST',

                    data: {
                        token: token,
                        account_id: userId,
                        goods_id: goodVerifyInfo.goods_id,
                        status: 'delete',
                        remark: deleteMsg
                    },
                    success: function (reData) {
                        if (reData.status == 'succ') {
                
                            layer.msg('删除成功')
                        } else {
                            layer.msg('删除失败');
                        }
                        setTimeout(function () {
                            $(".good_list_nav").trigger('click');
                        }, 1000)
                    }
                 })
            },
            cancel: function () {
 
            }
        });
    })

    // 查看大图
    $('.contai').on('click', '.show_big_img', function () {
        common.showBigImg(this);
    })

    exports('goodsManagement/goodDetail', null);
})