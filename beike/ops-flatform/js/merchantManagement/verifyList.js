layui.define(['element', 'form', 'laydate', 'table', 'laypage', 'jquery', 'common', 'url', 'laytpl'], function (exports) {
	var element = layui.element;
	var form = layui.form;
	var laydate = layui.laydate;
	var table = layui.table;
	var laypage = layui.laypage;
	var laytpl = layui.laytpl;

	var $ = layui.jquery;
    
    var common = layui.common;
    var requestUrl = layui.url;
	var userInfo = JSON.parse(common.getCookie('userInfo'));
	var token = userInfo.token;
	var userId = userInfo.account_id;

	// 数据总条数
	var totalCount = null;
	//日期范围
	laydate.render({
	    elem: '#date',
	    range: true
	});
    

  
    // 商户list
    function getVerifyList (pageCount) {
    	var searchOption = {
    			contact_tel: '15220214256',
    			stores_type: '个人',
    			stores_name: '贝壳',
    			audit_status: '审核通过',
    			created_date: '2019-04-01 - 2019-05-01'
    		};
        var searchOptionStr = JSON.stringify(searchOption);
        var trHtml = '';
        $.ajax({
        	url: '../../views/merchantManagement/verifyList.html',
        	type: 'get',
        	success: function (resHtml) {
                trHtml = resHtml;
        	} 
        });

        $.ajax({
    		url: requestUrl.verifyListPage.verifyList,
    		type: 'POST',
    		'Content-Type': 'application/json',
    		data: {
    			token: token,
    			account_id: userId,
    			page: pageCount || 1,
                status: 'suspend'
    		},
    		success: function (resData) {
    			totalCount = parseInt(resData.total);
                laytpl(trHtml).render({verifyList: resData.datas, requestUrl: requestUrl}, function (trView) {
                    $("#verify_table").find("tbody").html(trView);
                })
    		}
		})
    }

    getVerifyList();

    setTimeout(function () {
        // 分页
		laypage.render({
		    elem: 'page',
		    count: totalCount,
		    limit: 10,
		    jump: function(obj){
		      var pageCount = parseInt(obj.curr);
		      getVerifyList(pageCount);
		    }
		});
    }, 1500);

    $("#verify_table").on('click', '.change_page', function () {
        var $this = $(this);
        var aBtn = $this.siblings('a').eq(0);
        var verifyShopData = $this.parents('tr').attr('item');
        common.setStorage('verifyShop', verifyShopData);
        aBtn.find('span').trigger('click');
    })
    


    // $(document).click(function () {
    //     var obj = {
    //         url: requestUrl.verifyListPage.verifyList,
    //         type: 'POST',
    //         data: {
    //            page: 1
    //         }
    //     }
    //     common.ajax(obj, function (reData) {
    //         console.log('-------------re-------')
    //         console.log(reData)
    //         console.log('-------------re-------')
    //     })
    // })
    exports('merchantManagement/verifyList', {})
})