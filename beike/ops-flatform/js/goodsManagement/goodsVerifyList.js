layui.define(['form', 'common', 'jquery', 'laydate', 'element', 'laypage', 'laytpl', 'url'], function (exports) {
  var form = layui.form;
  var common = layui.common;
  var $ = layui.jquery;
  var laydate = layui.laydate;
  var laypage = layui.laypage;
  var laytpl = layui.laytpl;

 

  var common = layui.common;
  var requestUrl = layui.url;
  var userInfo = JSON.parse(common.getCookie('userInfo'));
  var token = userInfo.token;
  var userId = userInfo.account_id;
  var goodCountTotal = 0;

  laydate.render({
  	elem: '#date',
  	range: true
  });

  
  // 加载审核table数据
  function loadGoodList (pageCount) {
      var trView = '';
      $.ajax({
          url: '../../views/goodsManagement/goodsVerifyList.html',
          type: 'get',
          success: function (reHtml) {
             trView  = reHtml;
          }
      });

      $.ajax({
          url: requestUrl.goodsListPage.goodsList,
          type: 'POST',
          'Content-Type': 'application/json',
          data: {
              token: token,
              account_id: userId,
              page: pageCount || 1,
              status:'suspend'
          },
          success: function (reData) {
              var goodsData = reData.datas;

              goodCountTotal = reData.total;
              laytpl(trView).render({goodsData: goodsData, imgUrl: requestUrl.imgUrl}, function (trHtml) {
                 $("#verify_table").find('tbody').html(trHtml);
              });
          }
      })
  }

  loadGoodList();
 
  setTimeout(function () {
      // 分页
      laypage.render({
        elem: 'page',
        limit: 10,
        count: goodCountTotal,
        jump: function (obj) {
           console.log(obj)
           var pageCount = obj.curr;
           loadGoodList(pageCount);
        }
      });
  }, 1500);
 
  

  // 设置点击那个商品具体信息
  $("#verify_table").on('click','.detail_btn', function () {
      var $this = $(this); 
      var goodVerifyStr = $this.parents('tr').attr('item');
      common.setStorage('goodVerify', goodVerifyStr);
      $this.siblings('a').find('span').trigger('click');
  });


  exports('goodsManagement/goodsVerifyList', null);





});