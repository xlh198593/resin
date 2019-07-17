$(function () {

    var itemIndex = 0;

    var tabLoadEndArray = [false, false, false];
   
    var tabLenghtArray = [20, 15, 47];

    var tabScroolTopArray = [0, 0, 0];
    
    // 代理商 商户page
    var agentPage = 0;

    // 代理商 合伙人page
    var copartnerPage = 0;

    var userInfo = getLocalStorage('userInfo');

    // 用户类型
    // 不删 var account_type = userInfo.data.CompanyAccount.account_type;
    // var account_type = 'subcompany';
    var account_type = 'co-partner';
    
    // list 状态
    var tabStatus = 'agent';


    // 每个状态下渲染list 长度
    var agentListLenght = 0;

    // 加载代理商 商户list
    function getAgentList (agentPage, callback) {
        // 不删 var userInfo = getLocalStorage('userInfo');
        var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            memberId: 'acee59d9076083ddf1a80741106a0b54',

            queryKey: ''
        }
        var page = {
            page_no: agentPage,
            page_size: 2
        }
        var getAgentListArr = [];
        $.ajax({
            url: agentList,
            type: 'POST',
            data: {
                params: JSON.stringify(params),
                page: JSON.stringify(page),
                service: 'pagelist.listSubStoreInfoList'
            },
            success: function (reData) {
                // if (reData.rsp_code == 'succ') {
                //     callback&&callback(reData.data.soresInfoList) ;
                // } else {
                //     $.DialogByZ.Autofade({Content: "加载数据失败"});
                // }
                var agentData = reData.data;
                $(".allStoreCount").html(agentData.allStoreCount);
                $(".directStoreCount").html(agentData.directStoreCount);
                $(".indirectStoreCount").html(agentData.indirectStoreCount);
                agentListLenght = reData.data.soresInfoList.length;
                callback&&callback(reData);
            }
        })
    }
    


    // 加载代理商 合伙人list
    function getCopartnerList (copartnerPage, callback) {
        // 不删 var userInfo = getLocalStorage('userInfo');
        var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            memberId: 'acee59d9076083ddf1a80741106a0b54'
        }
        var page = {
            page_no: copartnerPage,
            page_size: 2
        }
        var getAgentListArr = [];
        $.ajax({
            url: agentCopartnerList,
            type: 'POST',
            data: {
                params: JSON.stringify(params),
                page: JSON.stringify(page),
                service: 'pagelist.listFindCopartnerInfoBySub'
            },
            success: function (reData) {
                // if (reData.rsp_code == 'succ') {
                //     callback&&callback(reData.data.soresInfoList) ;
                // } else {
                //     $.DialogByZ.Autofade({Content: "加载数据失败"});
                // }
                var copartnerData = reData.data;
                agentListLenght = reData.data.copartnerInfoList.length;
                $(".allStoreCount1").html(copartnerData.allCopartnerCount);
                $(".directStoreCount1").html(copartnerData.directRecommendCount);
                $(".indirectStoreCount1").html(copartnerData.indirectRecommendCount);
                callback&&callback(reData);
            }
        })
    }

    
    // 加载合伙人 商户list
    function getAgentListC (agentPage, callback) {
        // 不删 var userInfo = getLocalStorage('userInfo');
        var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            memberId: 'e0c25d25002b0ddbd9e3c5f3f9a6373a',
            queryKey: ''
        }
        var page = {
            page_no: copartnerPage,
            page_size: 2
        }
        var getAgentListArr = [];
        $.ajax({
            url: agentList,
            type: 'POST',
            data: {
                params: JSON.stringify(params),
                page: JSON.stringify(page),
                service: 'pagelist.listCoStoreInfoList'
            },          
            success: function (reData) {
                // if (reData.rsp_code == 'succ') {
                //     callback&&callback(reData.data.soresInfoList) ;
                // } else {
                //     $.DialogByZ.Autofade({Content: "加载数据失败"});
                // }
                var agentData = reData.data;
                $(".directStoreCount").html(agentData.directStoreCount);
                agentListLenght = reData.data.soresInfoList.length;
                callback&&callback(reData);
            }
        })
    }

   

    // 加载合伙人 合伙人list
    function getCopartnerListC (copartnerPage, callback) {
        // 不删 var userInfo = getLocalStorage('userInfo');
        var params = {
            // 不删 memberId: userInfo.data.CompanyAccount.account_id
            memberId: 'e0c25d25002b0ddbd9e3c5f3f9a6373a'
        }
        var page = {
            page_no: copartnerPage,
            page_size: 2
        }
        var getAgentListArr = [];
        $.ajax({
            url: agentCopartnerList,
            type: 'POST',
            data: {
                params: JSON.stringify(params),
                page: JSON.stringify(page),
                service: 'pagelist.listFindCopartnerInfoByCo'
            },
            success: function (reData) {
                // if (reData.rsp_code == 'succ') {
                //     callback&&callback(reData.data.soresInfoList) ;
                // } else {
                //     $.DialogByZ.Autofade({Content: "加载数据失败"});
                // }
                var copartnerData = reData.data;
                agentListLenght = reData.data.copartnerInfoList.length;
                tabLenghtArray[1] = copartnerData.page.total_count;
                // $(".allStoreCount1").html(copartnerData.allCopartnerCount);
                // $(".directStoreCount1").html(copartnerData.directRecommendCount);
                // $(".indirectStoreCount1").html(copartnerData.indirectRecommendCount);

                callback&&callback(reData);

            }
        })
    }


    $(".tabHead").find("span").click(function () {
        tabStatus = $(this).attr("status"); 
        tabScroolTopArray[itemIndex] = $(window).scrollTop();
        var $this = $(this);
        itemIndex = $this.index();
        $(window).scrollTop(tabScroolTopArray[itemIndex]);
        
        $(this).addClass('active').siblings('.tabHead span').removeClass('active');
        $('.tabHead .border').css('left', $(this).offset().left + 'px');
        $('.khfxPane').eq(itemIndex).show().siblings('.khfxPane').hide();

        if (!tabLoadEndArray[itemIndex]) {
            dropload.unlock();
            dropload.noData(false);
        } else {
            dropload.lock('down');
            dropload.noData();
        }
        dropload.resetload();
    })

    
    //  代理商登录
    if ( account_type == 'subcompany') {
        $(".subcompany_con").show();
        $(".copartner_con").remove();
        // 代理商  统计商户总条数
        if (tabStatus == 'agent') {
            getAgentList(1, function (reData) {
                var agentData = reData.data;
                tabLenghtArray[0] = agentData.page.total_count;
            });
        }

        
        if (tabStatus == 'copartner') { 
            getCopartnerList(1, function (reData) {
                var copartnerData = reData.data;
                tabLenghtArray[1] = copartnerData.page.total_count;
            })
        }
    }
 
    
    // 合伙人登陆
    if ( account_type == 'co-partner') {
        $(".subcompany_con").remove();
        $(".copartner_con").show();
        if (tabStatus == 'agent') {
            getAgentListC(1, function (reData) {
                var agentData = reData.data;
                tabLenghtArray[0] = agentData.page.total_count;
            });
        }


        if (tabStatus == 'copartner') {
          
            getCopartnerListC(1, function (reData) {
                var copartnerData = reData.data;
                tabLenghtArray[1] = copartnerData.page.total_count;
            })
        } 
    }
     
   



 
     

    // dropload
    var dropload = $('.khfxWarp').dropload({
        scrollArea: window,
        domDown: {
            domClass: 'dropload-down',
            domRefresh: '<div class="dropload-refresh">上拉加载更多</div>',
            domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
            domNoData: '<div class="dropload-noData">已无数据</div>'
        },
        loadDownFn: function (me) {
            // 代理商
            if (account_type == 'subcompany') {

                $(".subcompany_con").show();
                $(".copartner_con").remove();
                // 代理商商户数组
                var getAgentListData = [];
                // 代理商合伙人数组
                var getCopartnerListData = [];
                if (tabStatus == 'agent') {
                    agentPage++;
                    getAgentList(agentPage, function (reData) {
                        getAgentListData = reData.data.soresInfoList;
                    });
                }
                if (tabStatus == 'copartner') {
                    copartnerPage++;
                    getCopartnerList(copartnerPage, function (reData) {
                        getCopartnerListData = reData.data.copartnerInfoList;
                    })
                }
                

                setTimeout(function () {  
                        if (tabLoadEndArray[itemIndex]) {
                            me.resetload();
                            me.lock();
                            me.noData();
                            me.resetload();
                            return ;
                        }

                        var result = '';

                        for (var index = 0; index < agentListLenght; index++) {
                            if (getAgentListData.length > 0) {
                                var introStr = getAgentListData[index].recommendType == '自推'?'<span class="intro">自推商户</span>':'';
                            }
                         
                            if (tabLenghtArray[itemIndex] > 0) {
                                tabLenghtArray[itemIndex]--;
                            } else {
                                tabLoadEndArray[itemIndex] = true;
                                break;
                            }
                            if (itemIndex == 0) {
                                result
                                += ''
                                +'   <a href="./mercantile_agent_detail2.html" class="good" item='+ JSON.stringify(getAgentListData[index]) +'>'
                                +'       <div class="img"><img src="'+ getAgentListData[index].headPicPath +'" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit">'+ getAgentListData[index].storesName +'</div>'
                                +'          <p class="txt">'+ getAgentListData[index].address +'</p>'
                                +'          <div class="total">'
                                +'              <span>店铺订单创收:</span>'
                                +'              <span style="color:#F12E5C;">￥'+ getAgentListData[index].profit +'</span>'
                                +               introStr 
                                +'              <span style="float:right;">'+ getAgentListData[index].recommendName +'</span>'
                                +'              <span style="float:right;">直推合伙人:</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            } else 
                            if (itemIndex == 1) {
                                result
                                += ''
                                +'   <a href="./mercantile_agent_detail3.html" class="good" item='+ JSON.stringify(getCopartnerListData[index]) +'>'
                                +'       <div class="img"><img src="'+ getCopartnerListData[index].profilePic +'" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit"><span>'+ getCopartnerListData[index].accountName +'</span><span style="margin-left:10px;">'+ getCopartnerListData[index].mobile +'</span></div>'
                                +'          <p class="txt"><span>直推商户:</span><span style="margin-left:10px;">'+ getCopartnerListData[index].recomStoresNum +'</span>家</p>'
                                +'          <div class="total">'
                                +'              <span>推荐方式:</span>'
                                +'              <span style="color:#F12E5C;">'+ getCopartnerListData[index].recommendType +'</span>'
                                +'              <span style="float:right;">'+ getCopartnerListData[index].recomPartnerNum +'人</span>'
                                +'              <span style="float:right;">直推合伙人:</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            } else 
                            if (itemIndex == 2) {
                    
                                result
                                += ''
                                +'   <a href="./mercantile_agent_detail2.html" class="good" item='+ JSON.stringify(getAgentListData[index]) +'>'
                                +'       <div class="img"><img src="images/shopImg.png" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit">潮汕牛肉火锅（万象城店）牛肉火牛肉...</div>'
                                +'          <p class="txt">深圳市福田区泰然科技园213栋泰然科技园店...</p>'
                                +'          <div class="total">'
                                +'              <span>店铺订单创收:</span>'
                                +'              <span style="color:#F12E5C;">￥399</span>'
                                +'              <span class="intro">自推商户</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            }
                        }

                        $('.khfxPane').eq(itemIndex).append(result);
                        me.resetload();
                        return ;
                }, 500);
            }
           

            // 合伙人
            if (account_type == 'co-partner') {
             
                $(".subcompany_con").remove();
                $(".copartner_con").show();
                // 合伙人  代理商数组
                var getAgentListDataC = [];

               
                // 合伙人  合伙人数组
                var getCopartnerListDataC = [];


                if (tabStatus == 'agent') {
                    agentPage++;
                    getAgentListC(agentPage, function (reData) {
                        getAgentListDataC = reData.data.soresInfoList;
                    });
                }

                if (tabStatus == 'copartner') {
                    copartnerPage++;
                    getCopartnerListC(copartnerPage, function (reData) {
                        getCopartnerListDataC = reData.data.copartnerInfoList;
                    })
                }
                
                setTimeout(function () {  
                        if (tabLoadEndArray[itemIndex]) {
                            me.resetload();
                            me.lock();
                            me.noData();
                            me.resetload();
                            return;
                        }
                        var result = '';

                        for (var index = 0; index < agentListLenght; index++) {
                            // if (getCopartnerListData.length > 0) {
                            //     var introStr = getAgentListDataC[index].recommendType == '自推'?'<span class="intro">自推商户</span>':'';
                            // }
                         
                            if (tabLenghtArray[itemIndex] > 0) {
                                tabLenghtArray[itemIndex]--;
                            } else {
                                tabLoadEndArray[itemIndex] = true;
                               // break;
                               return ;
                            }
                            if (itemIndex == 0) {
                                result
                                += ''
                                +'   <a href="./mercantile_agent_detail2.html" class="good" item='+ JSON.stringify(getAgentListDataC[index]) +'>'
                                +'       <div class="img"><img src="'+ getAgentListDataC[index].headPicPath +'" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit">'+ getAgentListDataC[index].storesName +'</div>'
                                +'          <p class="txt">'+ getAgentListDataC[index].address +'</p>'
                                +'          <div class="total">'
                                +'              <span>店铺订单创收:</span>'
                                +'              <span style="color:#F12E5C;">￥'+ getAgentListDataC[index].profit +'</span>'
                                // +               introStr 
                                +'              <span style="float:right;">'+ getAgentListDataC[index].recommendName +'</span>'
                                +'              <span style="float:right;">直推合伙人:</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            } else 
                            if (itemIndex == 1) {
                                result
                                += ''
                                +'   <a href="./mercantile_agent_detail3.html" class="good" item='+ JSON.stringify(getCopartnerListDataC[index]) +'>'
                                +'       <div class="img"><img src="'+ getCopartnerListDataC[index].profilePic +'" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit"><span>'+ getCopartnerListDataC[index].accountName +'</span><span style="margin-left:10px;">131****8888</span></div>'
                                +'          <p class="txt"><span>直推商户:</span><span style="margin-left:10px;">'+ getCopartnerListDataC[index].recomPartnerNum +'</span>家</p>'
                                +'          <div class="total">'
                                +'              <span>推荐方式:</span>'
                                +'              <span style="color:#F12E5C;">自推</span>'
                                +'              <span style="float:right;">'+ getCopartnerListDataC[index].recomPartnerNum +'人</span>'
                                +'              <span style="float:right;">直推合伙人:</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            } else 
                            if (itemIndex == 2) {
                                console.log(itemIndex)
                                result
                                += ''
                                +'   <a href="#" class="good">'
                                +'       <div class="img"><img src="images/shopImg.png" width="100%"></div>'
                                +'       <div class="text">'
                                +'          <div class="tit">潮汕牛肉火锅（万象城店）牛肉火牛肉...</div>'
                                +'          <p class="txt">深圳市福田区泰然科技园213栋泰然科技园店...</p>'
                                +'          <div class="total">'
                                +'              <span>店铺订单创收:</span>'
                                +'              <span style="color:#F12E5C;">￥399</span>'
                                +'              <span class="intro">自推商户</span>'
                                +'           </div>'
                                +'       </div>'
                                +'  </a>';
                            }
                        }

                        $('.khfxPane').eq(itemIndex).append(result);
                        me.resetload();
                        return ;
                }, 500);
            }
           
        }
    });


    // $('.tabHead span').on('click', function () {

    //     tabScroolTopArray[itemIndex] = $(window).scrollTop();
    //     var $this = $(this);
    //     itemIndex = $this.index();
    //     $(window).scrollTop(tabScroolTopArray[itemIndex]);
        
    //     $(this).addClass('active').siblings('.tabHead span').removeClass('active');
    //     $('.tabHead .border').css('left', $(this).offset().left + 'px');
    //     $('.khfxPane').eq(itemIndex).show().siblings('.khfxPane').hide();

    //     if (!tabLoadEndArray[itemIndex]) {
    //         dropload.unlock();
    //         dropload.noData(false);
    //     } else {
    //         dropload.lock('down');
    //         dropload.noData();
    //     }
    //     dropload.resetload();
    // });



    // 选择哪个商户进入商户详情
    $(".container").on("click", '.good', function () {
         var dataStr = $(this).attr("item");
         var data = JSON.parse(dataStr);
         setLocalStorage('agentItem', data);
    })

});