layui.use(['form', 'jquery', 'laydate', 'element', 'laypage', 'laytpl'], function(){
    var laypage = layui.laypage;
    var $ = layui.jquery;
    var laydate = layui.laydate;
    var laytpl = layui.laytpl;
    // 数据总条数
    var goodCount = null;
    laydate.render({
        elem: '#test16',
        type: 'datetime',
        range: '到',
        format: 'yyyy年M月d日H时m分s秒'
    });

 

    $(".choose_con .select_date").find('li').click(function () {
            var $this = $(this);
            $this.siblings('li').removeClass('on');
            $this.addClass("on");
            // loadTable()
    });

    // 加载table下tr配置数据
    function loadTable (data) {
        var tbodyTab = $(".merchandise_warehouse_table").find("tbody");
        $.ajax({
            url: '../../module/merchandise_control/merchandise_sell.html',
            type: 'get',
            data: '',
            success: function (html) {
                  laytpl(html).render(data , function(str){
                     tbodyTab.html(str)
                  });
            }
        })
    }

  
    // 加载商品list
    function loadGoodList (page) {
         $.ajax({
            url: goodsListUrl,
            type: "POST",
            data: {
                stores_id: stores_id,
                oauth_token: oauth_token,
                login_type: 2,
                pageIndex: page || 0,
                pageSize: 5,
                is_recommend: '',
                status: 'on_shelf',
                goods_code: '',
                title: '',
                start_time: '',
                end_time: '',
                is_export: ''
            },
            success: function (res) {
                var dataArr = res.data;
                var length = dataArr.length;
                goodCount = parseInt(res.total);
                if (length > 0){
                    loadTable(dataArr)
                }
            }
        })
    }
    loadGoodList();

    // 上架, 下架
    $('.merchandise_warehouse_table').on("click", ".grounding_btn", function () {
        var dir = $(this).data("dir");
        var type_id = null;
        var goodId = $(this).parents("tr").data('id');
        var confirm = null;
        if (dir == 1) {
            // 上架
            type_id = 1;
            confirm = window.confirm('确定上架吗?');
        } else {
            // 下架
            type_id = 2;
            confirm = window.confirm('确定下架吗?');
        }
        if (confirm) {
            $.ajax({
                url: groundingUrl,
                type: "POST",
                data: {
                    stores_id: stores_id,
                    oauth_token: oauth_token,
                    login_type: 2,
                    type_id: type_id,
                    goods_id: goodId
                },
                success: function (res) {
                    var resData =  eval("(" + res + ")");
                    layer.msg(resData.msg);
                    setTimeout(function () {
                       history.go(0);
                    }, 3000)
                    
                }
            });
        }
    })


  
    setTimeout(function () {
        laypage.render({
            elem: 'test1',
            count: goodCount,
            limit: 5,
            jump: function(obj, first){
                var page = parseInt(obj.curr) - 1;
                if (!first) {
                     loadGoodList(page)
                }
            }
        });
    }, 1500);
});