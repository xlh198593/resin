	layui.use(['element', 'upload', 'jquery', 'form', 'laydate'], function(){
	    var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
	    var $ = layui.jquery;
	    var upload = layui.upload;
	    var form = layui.form;
	    var laydate = layui.laydate;
       
       
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
            if (firstNameUniq.length == firstNameAll.length) {
               return goodNameDataArr;
            } else {
               alert('套餐类目不能重复');
               return false;
            }
            
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




        // 验证库存总量
        $(".stock_qty").keyup(function () {
           var $this = $(this);
           // 正整数
           var regPrice = /^[1-9]{1,}[\d]*$/;
           var val = $this.val();
           if (!regPrice.test(val)) {
              layer.msg('请输入正整数');
              $this.val('');
              return ;
           }
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

        // 是否排序
        var orderStatus = 1;
        $(".select_order_btn").find(".layui-form-radio").click(function () {
        	var status = $(this).find("div").html();
        	var orderInput = $(".order_input_con").find(".input_val");
        	if (status == '否') {
            orderInput.attr("disabled", "disabled");
            $(".order_val").val('');
            orderStatus = 0;
        	} else {
        		orderInput.removeAttr('disabled');
            $(".order_val").val(60);
            orderStatus = 1;
        	}
        });

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
             	url: addGoodsUrl,
             	type: 'POST',
             	data: {
                    login_type: 2,
             	    	stores_id: stores_id,
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
             	}
            });
        });
	});