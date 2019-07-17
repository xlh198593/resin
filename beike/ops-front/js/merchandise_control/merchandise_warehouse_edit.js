layui.define(['layer', 'jquery', 'common/common_b', 'common/url_b', 'laytpl', 'form', "laydate"], function (exports) {
    var layer = layui.layer;
    var $ = layui.jquery;
    var laytpl = layui.laytpl;
    var laydate = layui.laydate;
	var common = layui['common/common_b'];
	var form = layui.form;
	var requestUrl = layui['common/url_b'];
    // common.clearOldData();
    // 商品id
    var goodId = common.getStorage('goodId', 'string');
    laydate.render({
	    elem: '#test15',
	    type: 'datetime',
	    range: '到',
	    format: 'yyyy-M-d H:m:s'
	});

	laydate.render({
	    elem: '#test16',
	    type: 'time',
	    range: true
	});




    // 选择是否参加活动
    var activeStatus = '是';   
    $('.active_status_con').find(".layui-unselect").click(function () {
      var $this = $(this);
      var price = $(".price");
      var payCount = $(".pay_count");
      var pay_count_unit = $(".pay_count_unit");
      var shellCount = $(".shell_count");
      var active_btn = $(".active_btn");
          activeStatus = $this.find('div').html();
        if (activeStatus == '是') {
            $(".active_con").show();
            payCount.attr('disabled', "disabled");
            active_btn.removeClass('on');
            $(active_btn.get(0)).trigger("click").addClass('on');
            $(".discount").show();
        } else {
            $(".active_con").hide();
            $(".and_icon").show();
            payCount.show();
            pay_count_unit.show();
            payCount.removeAttr('disabled');
            $(".discount").hide();
        }
        price.val('');
        payCount.val('');
        shellCount.val('');
    });


    // 参加哪项活动
    var activeItemStatus = 'half';
    // 打折按钮
    $(".active_btn").click(function () {
        var $this = $(this);
        var price = $(".price");
        var payCount = $(".pay_count");
        var shellCount = $(".shell_count");
        var pay_count_unit = $(".pay_count_unit");

        activeItemStatus = $this.attr('activeItemStatus');
        $('.active_btn').removeClass('on');
        $this.addClass('on');
        if (activeItemStatus == 'half') {
          // 超划算
          $(".and_icon").show();
          payCount.show();
          pay_count_unit.show();
          $(".discount").show();
        } else if (activeItemStatus == "shell") {
          // 天天抢
          $(".and_icon").hide();
          payCount.hide();
          pay_count_unit.hide();
          $(".discount").hide();
        }
            price.val('');
            payCount.val('');
            shellCount.val('');
    });




    // 加载详情
    function getGoodDetail () {
		var option = {
			url: requestUrl.merchandise_control.merchandise_warehouse_edit.detail,
			type: 'POST',
			data: {
              goods_id: goodId
			}
		}

        common.ajax(option, function (reData) {
        	var data = JSON.parse(reData).data;
        
            // 商品标题
            $(".good_title").val(data.title);
            // 副标题
            $(".small_title").val(data.desc1);
            // 商品编码
            $(".goods_code").val(data.goods_code);
          
            // 商品图片 
            var imgInfo = eval("("+ data.pic_info +")");
            var uploadImgStr = '';
            var imgIndex = 0;
            for (var i in data.pic_info_new) {
                var imgTxt = imgIndex == 0? '<p class="txt">封面页</p>': '<p class="txt">缩略图</p>';
                var closeBtn = imgIndex == 0? '': '<span class="close">X</span>';
                if (data.pic_info_new[i]) {
                    uploadImgStr += '<div class="uploadimg_con">'+
                                        '<div class="layui-upload-list uploadimg" id="test'+ imgIndex +'">'+
                                            closeBtn +
                                            '<img class="layui-upload-img uploadimg_src" id="demo'+ imgIndex +'" width="100%" height="99.5%" src="'+  data.pic_info_new[i] +'">'+
                                            '<input type="file" name="imageFile" class="upload_img_btn" id="uploadImg'+ imgIndex +'" picid="'+ imgInfo[imgIndex].path_id +'">'+
                                        '</div>'+
                                        imgTxt +
                                    '</div>'
                }
                imgIndex++;
            }

            $(".uploadimg_add_con_btn").before(uploadImgStr);
            // 记录图片个数
            var imgLength = $(".upload_img_btn").length;
            $(".uploadimg_con_list").attr("count", imgLength);

           
            // 图片展示类型
            var imgType = imgInfo[0].type;
            $(".layui-form-radio").eq(imgType-1).trigger('click');
            $(".select_img_type").hide();
            $(".select_img_type").eq(imgType-1).show();


            // 套餐
            var setmeal = JSON.parse(data.specification);
            var setmealStr = '';
            // 商品标题
            var makeTabStr = '';
            if (setmeal) {
                for (var i=0; i<setmeal.length; i++) {
                    setmealStr += '	<li>'+
										'<div class="grade_1">'+
											'<input type="text" name="" class="grade_val_1 grade_val" placeholder="输入一级类型名称" value="'+ setmeal[i].class_name +'">'+
											'<span class="close_1_btn">X</span>'+
										'</div>'+
									 
										'<ul class="grade_2" count="'+ setmeal[i].data.length +'">';

										for (var j=0; j<setmeal[i].data.length; j++) {
											setmealStr +=   '<li>'+
																'<input type="text" name="" class="grade_val_2 grade_val" value="'+ setmeal[i].data[j].name +'">'+
																'<span class="close_2_btn">X</span>'+
															'</li>'
										}

	                    setmealStr +=       '<div class="add_grade_2_btn">增加</div>'+
										'</ul>'+
									'</li>'						 
                }
                $(".goods_grade_list").html(setmealStr);
                $(".goods_grade_list").attr("count", setmeal.length);

                // 商品标题
                for (var i=0; i<setmeal.length; i++) {
                    for (var j=0; j<setmeal[i].data.length; j++) {
                    	if (j == 0) {
                            makeTabStr +=   '<tr>'+
	                                            '<td rowspan="'+ setmeal[i].data.length +'">'+ setmeal[i].class_name +'</td>'+
	                                            '<td>'+ setmeal[i].data[j].name +'</td>'+
	                                            '<td><input type="text" name="" class="tab_val" value="'+ setmeal[i].data[j].num +'"></td>'+
	                                            '<td><input type="text" name="" class="tab_val" value="'+ setmeal[i].data[j].money +'"></td>'+
	                                        '</tr>';
                    	} else {
                    		makeTabStr +=   '<tr>'+
	                                            '<td style="display:none;">'+ setmeal[i].class_name +'</td>'+
	                                            '<td>'+ setmeal[i].data[j].name +'</td>'+
	                                            '<td><input type="text" name="" class="tab_val" value="'+ setmeal[i].data[j].num +'"></td>'+
	                                            '<td><input type="text" name="" class="tab_val" value="'+ setmeal[i].data[j].money +'"></td>'+
	                                        '</tr>';
                    	}

                         
                    }
                }
                $(".make_tab").find("tbody").html(makeTabStr);
            }

            // 库存
            $(".stock_qty").val(data.stock_qty);




            // 参加活动
            var activeItemLabel = data.label;
            if (activeItemLabel == 11) {
                // 天天抢
                activeStatus = '是';
                activeItemStatus = 'shell';
                $('.active_status_con').find(".layui-unselect").eq(0).trigger('click');
                $(".active_btn").eq(1).trigger('click');
            } else if (activeItemLabel == 12) {
                // 超划算
                activeStatus = '是';
                activeItemStatus = 'half';
                $('.active_status_con').find(".layui-unselect").eq(0).trigger('click');
                $(".active_btn").eq(0).trigger('click');
            } else if (activeItemLabel == 0) {
                // 不参加活动
                activeStatus = '否';
                activeItemStatus = '';
                $('.active_status_con').find(".layui-unselect").eq(1).trigger('click');
               
            }


            // 原价(门店价)
            $(".price").val(data.sale_price);

            // 超级会员价
            $(".pay_count").val(data.vip_price);

            // 抵扣贝壳
            $(".shell_count").val(data.beike_credit);

            // 折扣
            var beatVal = (parseFloat(data.discount_rate) * 10) + '折';
            $(".beat .layui-select-title").find(".layui-unselect").val(beatVal);


            // 商品有效期
            var activityTime = data.activity_start_time + ' 到 ' + data.ativity_end_time;
            $("#test15").val(activityTime);

            // 使用时间
            var perchase_notice = JSON.parse(data.perchase_notice);
            var useTime = perchase_notice.use_time.split(' ');

            var useTime1 = useTime[0];
            var useTime2 = useTime[1].split('-');
        
            var ddS = $(".ticket_use_date").find("dl").find("dd");
            ddS.each(function (index, obj) {
                var _this =  $(obj);
                var dHtml = _this.html();
                if (dHtml == useTime1) {
                   _this.trigger('click');
                }
            })
            $("#test16").val(useTime2[0] + ' - ' + useTime2[1]);

            // 使用规则
            var max_count = perchase_notice.max_count;
            $(".ticket_use_count").find("dd").each(function (i, d) {
                var _this = $(d);
                var ticketCount = _this.attr('lay-value');
                if (ticketCount == max_count) {
                    _this.trigger("click");
                }
            })


            // 提供发票
            var use_bill = $(".use_bill").find(".layui-unselect");
            var is_offer_invoice = perchase_notice.is_offer_invoice;
         
            $(".use_bill").find("dd").each(function (i, d) {
                var $this = $(d);
                var billVal = $this.attr("lay-value");
                if (billVal == is_offer_invoice) {
                    $this.trigger('click');
                }
            })

            // 其它事项
            var rest_remarkS = JSON.parse(perchase_notice.rest_remark);
            var otherLiStr = '';
            for (var i=0; i<rest_remarkS.length; i++) {
                otherLiStr +=  	'<li>'+
									'<input type="text" name="other" placeholder="例如：适用范围：除酒水饮料、茶位蘸料、特价菜外全场通用" autocomplete="off" value="'+ rest_remarkS[i] +'">'+
									'<span class="close_btn">X</span>'+
								'</li>';
            }
            $(".other_list").html(otherLiStr).attr("count", rest_remarkS.length);


            // 商家服务 
            var service_level = data.service_level;
            var serviceLevelArr = service_level.split(",");
            var serveIcon = $(".shop_serve_select .drawback_select_list");
            for (var i=0; i<serviceLevelArr.length; i++) {
               serveIcon.eq(serviceLevelArr[i]-1).find(".layui-unselect").addClass("layui-form-checked");
            }

        });
    }


    getGoodDetail()







    // 点击字查看大图
    $(".merchandise_control").on('click', '.uploadimg_con .txt', function () {
         common.showBigImg(this);
    })

    // 新增上传图片
    $(".uploadimg_add_btn").click(function () {
          var uploadimgConList = $(".uploadimg_con_list");
          var uploadImgCount =  parseInt(uploadimgConList.attr('count'));
          var text = '';
          if (uploadImgCount < 5) {
              uploadImgCount++;
              text = uploadImgCount == 1? '封面页': '缩略图';  
              uploadimgConList.attr('count', uploadImgCount);

              var closeBtn = uploadImgCount == 1? '': '<span class="close">X</span>';
              var str =  '<div class="uploadimg_con">'+
                            '<div class="layui-upload-list uploadimg" id="test'+ uploadImgCount +'">'+
                                closeBtn+
                                '<img class="layui-upload-img uploadimg_src" id="demo'+ uploadImgCount +'" width="100%" height="99.5%" src="../images/upload_default.png">'+
                                '<input type="file" name="imageFile" class="upload_img_btn" id="uploadImg'+ uploadImgCount +'">'+
                            '</div>'+
                            '<p class="txt">'+ text +'</p>'+
                          '</div>';
              $(".uploadimg_add_con").before(str);
          } 
          if (uploadImgCount == 5) {
            $(".uploadimg_add_btn").hide();
          }
    });

    //  删除上传的商品图片
    $(".uploadimg_con_list").on('click', '.uploadimg .close', function () {
          var $this = $(this);
          var uploadimgConList = $(".uploadimg_con_list");
          var uploadImgCount =  parseInt(uploadimgConList.attr('count'));
          var uploadimgCon = $this.parents(".uploadimg_con");
          if (uploadImgCount > 0) {
            uploadImgCount--;
            uploadimgCon.remove();
            uploadimgConList.attr('count', uploadImgCount);
            $(".uploadimg_add_con").show().find(".uploadimg_add_btn").show();
          }
    });


    var selectImgType = 1;
    // 选择商品图片展示类型
    $(".select_img_btn").find('.layui-unselect').click(function () {
        var $this = $(this);
        var radioInput = $this.prev('input');
        var index = parseInt(radioInput.data('id'));
        selectImgType = index + 1;
        $(".select_img_type").hide();
        $('.select_img_type').eq(index).show();
    });


    // 第一等级增加商品
    $(".add_grade_1_btn").click(function () {
        var goods_grade_list = $(".goods_grade_list");
        var count = parseInt(goods_grade_list.attr('count'));
            var str =   '<li>'+
                                    '<div class="grade_1">'+
                                        '<input type="text" name="" class="grade_val_1 grade_val" placeholder="输入一级类型名称">'+
                                        '<span class="close_1_btn">X</span>'+
                                    '</div>'+
                                 
                                    '<ul class="grade_2" count="1">'+
                                        '<li>'+
                                            '<input type="text" name="" class="grade_val_2 grade_val">'+
                                            '<span class="close_2_btn">X</span>'+
                                        '</li>'+
                                        '<div class="add_grade_2_btn">增加</div>'+
                                    '</ul>'+
                                '</li>';
            if (count < 10) {
                count++;
                goods_grade_list.attr('count', count);
          goods_grade_list.append(str);
            } else {
          layer.msg('只能发布有10个商品');
          return ;
            }            
    });


    // 第一等级删除
    $(".goods_grade_list").on("click", ".close_1_btn", function () {
        var goods_grade_list = $(".goods_grade_list");
        var li = $(this).parents("li");
        var count = goods_grade_list.attr('count');
        if (count > 1) {
            li.remove();
            count--;
            goods_grade_list.attr('count', count);
        } else {
            layer.msg('必须发布1个商品');
            return ;
        }
    });


    // 增加第二等级商品
    $(".goods_grade_list").on('click', ".add_grade_2_btn", function () {
        var $this = $(this);
        var grade_2 = $this.parents(".grade_2");
        var count = grade_2.attr("count");
        var str =   '<li>'+
                        '<input type="text" name="" class="grade_val_2 grade_val">'+
                        '<span class="close_2_btn">X</span>'+
                    '</li>';
        if (count < 10) {
            count++;
            grade_2.attr('count', count);
            $this.before(str);
        } else {
            layer.msg('只能发布有10个商品');
            return ;
        }
    });



    // 第二等级删除商品
    $(".goods_grade_list").on('click', '.close_2_btn', function () {
        var $this = $(this);
        var li = $this.parents('li').get(0);
        var grade_2 = $this.parents(".grade_2");
        var count = grade_2.attr('count');
        if (count > 1) {
            count--;
            li.remove();
            grade_2.attr('count', count);
        } else {
            layer.msg('必须发布1个商品, 或者删除此等级')
        }
    });



    // // 活得类目名字
    // function getGoodNameArr () {
    //     // 全部类目名字数组
    //     var goodNameDataArr = [];
       
    //     // 第一等级全部li
    //     var liGradeF = $(".goods_grade_list").children("li");
    //     liGradeF.each(function (index, item) {
    //         var $this = $(item); 
    //         var firstName = $this.find(".grade_val_1").val();
    //         // 第二等级li
    //         var childGradeLis = $this.find('.grade_2').find('li');
    //         // 一个等级
    //         var gardeItem = {
    //             firstName: '',
    //             children: []
    //         };
    //         gardeItem.firstName = firstName;
    //         childGradeLis.each(function (i, val) {
    //             var gradeVal_2 = $(val).find(".grade_val_2").val();
    //             gardeItem.children.push({
    //                 id: i,
    //                 name: gradeVal_2
    //             })
    //         });
    //         goodNameDataArr.push(gardeItem);
    //     })
    //     return goodNameDataArr;
    // }


    // 活得类目名字
    function getGoodNameArr () {
        // 全部类目名字数组
        var goodNameDataArr = [];
        // 没有去重的一级分类名字
        var firstNameAll = [];
        var firstNameUniq = [];
        // 第一等级全部li
        var liGradeF = $(".goods_grade_list").children("li");
        liGradeF.each(function (index, item) {
            var $this = $(item); 
            var firstName = $this.find(".grade_val_1").val();
            firstNameAll.push(firstName);

            // 第二等级li
            var childGradeLis = $this.find('.grade_2').find('li');
            // 一个等级
            var gardeItem = {
                firstName: '',
                children: []
            };
            gardeItem.firstName = firstName;
            childGradeLis.each(function (i, val) {
                var gradeVal_2 = $(val).find(".grade_val_2").val();
                gardeItem.children.push({
                    id: i,
                    name: gradeVal_2
                })
            });
            goodNameDataArr.push(gardeItem);
        })
        firstNameUniq = uniq(firstNameAll);
        if (firstNameUniq.length != firstNameAll.length) {
           alert('套餐类目不能重复');
           return false;
        }
        return goodNameDataArr;
    }

    // 生成商品规格目录
    $(".make_table").click(function () {
        var  goodsNameArr = getGoodNameArr();
        var  str = '';
        var  makeTable = $(".make_tab").find("tbody");
        goodsNameArr.forEach(function (item, index) {
            var length = item.children.length;
            item.children.forEach(function (val, i) {
                if (i == 0) {
                    str +=  '<tr>'+
                                        '<td rowspan="'+ length +'">'+ item.firstName +'</td>'+
                                        '<td>'+ val.name +'</td>'+
                                        '<td><input type="text" name="" class="tab_val"></td>'+
                                        '<td><input type="text" name="" class="tab_val"></td>'+
                                '</tr>';
                } else {
                    str +=  '<tr>'+
                                        '<td style="display:none;">'+ item.firstName +'</td>'+
                                        '<td>'+ val.name +'</td>'+
                                        '<td><input type="text" name="" class="tab_val"></td>'+
                                        '<td><input type="text" name="" class="tab_val"></td>'+
                                '</tr>';
                }
            });
        });
      makeTable.html(str);
    });

    
    // 输入原价（门店价）
    $(".price").keyup(function () {
        var $this = $(this);
        var price = $this.val();
        var payCount = $(".pay_count");
        var shellCount = $(".shell_count");
        // 正整数
        var regPrice = /^[1-9]{1,}[\d]*$/;
        if (!regPrice.test(price)) {
                layer.msg('请输入正整数');
                $this.val('');
                return ;
            }

        if (activeStatus == '是' && activeItemStatus == 'half') {
            $(".discount .layui-anim").find("dd").eq(0).trigger('click')
            payCount.val('');
            shellCount.val('');

        } else if (activeStatus == '是' && activeItemStatus == 'shell') {
            // 贝壳
            shellCount.val(price);
        }
    });

    
    // 打折
    $(".discount .layui-anim dd").click(function () {
        var $this = $(this);
        var price = $(".price").val();
        var payCount = $(".pay_count");
        var shellCount = $(".shell_count");
        var discount = $this.attr('lay-value');
        var vipPrice = Math.round(price * discount);
        var shellPrice = Math.round(price - vipPrice);
        if (activeStatus == '是' && activeItemStatus == 'half') {
            if (price) {
                payCount.val(vipPrice);
                shellCount.val(shellPrice);
            } else {
                layer.msg('请输入门店价');
            }
        }
    })


    // 当是否不参加输入会员价统计贝壳数量
    $(".pay_count").keyup(function () {
       var $this = $(this);
       var price = parseInt($(".price").val());
       var shellCount = $(".shell_count");
       // 正整数
       var regPrice = /^[1-9]{1,}[\d]*$/;
       var payCount = parseInt($this.val());
       if (!regPrice.test(price)) {
          layer.msg('请输入正整数');
          return ;
       }


      if (!regPrice.test(payCount)) {
          layer.msg('请输入正整数');
          $this.val('');
          return ;
      }

      if (activeStatus == '否') {
          if (payCount > price) {
              layer.msg('会员价不能大于门店价');
              $this.val('');
              shellCount.val('');
              return ;
          }
          var dl = price - payCount;
          shellCount.val(dl);
      }
    });

    // 添加其他事项
    $(".add_other_btn").click(function () {
        var str = '<li>'+
                      '<input type="text" name="" placeholder="例如：适用范围：除酒水饮料、茶位蘸料、特价菜外全场通用" autocomplete="off">'+
                      '<span class="close_btn">X</span>'+
                  '</li>';
        var otherList = $(".know_that .other_list");
        var count = parseInt(otherList.attr('count'));
        if (count<5) {
        count++;
            otherList.append(str);
            otherList.attr("count", count);
        } else {
            layer.msg('只能添加5个');
        }
    });


    // 删除其他事项
    $(".other_list").on("click", ".close_btn", function () {
        var otherList = $(".know_that .other_list");
        var count = parseInt(otherList.attr('count'));
        var li = $(this).parents('li');
        if (count > 1) {
            count--;
            li.remove();
            otherList.attr('count', count);
        } else  {
            layer.msg('不能删除了')
        }
    })

    // 是否直接上架
    var selectGroundingStatus = 'suspend';
    $(".select_grounding_btn").find(".layui-form-switch").click(function () {
       var status = $(this).find('em').html();
       var sub_good_btn = $(".sub_good_btn");
       if (status == 'ON') {
          sub_good_btn.html('确认修改，审核通过后直接上架商品');
          selectGroundingStatus = 'checked_online';
       } else {
          sub_good_btn.html('提交审核并放入仓库');
          selectGroundingStatus = 'suspend';
       }
    })


    // 分类的商品数组
    function menuList () {
        var trS = $(".make_tab").find('tbody').children("tr");

        // 商品类的名字
        var curItemGoodName = trS.eq(0).children('td').eq(0).html();
        // 每行
        var trItemName = '';
        // 商品分类的名字数组没去重
        var menuGoodsNameAllArr = [];

     
        trS.each(function (index, val) {
           var tr = $(val);
           var trGoodName = tr.children('td').eq(0).html();
           menuGoodsNameAllArr.push(trGoodName);
        });
    
        // 商品分类的名字数组没去重
        var menuGoodsNameArr = uniq(menuGoodsNameAllArr);
        
        // 分类的商品数组
        var menuGoodsArr = [];

   
        try {
            menuGoodsNameArr.forEach(function (kindName, kindIndex) {
               var goodItem = {
                  "class_name": kindName,
                  "data": []
               }
               // 每行
               var itemTr = null;
               // 每行类的名字
               var trKindName = null;
               // 每行商品的名字
               var goodName = null;
               // 每行商品的数量
               var count = null;
               // 每行商品的价格
               var price = null;
             
              
               trS.each(function (indexTr, itemTr) {
                  itemTr = $(itemTr);
                  trKindName = itemTr.children('td').eq(0).html();
                  goodName =  itemTr.children('td').eq(1).html();
                  count =  itemTr.children('td').eq(2).find(".tab_val").val();
                  price = itemTr.children('td').eq(3).find(".tab_val").val();
                  if (!count) {
                      alert('商品数量不能空');
                      foreach.break = new Error("StopIteration");
                      return false;
                  }
                  if (!price) {
                      alert('商品价格不能空');
                      foreach.break = new Error("StopIteration");
                      return false;
                  }
                  if (kindName == trKindName) {
                     goodItem.data.push({
                         name: goodName,
                         num: count,
                         money: price
                     })
                  }
               });
               
               menuGoodsArr.push(goodItem);
            })
        } catch (e) {
            if(e.message==="foreach is not defined") {
                return;
            } else {
                throw e;
            }
        }

        if (menuGoodsArr.length > 0) {
            return menuGoodsArr;
        } else {
            return 0;
        }
    }



    // 图片上传
    $(".uploadimg_con_list").on('change', '.upload_img_btn', function () {
          var $this = $(this);
          var file = $this[0].files[0];
          var formData = new FormData();
          formData.append('login_type', 2);
          formData.append('stores_id', stores_id);
          formData.append('oauth_token', oauth_token);
          formData.append('imageFile',  file);
          $.ajax({
            url: uploadImgUrl,
            type: "POST",
            data:  formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (res) {
              var resData =  eval("(" + res + ")");
              $this.siblings(".uploadimg_src").attr("src", resData.url);
              $this.attr('picId', resData.pictureId)
            }
          })
    })





    var selectImgType = 1;
    // 商品图片展示类型
    $(".select_img_btn").find('.layui-unselect').click(function () {
        var $this = $(this);
        var radioInput = $this.prev('input');
        var index = parseInt(radioInput.data('id'));
        selectImgType = index + 1;
        $(".select_img_type").hide();
        $('.select_img_type').eq(index).show();
    });



        // 商品上传
        $(".sub_good_btn").click(function () {
            var title = $(".good_title").val();
        
            if (!title) {
               layer.msg('商品标题不能空');
               return false;
            }

            // 副标题
            var smallTitle = $(".small_title").val();

            if (!smallTitle) {
               layer.msg('商品副标题不能空');
               return false;
            }

              // 商品编码
            var goodsCode = $(".goods_code").val();
           

            // 商品数量价格目录
            var menuArr = menuList();
            if (!menuArr) {
              layer.msg('请生成套餐');
              return false;
            }
            var menuArrStr = JSON.stringify(menuArr);
          
            // 5折状态
            var label = 12;

            
            // 库存总量
            var stock_qty = $(".stock_qty").val();
        
            
            if (!stock_qty) {
               layer.msg('库存总量不能空');
               return false;
            }

            // 门店价
            var goodPrice = 0; 
            // 会员价
            var vip_price = 0;
            // 贝壳
            var beike_credit = 0;

            if (activeStatus == '是') {
                if (activeItemStatus == 'half') {
                    // 超划算
                    label = 12;
                    vip_price = $(".pay_count").val();
                } else if (activeItemStatus == 'shell') {
                    // 天天抢
                        label = 11;
                        vip_price = 0;
                }
            } else if (activeStatus == '否') {
                    label = 0;
                    activeItemStatus = ' ';
                    vip_price = $(".pay_count").val();
            }
            goodPrice = $(".price").val();
            beike_credit = $(".shell_count").val();
            
            var beat = $(".beat").find(".layui-anim-upbit .layui-this").attr('lay-value');

            if (!goodPrice) {
               layer.msg('门店价不能空');
               return false;
            }
    
            if (activeItemStatus != 'shell') {
              if (!vip_price) {
                 layer.msg('会员价不能空');
                 return false;
              }
            }
        
            if (!beike_credit) {
               layer.msg('贝壳个数不能空');
               return false;
            }

            // 贝壳券有效期
            var sellDate = $("#test15").val().split("到");
            var sellDateStartTime = sellDate[0];
            var sellDateEndTime = sellDate[1];
        
            if (!sellDateStartTime) {
              layer.msg("请选择贝壳券有效期");
              return false;
            }
        
            // 使用时间                                                                      
            var ticketUseDate = $.trim($(".ticket_use_date").find('.layui-this').html());
            var ticketUseTime = $.trim($("#test16").val());
            var ticketUseTimeArr = ticketUseTime.split('-');
            var ticketUseTime1 = $.trim(ticketUseTimeArr[0]);
            var ticketUseTime2 = $.trim(ticketUseTimeArr[1]);
            var ticketUseTimeArrStr = ticketUseTime1 + '-' + ticketUseTime2;
            var ticketUseTimeStr = ticketUseDate + ' ' + ticketUseTimeArrStr;
            
            if (ticketUseDate == '请选择') {
               layer.msg("请选择使用时间")
               return false;
            }

            if (!ticketUseTime) {
              layer.msg('请选择使用时间范围');
              return false;
            }

            // 使用规则           
            var ticketUseCount = JSON.parse($(".ticket_use_count").find(".layui-this").attr("lay-value"));
            if (!ticketUseCount) {
                layer.msg('请选择使用规则');
                return false;
            }

            // 票信息
            var use_bill = JSON.parse($(".use_bill").find(".layui-this").attr("lay-value"));
            if (use_bill < 0) {
              layer.msg('请选择是否开发票');
              return false;
            }

            // 其它事项 li
            var otherLiS = $(".other_list").find("li");
            var otherArr = [];
            otherLiS.each(function (index, val) {
               otherArr[index] = $(val).find("input").val();
            });
            var otherArrStr = JSON.stringify(otherArr);
         

            // 商家服务       
            var shopServeSelectCountArr = [];
            $(".drawback_select_list").find(".layui-unselect").each(function (indexDrawback, itemDrawback) {
               var itemBac = $(itemDrawback);
               if (itemBac.hasClass("layui-form-checked")) {
                  shopServeSelectCountArr.push(itemBac.prev("input").data('index'));
               }
            });
            var shopServeSelectCountArrStr = shopServeSelectCountArr.join(',');
     
         
            // 备注
            var remark = $(".remark").val();

            // 排序
            var sortStatus = $(".select_order_btn").find(".layui-form-radioed").find("div").html();
            var sort = 0;
            if (sortStatus == '是') {
              sort = $(".order_val").val();
            } else {
              sort = 0;
            }

         
            var imgArr = [];
            var imgArrStr = null;


            // 配置图片数据
            $(".upload_img_btn").each(function (index, val) {
              var $this = $(val);
              var path_id = $this.attr("picid");
              if (index == 0) {
                if (path_id) {
                  imgArr.push({
                    title: "封面图",
                    path_id: path_id,
                    type: selectImgType
                  })
                }
              } else {
                if (path_id) {
                  imgArr.push({
                    title: "缩略图",
                    path_id: path_id,
                    type: selectImgType
                  })
                }
              }
            });

            if (imgArr.length < 3) {
               layer.msg('至少上传3张商品图片');
               return false;
            }
            imgArrStr = JSON.stringify(imgArr);


            var imgDetailArr = [{"path_id":"bab92555d94948e2985bcd84e2d4a18e","title":""}];
            var imgDetailArrStr = JSON.stringify(imgDetailArr);

            $.ajax({
                url: requestUrl.merchandise_control.merchandise_warehouse_edit.edit,
                type: 'POST',
                data: {
                    login_type: 2,
                    stores_id: stores_id,
                    goods_id: goodId,
                    oauth_token: oauth_token,
                    goods_code: goodsCode,
                    title: title,
                    desc1: smallTitle,
                    brand_id: '',
                    label: label,
                    label_promotion: 'aaa',
                    pic_info: imgArrStr,
                    pic_detail_info: imgDetailArrStr,
                    specification: menuArrStr,
                    sale_price: '100',
                    stock_qty: stock_qty,
                    remark: remark,
                    // sale_price: price,
                    sale_price: goodPrice,
                    vip_price: vip_price,
                    beike_credit: beike_credit,
                    service_level: shopServeSelectCountArrStr,
                    status: selectGroundingStatus,
                    activity_start_time: sellDateStartTime,
                    ativity_end_time: sellDateEndTime,
                    use_time: ticketUseTimeStr,
                    max_count: ticketUseCount,
                    is_offer_invoice: use_bill,
                    rest_remark: otherArrStr,
                    discount_rate: beat
                    // is_recommend: orderStatus,
                    // sort: sort
                },
                success: function (res) {
                  var resData =  eval("(" + res + ")");
                  layer.msg(resData.msg);
                  setTimeout(function () {
                        if (resData.code == 0) {
                             //window.location.href = "./merchandise_warehouse.html";
                        }
                  }, 3000)
                }
            });
        });
	exports('merchandise_control/merchandise_warehouse_edit', {})
})