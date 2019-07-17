layui.define(['form', 'common', 'jquery', 'laydate', 'element', 'laypage', 'url', 'laytpl'], function (exports) {
  var form = layui.form;
  var common = layui.common;
  var $ = layui.jquery;
  var laydate = layui.laydate;
  var laypage = layui.laypage;
  var laytpl = layui.laytpl;
  var requestUrl = layui.url;
  var userInfo = JSON.parse(common.getCookie('userInfo'));
  var token = userInfo.token;
  var userId = userInfo.account_id;
  // 商品总条数
  var goodTotalAll = 0;
  // 下单时间
  laydate.render({
  	elem: '#date',
  	range: true
  })


  // 获得商品list
  function getGoodsList (pageCount) {
      var trView = '';
      $.ajax({
          url: '../../views/goodsManagement/goodsList.html',
          type: 'get',

          success: function (resHtml) {  
              trView = resHtml;
          } 
      });

      $.ajax({
          url: requestUrl.goodsListPage.goodsList,
          type: "POST",
          'Content-Type': 'application/json',
          data: {
              token: token,
              account_id: userId,
              page: pageCount || 1
          },
          success: function (resData) {
              goodTotalAll = resData.total;
              laytpl(trView).render({goodsList: resData.datas, imgUrl: requestUrl.imgUrl }, function (trHtml) {
                  $("#goodsList").find('tbody').html(trHtml);
              })
          }
      })
  }
  getGoodsList();
 

  // 分页
  setTimeout(function () {
      laypage.render({
        elem: 'page',
        limit: 10,
        count: goodTotalAll,
        jump: function (obj) {
          console.log(obj)
          var pageCount = parseInt(obj.curr);
          getGoodsList(pageCount);
        }
      })
  }, 1500);

  $("#goodsList").on('click','.detail_btn', function () {
      var $this = $(this); 
      var goodVerifyStr = $this.parents('tr').attr('item');
      common.setStorage('goodVerify', goodVerifyStr);
      // window.localStorage.setItem('goodVerifyId', goodId);
      $this.siblings('a').find('span').trigger('click');
  })
 
  exports('goodsManagement/goodsList', {});



});