layui.define(['layer', 'jquery', 'common/common_b', 'common/url_b', 'laytpl', 'form', "laydate"], function (exports) {
    var layer = layui.layer;
    var $ = layui.jquery;
    var laytpl = layui.laytpl;
    var laydate = layui.laydate;
	var common = layui['common/common_b'];
	var form = layui.form;
	var requestUrl = layui['common/url_b'];

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
    	var goodId = common.getStorage('goodId', 'string');
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
            var uploadImgStr = '';
            var imgIndex = 0;
            for (var i in data.pic_info_new) {
                imgIndex++;
                var imgTxt = imgIndex ==1? '<p class="txt">封面页</p>': '<p class="txt">缩略图</p>';
            	if (data.pic_info_new[i]) {
                 	uploadImgStr += '<div class="uploadimg_con">'+
                                        '<div class="layui-upload-list uploadimg" id="test'+ imgIndex +'">'+
                                            '<img class="layui-upload-img uploadimg_src" id="demo'+ imgIndex +'" width="100%" height="99.5%" src="'+  data.pic_info_new[i] +'">'+
                                        '</div>'+
                                        imgTxt +
                                    '</div>'
            	}
            }

            $(".uploadimg_add_con_btn").before(uploadImgStr);
            // 记录图片个数
            var imgLength = $(".upload_img_btn").length;
            $(".uploadimg_con_list").attr("count", imgLength);

            // 商品图片 
            var imgInfo = eval("("+ data.pic_info +")");
            // 图片样式类型
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
                    setmealStr +=  '<li>'+
										'<div class="grade_1">'+
											'<input type="text" name="" class="layui-input grade_val_1 grade_val" placeholder="输入一级类型名称" value="'+ setmeal[i].class_name +'" disabled="disabled">'+
										'</div>'+
										'<ul class="grade_2" count="1">';
										for (var j=0; j<setmeal[i].data.length; j++) {
											setmealStr +=   '<li>'+
																'<input type="text" name="" class="layui-input grade_val_2 grade_val" disabled="disabled" value="'+ setmeal[i].data[j].name +'">'+
															'</li>'
										}

	                    setmealStr +=   '</ul>'+
									'</li>'				 
                }
                $(".goods_grade_list").html(setmealStr);


                // 商品标题
                for (var i=0; i<setmeal.length; i++) {
                    for (var j=0; j<setmeal[i].data.length; j++) {
                    	if (j == 0) {
                            makeTabStr +=   '<tr>'+
	                                            '<td rowspan="'+ setmeal[i].data.length +'">'+ setmeal[i].class_name +'</td>'+
	                                            '<td>'+ setmeal[i].data[j].name +'</td>'+
	                                            '<td><input type="text" name="" class="layui-input tab_val" value="'+ setmeal[i].data[j].num +'" disabled="disabled"></td>'+
	                                            '<td><input type="text" name="" class="layui-input tab_val" value="'+ setmeal[i].data[j].money +'" disabled="disabled"></td>'+
	                                        '</tr>';
                    	} else {
                    		makeTabStr +=   '<tr>'+
	                                            '<td style="display:none;">'+ setmeal[i].class_name +'</td>'+
	                                            '<td>'+ setmeal[i].data[j].name +'</td>'+
	                                            '<td><input type="text" name="" class="layui-input tab_val" value="'+ setmeal[i].data[j].num +'" disabled="disabled"></td>'+
	                                            '<td><input type="text" name="" class="layui-input tab_val" value="'+ setmeal[i].data[j].money +'" disabled="disabled"></td>'+
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
            $(".beat_val").val(beatVal);


            // 商品有效期
            var activityTime = data.activity_start_time + ' 到 ' + data.ativity_end_time;
            $("#test15").val(activityTime);

            // 使用时间
            var perchase_notice = JSON.parse(data.perchase_notice);
            var useTime = perchase_notice.use_time.split(' ');

            var useTime1 = useTime[0];
            var useTime2 = useTime[1].split('-');
            
            $(".ticket_use_date_val").val(useTime1);

            $("#test16").val(useTime2[0] + ' - ' + useTime2[1]);


            // 使用规则
            var max_count = perchase_notice.max_count;
            var ticket_use_count = $(".ticket_use_count_val");
            if (max_count == 1) {
                ticket_use_count.val('最多可以用一张');
                ticket_use_count.attr('count', 1)
            } else if (max_count == 2) {
                ticket_use_count.val('最多可用两张');
                ticket_use_count.attr('count', 2)
            } else if (max_count == 3) {
                ticket_use_count.val('最多可用三种');
                ticket_use_count.attr('count', 3)
            } else if (max_count == -1) {
                ticket_use_count.val('不限使用张数');
                ticket_use_count.attr('count', -1)
            }


            // 提供发票
            var use_bill = $(".use_bill_val");
            var is_offer_invoice = perchase_notice.is_offer_invoice;
            if (is_offer_invoice == 0) {
                use_bill.val('不提供发票');
            } else if (is_offer_invoice == 1) {
                use_bill.val('可提供发票');
            }
            

            // 其它事项
            var rest_remarkS = JSON.parse(perchase_notice.rest_remark);
            var otherLiStr = '';
            for (var i=0; i<rest_remarkS.length; i++) {
                otherLiStr +=  	'<li>'+
									'<input type="text" name="other" value="'+ rest_remarkS[i] +'" class="layui-input" disabled="disabled">'+
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
           
            // 禁用所有按钮
            $(".layui-form-radio").unbind();
        });
    }


    getGoodDetail()

    // 查看大图
    $(".merchandise_control").on('click', '.layui-upload-img', function () {
         common.showBigImg(this);
    });



	exports("merchandise_control/merchandise_warehouse_detail", {})
})