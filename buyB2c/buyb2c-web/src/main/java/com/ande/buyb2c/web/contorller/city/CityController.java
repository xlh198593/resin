package com.ande.buyb2c.web.contorller.city;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.city.entity.Area;
import com.ande.buyb2c.common.city.entity.City;
import com.ande.buyb2c.common.city.entity.Province;
import com.ande.buyb2c.common.city.service.IAreaService;
import com.ande.buyb2c.common.city.service.ICityService;
import com.ande.buyb2c.common.city.service.IProvinceService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.web.jms.JMSProducerUtil;

/**
 * @author chengzb
 * @version 2017年9月13日下午12:22:19
 */
@RestController
@RequestMapping("/city")
public class CityController extends AbstractController {
	@Resource
	private IProvinceService provinceService;
	@Resource
	private ICityService cityService;
	@Resource
	private IAreaService areaService;
	@Resource
	JMSProducerUtil producerUtil;

	@RequestMapping("/test")
	public void test() {
		producerUtil.sendDelayMessage(1 + "", "order.delay.cancelOrder", Long.valueOf(1000 * 10));
	}

	@RequestMapping("/getProvince")
	@Cacheable("province")
	public JsonResponse<Province> getProvince() {
		JsonResponse<Province> result = new JsonResponse<Province>();
		result.setRes(SystemCode.SUCCESS.getCode());
		result.setResult(SystemCode.SUCCESS.getMsg());
		result.setList(provinceService.getAll());
		return result;
	}

	@RequestMapping("/getCity")
	@Cacheable(value = "city")
	public JsonResponse<City> getCity(Integer provinceId) {
		JsonResponse<City> result = new JsonResponse<City>();
		result.setRes(SystemCode.SUCCESS.getCode());
		result.setResult(SystemCode.SUCCESS.getMsg());
		City city = new City();
		city.setProvinceId(provinceId);
		result.setList(cityService.getAllBySelect(city));
		return result;
	}

	@RequestMapping("/getArea")
	@Cacheable(value = "area")
	public JsonResponse<Area> getArea(Integer cityId) {
		JsonResponse<Area> result = new JsonResponse<Area>();
		result.setRes(SystemCode.SUCCESS.getCode());
		result.setResult(SystemCode.SUCCESS.getMsg());
		Area area = new Area();
		area.setCityId(cityId);
		result.setList(areaService.getAllBySelect(area));
		return result;
	}
}
