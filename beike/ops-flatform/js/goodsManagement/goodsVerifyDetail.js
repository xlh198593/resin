layui.define(['form', 'element', 'jquery', 'url', 'laytpl', 'common'], function (exports) {
    var form = layui.form;
    var $ = layui.jquery;
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
        var detailView = '';
        $.ajax({
            url: '../../views/goodsManagement/goodsVerifyDetail.html',
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
    


    // 商品审核通过
    $("#goodDetail_con").on('click', '.sub_btn1', function () {
        var statusStr = null;
        if (goodVerifyInfo.status == 'suspend') {
            statusStr = 'normal';
        } else {
            statusStr = 'on_shelf';
        }
        layer.open({
              type: 1, 
              content: '<div style="width:80%;margin:25px auto;text-align:center;">确定通过审核吗?</div>',
              area: ['250px', ''],
              btn: ['确定', '取消'],
              btnAlign: 'c',
              yes: function (index, layero) {
                 layer.close(index);
                 $.ajax({
                    url: requestUrl.goodsVerifyDetailPage.checkUrl,
                    type: 'POST',
                    'Content-Type': 'application/json',
                    data: {
                        token: token,
                        account_id: userId,
                        goods_id: goodVerifyInfo.goods_id,
                        status: statusStr,
                        remark: '1111'
                    },
                    success: function (resData) {
                         
                        if (resData.status == 'succ') {
                            layer.msg('审核成功');
                            setTimeout(function () {
                                $(".good_list_nav").trigger('click');
                            }, 1000)
                        } else {
                            layer.msg('审核失败');
                        }
                    }
                 })
              },
              cancel: function () {

              }
        });
    })
 
 
    // 商品审核拒绝
    $("#goodDetail_con").on('click', '.sub_btn2', function () {
        var msg = '请输入拒绝的详细原因，如某资料不符合要求，需重新修改/上传。该原因将通过短信以及小程序发送给该商户。';
        layer.open({
            title: '请输入拒绝原因',
            type: 1, 
            content: '<textarea placeholder="'+ msg +'" class="reject_msg" style="display:block;width:80%;height:100px;padding:10px;border:1px solid #e5e5e5;margin:25px auto 0;"></textarea>',
            area: ['330px', ''],
            btn: ['确定', '取消'],
            btnAlign: 'c',

            yes: function (index, layero) {
                layer.close(index);

                var rejectMsg = $(".reject_msg").val();
                $.ajax({
                    url: requestUrl.goodsVerifyDetailPage.checkUrl,
                    type: 'POST',
                    'Content-Type': 'application/json',
                    data: {
                        token: token,
                        account_id: userId,
                        goods_id: goodVerifyInfo.goods_id,
                        status: 'violation',
                        remark: rejectMsg
                    },
                    success: function (resData) {
                        if (resData.status == 'succ') {
                            layer.msg('审核已拒绝');
                            setTimeout(function () {
                                $(".good_list_nav").trigger('click');
                            }, 1000)
                        } else {
                            layer.msg('拒绝审核失败');
                        }
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

    exports('goodsManagement/goodsVerifyDetail', null);
})