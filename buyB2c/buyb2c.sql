/*
Navicat MySQL Data Transfer

Source Server         : 百事贝测试
Source Server Version : 50621
Source Host           : 120.76.250.165:6953
Source Database       : buyb2c

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2018-03-23 15:16:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
  `admin_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT '',
  `password` varchar(255) DEFAULT '',
  `rand` varchar(255) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` enum('1','2') DEFAULT '1' COMMENT '账户状态 1可用 2不可用',
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `user_name` (`user_name`),
  UNIQUE KEY `phone` (`phone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_advert_position
-- ----------------------------
DROP TABLE IF EXISTS `t_advert_position`;
CREATE TABLE `t_advert_position` (
  `advert_position_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `advert_name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_show` enum('1','2') DEFAULT '1' COMMENT '是否启用 1是2 不是',
  `admin_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`advert_position_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_advert_position_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_advert_position_detail`;
CREATE TABLE `t_advert_position_detail` (
  `advert_position_detail_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `image` varchar(255) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `advert_position_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`advert_position_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_area
-- ----------------------------
DROP TABLE IF EXISTS `t_area`;
CREATE TABLE `t_area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `area_id` int(20) NOT NULL,
  `area` varchar(50) NOT NULL,
  `city_id` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3148 DEFAULT CHARSET=utf8 COMMENT='行政区域县区信息表';

-- ----------------------------
-- Table structure for t_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_attribute`;
CREATE TABLE `t_attribute` (
  `attribute_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(255) DEFAULT NULL,
  `attribute_type` enum('1','2') DEFAULT '1' COMMENT '属性类型 1 复选框 2文本框',
  `admin_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remarks` varchar(30) DEFAULT '' COMMENT '属性备注',
  PRIMARY KEY (`attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='属性表';

-- ----------------------------
-- Table structure for t_attribute_type
-- ----------------------------
DROP TABLE IF EXISTS `t_attribute_type`;
CREATE TABLE `t_attribute_type` (
  `attribute_type_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `attribute_type_name` varchar(255) DEFAULT '',
  `attribute_type_level` enum('1','2','3') DEFAULT NULL COMMENT '属性分类级别 1 大类 2中类 3小类',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `admin_id` int(11) DEFAULT '0',
  `parent_id` int(11) DEFAULT '0' COMMENT '上级分类id',
  `logo` varchar(255) DEFAULT NULL,
  `sort` tinyint(255) DEFAULT NULL,
  `parent_name` varchar(255) DEFAULT '' COMMENT '上级分类名称',
  `grand_parent_id` int(11) DEFAULT '0' COMMENT '上上级分类(为了在属性分类与属性中间表存储,用来删除分类时查找该分类是否被用)',
  `grand_parent_name` varchar(255) DEFAULT '' COMMENT '上上级分类名称',
  PRIMARY KEY (`attribute_type_id`),
  UNIQUE KEY `sort` (`admin_id`,`sort`,`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8 COMMENT='属性分类';

-- ----------------------------
-- Table structure for t_attribute_type_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_attribute_type_attribute`;
CREATE TABLE `t_attribute_type_attribute` (
  `attribute_type_attribute_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attribute_type_id` int(11) DEFAULT '0' COMMENT '属性分类id',
  `attribute_id` int(11) DEFAULT '0',
  `attribute_type_parent_id` int(11) DEFAULT '0' COMMENT '属性分类二级id',
  `attribute_type_grand_parent_id` int(11) DEFAULT '0' COMMENT '属性分类一级id',
  `create_time` datetime DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`attribute_type_attribute_id`),
  UNIQUE KEY `attribute_type_id` (`attribute_type_id`,`attribute_id`) USING BTREE COMMENT '属性分类和属性联合唯一'
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8 COMMENT='属性分类(小类)和属性的中间表';

-- ----------------------------
-- Table structure for t_attribute_val
-- ----------------------------
DROP TABLE IF EXISTS `t_attribute_val`;
CREATE TABLE `t_attribute_val` (
  `attribute_val_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attribute_val` varchar(255) DEFAULT '' COMMENT '属性值',
  `attribute_id` int(11) DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`attribute_val_id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_city
-- ----------------------------
DROP TABLE IF EXISTS `t_city`;
CREATE TABLE `t_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `city_id` int(20) NOT NULL,
  `city` varchar(50) NOT NULL,
  `province_id` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=402 DEFAULT CHARSET=utf8 COMMENT='行政区域地州市信息表';

-- ----------------------------
-- Table structure for t_collection
-- ----------------------------
DROP TABLE IF EXISTS `t_collection`;
CREATE TABLE `t_collection` (
  `collection_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL,
  `customer_id` int(11) DEFAULT '0' COMMENT '用户id(t_user表的userId)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`collection_id`),
  UNIQUE KEY `goods_id` (`goods_id`,`customer_id`) USING BTREE COMMENT '每个客户对一个商品的同一属性只能添加一条数据'
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_column
-- ----------------------------
DROP TABLE IF EXISTS `t_column`;
CREATE TABLE `t_column` (
  `column_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `column_name` varchar(255) DEFAULT '' COMMENT '栏目名称',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sort` tinyint(4) DEFAULT '1' COMMENT '排序',
  `admin_id` int(11) DEFAULT NULL,
  `goods_sort` enum('1','2','3') DEFAULT '1' COMMENT '1 上架时间降序 2 商品价格降序 3 商品价格升序',
  `show_goods_num` tinyint(11) DEFAULT '0' COMMENT '首页显示商品数',
  `column_logo` varchar(255) DEFAULT '',
  `is_recommend_index` enum('1','2') DEFAULT '1' COMMENT '是否推荐到首页 1 是 2 不是',
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  PRIMARY KEY (`column_id`),
  UNIQUE KEY `sort` (`sort`,`admin_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='栏目';

-- ----------------------------
-- Table structure for t_column_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_column_goods`;
CREATE TABLE `t_column_goods` (
  `column_goods_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `column_id` int(11) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`column_goods_id`),
  UNIQUE KEY `column_id` (`column_id`,`goods_id`) USING BTREE COMMENT '栏目和商品唯一'
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8 COMMENT='栏目商品中间表';

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `comment_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT '0',
  `goods_no` varchar(11) DEFAULT '' COMMENT '商品编号',
  `goods_image` varchar(255) DEFAULT NULL,
  `goods_name` varchar(255) DEFAULT NULL,
  `goods_num` int(11) DEFAULT NULL,
  `goods_price` decimal(10,2) DEFAULT NULL,
  `goods_total_amount` decimal(10,2) DEFAULT NULL,
  `goods_attribute` varchar(255) DEFAULT '' COMMENT '商品属性',
  `comment_type` enum('1','2','3') DEFAULT '1' COMMENT '1 好评 2中评 3差评(1-3差评,4中评,5好评)',
  `comment_image` varchar(255) DEFAULT '' COMMENT '评价图片',
  `comment_content` varchar(255) DEFAULT '',
  `comment_count` tinyint(4) DEFAULT '0' COMMENT '评论的星星数',
  `customer_id` int(11) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT '',
  `customer_phone` varchar(255) DEFAULT NULL,
  `order_detail_id` int(11) DEFAULT '0',
  `order_id` int(11) DEFAULT '0',
  `order_no` varchar(11) DEFAULT NULL,
  `order_create_time` datetime DEFAULT NULL COMMENT '订单下单时间',
  `create_time` datetime DEFAULT NULL COMMENT '评价时间',
  `is_show` enum('1','2') DEFAULT '2' COMMENT '是否显示 1是 2否',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COMMENT='评论表';

-- ----------------------------
-- Table structure for t_comment_state
-- ----------------------------
DROP TABLE IF EXISTS `t_comment_state`;
CREATE TABLE `t_comment_state` (
  `comment_state_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `state` enum('1','2','3') DEFAULT NULL,
  PRIMARY KEY (`comment_state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_content
-- ----------------------------
DROP TABLE IF EXISTS `t_content`;
CREATE TABLE `t_content` (
  `content_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `create_time` datetime DEFAULT NULL,
  `type` enum('1','2') DEFAULT '1' COMMENT '类型:1注册协议 2 售后服务',
  `update_time` datetime DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `admin_id` int(11) DEFAULT '0' COMMENT '操作者 id',
  PRIMARY KEY (`content_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='静态内容(注册协议，售后服务之类的)';

-- ----------------------------
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods` (
  `goods_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `platform_type_id` int(11) DEFAULT '0' COMMENT '平台分类id',
  `platform_type_parent_id` int(11) DEFAULT '0' COMMENT '平台分类父级id',
  `platform_type_grand_parent_id` int(11) DEFAULT '0' COMMENT '平台分类祖父id',
  `good_type_id` int(11) DEFAULT '0' COMMENT '商品分类id',
  `good_type_parent_id` int(11) DEFAULT '0',
  `good_type_grand_parent_id` int(11) DEFAULT '0',
  `goods_name` varchar(255) DEFAULT '' COMMENT '商品名称',
  `goods_no` varchar(255) DEFAULT '' COMMENT '商品编码',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_image_urls` varchar(255) DEFAULT '' COMMENT '商品图片url(多张拼接)',
  `goods_detail` longtext COMMENT '商品描述',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `sale_state` enum('1','2') DEFAULT '2' COMMENT '1 上架  2下架',
  `up_sale_time` datetime DEFAULT NULL COMMENT '上架时间',
  `main_image` varchar(255) DEFAULT NULL,
  `pinyin_goods_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_goods_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_goods_attribute`;
CREATE TABLE `t_goods_attribute` (
  `goods_attribute_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL,
  `attribute_id` int(11) DEFAULT '0' COMMENT '属性库里的属性id',
  `goods_attribute_type` enum('1','2') DEFAULT '1' COMMENT '属性类型 1 复选框 2文本框',
  `goods_attribute_val` varchar(255) DEFAULT '' COMMENT '属性类型是2 文本框时的值',
  PRIMARY KEY (`goods_attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8 COMMENT='商品属性表';

-- ----------------------------
-- Table structure for t_goods_attribute_val
-- ----------------------------
DROP TABLE IF EXISTS `t_goods_attribute_val`;
CREATE TABLE `t_goods_attribute_val` (
  `goods_attribute_val_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `attribute_val_id` int(11) DEFAULT '0' COMMENT '属性库里的属性值id',
  `goods_attribute_id` int(11) DEFAULT '0' COMMENT '商品属性id',
  PRIMARY KEY (`goods_attribute_val_id`)
) ENGINE=InnoDB AUTO_INCREMENT=464 DEFAULT CHARSET=utf8 COMMENT='商品属性值表';

-- ----------------------------
-- Table structure for t_goods_type
-- ----------------------------
DROP TABLE IF EXISTS `t_goods_type`;
CREATE TABLE `t_goods_type` (
  `goods_type_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `goods_type` varchar(255) DEFAULT '' COMMENT '商品分类名称',
  `goods_type_level` enum('1','2','3') DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `admin_id` int(11) DEFAULT '0',
  `parent_id` int(11) DEFAULT '0' COMMENT '上级分类id',
  `logo` varchar(255) DEFAULT NULL,
  `sort` tinyint(255) DEFAULT NULL,
  `parent_name` varchar(255) DEFAULT '' COMMENT '上级分类名称',
  `grand_parent_id` int(11) DEFAULT '0' COMMENT '上上级分类(为了在商品表存储,用来删除分类时查找该分类是否被用)',
  `grand_parent_name` varchar(255) DEFAULT '' COMMENT '上上级分类名称',
  PRIMARY KEY (`goods_type_id`),
  UNIQUE KEY `sort` (`admin_id`,`sort`,`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=328 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_logistics
-- ----------------------------
DROP TABLE IF EXISTS `t_logistics`;
CREATE TABLE `t_logistics` (
  `logistics_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `logistics_name` varchar(255) DEFAULT '' COMMENT '物流名称',
  `logistics_cost` decimal(11,2) DEFAULT '0.00' COMMENT '物流费用',
  `status` enum('1','2') DEFAULT '1' COMMENT '状态 1 开启 2 关闭',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `admin_id` int(11) DEFAULT '0' COMMENT '添加者id',
  PRIMARY KEY (`logistics_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_no` varchar(30) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `pay_state` enum('1','2','3') DEFAULT '1' COMMENT '支付状态 1 未付款 2已付款 3已退款',
  `order_state` enum('1','2','3','4','5','6','7') DEFAULT '1' COMMENT '订单状态 1待付款 2 待发货 3 待收货 4 待评价 5已完成 6平台关闭  7 取消订单',
  `pay_type` enum('1','2','0') DEFAULT '0' COMMENT '支付方式 1 微信 2 支付宝',
  `freight` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `order_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单金额',
  `order_total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额 (订单金额+运费)',
  `customer_id` int(11) DEFAULT '0' COMMENT '用户id(t_user表的userId)',
  `receipt_address_id` int(11) DEFAULT '0' COMMENT '收货地址id',
  `customer_name` varchar(255) DEFAULT '' COMMENT '客户名称',
  `customer_phone` varchar(255) DEFAULT '' COMMENT '客户手机号',
  `customer_address` varchar(255) DEFAULT '' COMMENT '客户收货地址',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `customer_feedback` varchar(255) DEFAULT '' COMMENT '客户留言',
  `goods_pieces` tinyint(4) DEFAULT '0' COMMENT '商品件数',
  `goods_num` tinyint(4) DEFAULT '0' COMMENT '商品种数',
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `logistics_id` int(11) DEFAULT '0' COMMENT '物流id',
  `send_logistics_name` varchar(255) DEFAULT '' COMMENT '发货时选择的物流名称',
  `send_logistics_id` int(11) DEFAULT NULL COMMENT '发货时选择的物流id',
  `logistics_name` varchar(255) DEFAULT '' COMMENT '物流名称',
  `logistics_no` varchar(255) DEFAULT '' COMMENT '物流单号',
  `send_goods_time` datetime DEFAULT NULL COMMENT '发货时间',
  `transaction_id` varchar(30) DEFAULT '' COMMENT '支付方(支付宝或者微信)的支付id',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_order_attribute`;
CREATE TABLE `t_order_attribute` (
  `order_attribute_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  `attribute_id` int(11) DEFAULT NULL,
  `attribute_val_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `customer_id` int(11) DEFAULT '0' COMMENT '用户id',
  PRIMARY KEY (`order_attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='客户下单选择的商品属性';

-- ----------------------------
-- Table structure for t_order_count_state
-- ----------------------------
DROP TABLE IF EXISTS `t_order_count_state`;
CREATE TABLE `t_order_count_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state` enum('2','3','5') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_order_detail`;
CREATE TABLE `t_order_detail` (
  `order_detail_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT '0',
  `goods_id` int(11) DEFAULT '0',
  `goods_no` varchar(11) DEFAULT '' COMMENT '商品编码',
  `good_type_id` int(11) DEFAULT '0' COMMENT '商品分类id',
  `good_type_parent_id` int(11) DEFAULT '0',
  `good_type_grand_parent_id` int(11) DEFAULT '0',
  `goods_name` varchar(255) DEFAULT '',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `goods_num` tinyint(4) DEFAULT '0' COMMENT '商品件数',
  `goods_total_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品总价',
  `goods_image` varchar(255) DEFAULT '' COMMENT '商品主图',
  `goods_attribute` varchar(255) DEFAULT '' COMMENT '买家选择的商品属性值(多个用逗号隔开)',
  `goods_state` enum('1','2','3','0') DEFAULT '0' COMMENT '商品状态 1退款中 2已退款 3撤销退款申请 0正常状态',
  PRIMARY KEY (`order_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_state
-- ----------------------------
DROP TABLE IF EXISTS `t_order_state`;
CREATE TABLE `t_order_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `state` enum('1','2','3','4') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_province
-- ----------------------------
DROP TABLE IF EXISTS `t_province`;
CREATE TABLE `t_province` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `province_id` int(20) NOT NULL,
  `province` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='省份信息表';

-- ----------------------------
-- Table structure for t_receipt_address
-- ----------------------------
DROP TABLE IF EXISTS `t_receipt_address`;
CREATE TABLE `t_receipt_address` (
  `receipt_address_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `province_id` int(11) DEFAULT NULL,
  `province_name` varchar(255) DEFAULT '',
  `city_id` int(11) DEFAULT NULL,
  `city_name` varchar(255) DEFAULT '',
  `county_id` int(11) DEFAULT NULL,
  `county_name` varchar(255) DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT '',
  `default_address` enum('1','2') DEFAULT '2' COMMENT '默认地址 1 是 2 否',
  `customer_id` int(11) DEFAULT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`receipt_address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_refund_order
-- ----------------------------
DROP TABLE IF EXISTS `t_refund_order`;
CREATE TABLE `t_refund_order` (
  `refund_order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `refund_order_no` varchar(30) DEFAULT '' COMMENT '退款单号',
  `order_no` varchar(30) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `order_detail_id` int(11) DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_price` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额(后台最终退款金额)',
  `refund_freight_price` decimal(10,2) DEFAULT NULL COMMENT '运费',
  `reund_goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品总价',
  `refund_apply_price` decimal(10,2) DEFAULT '0.00' COMMENT '退款申请金额(商品总价加运费)',
  `refund_apply_time` datetime DEFAULT NULL COMMENT '退款申请时间',
  `refund_num` int(11) DEFAULT NULL COMMENT '退款申请件数',
  `refund_reason` enum('1','2','3','4','5','6') DEFAULT NULL COMMENT '退款原因 1 订单不能按预计时间送达 2操作有误 3 重复下单 4其他渠道价格更低 5不想买了 6其他原因',
  `refund_remarks` varchar(255) DEFAULT NULL COMMENT '退款备注说明',
  `goods_id` int(11) DEFAULT NULL,
  `goods_no` varchar(11) DEFAULT '' COMMENT '商品编码',
  `goods_name` varchar(255) DEFAULT '',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `good_num` tinyint(4) DEFAULT '0' COMMENT '商品件数',
  `goods_image` varchar(255) DEFAULT '' COMMENT '商品主图',
  `goods_attribute` varchar(255) DEFAULT '' COMMENT '买家选择的商品属性值(多个用逗号隔开)',
  `refund_state` enum('1','2','3') DEFAULT '1' COMMENT '状态 1退款中 2已退款 3撤销退款申请',
  `customer_id` int(11) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT '' COMMENT '客户名称',
  `customer_phone` varchar(255) DEFAULT '' COMMENT '客户手机号',
  `customer_address` varchar(255) DEFAULT '' COMMENT '客户收货地址',
  `send_state` enum('1','2') DEFAULT '1' COMMENT '发货状态 1未发货 2已发货',
  `image` varchar(255) DEFAULT '' COMMENT '问题拍照',
  PRIMARY KEY (`refund_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COMMENT='退款单';

-- ----------------------------
-- Table structure for t_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_shop`;
CREATE TABLE `t_shop` (
  `shop_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) DEFAULT '',
  `shop_name` varchar(255) DEFAULT '' COMMENT '网店名称',
  `shop_domain` varchar(255) DEFAULT '' COMMENT '网店域名',
  `copyright` varchar(255) DEFAULT '' COMMENT '版权信息',
  `customer_phone` varchar(255) DEFAULT '' COMMENT '客服电话',
  `admin_id` int(11) DEFAULT '0' COMMENT '添加者 id ',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_state` enum('1','2') DEFAULT '2' COMMENT '是否删除 1是 2否',
  `free_shipping` int(11) DEFAULT '0' COMMENT '免邮金额标准点',
  `free_shipping_prompt` int(255) DEFAULT '0' COMMENT '免邮金额提示点',
  PRIMARY KEY (`shop_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='网店';

-- ----------------------------
-- Table structure for t_shop_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_shop_cart`;
CREATE TABLE `t_shop_cart` (
  `shop_cart_id` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL,
  `goods_num` tinyint(4) DEFAULT '0' COMMENT '商品件数',
  `customer_id` int(11) DEFAULT '0' COMMENT '用户id(t_user表的userId)',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`shop_cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8 COMMENT='购物车';

-- ----------------------------
-- Table structure for t_shop_cart_attribute
-- ----------------------------
DROP TABLE IF EXISTS `t_shop_cart_attribute`;
CREATE TABLE `t_shop_cart_attribute` (
  `shop_cart_attribute_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `shop_cart_id` int(11) NOT NULL,
  `goods_attribute_id` int(11) DEFAULT NULL COMMENT '属性id',
  `goods_attribute_val_id` int(11) DEFAULT NULL COMMENT '属性值id',
  `goods_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`shop_cart_attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COMMENT='购物车选中的商品属性';

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT '' COMMENT '会员姓名',
  `sex` enum('1','2','0') DEFAULT '0' COMMENT '性别 0未知  1 男 2 女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `phone` varchar(255) NOT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `open_id` varchar(50) DEFAULT '',
  `province_id` int(11) DEFAULT NULL,
  `city_id` int(11) DEFAULT NULL,
  `county_id` int(11) DEFAULT NULL,
  `disable` enum('1','2') DEFAULT '2' COMMENT '1 禁用 2 不禁用',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
