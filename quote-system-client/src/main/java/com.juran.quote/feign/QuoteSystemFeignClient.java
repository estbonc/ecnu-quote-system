package com.juran.quote.feign;

import com.alibaba.fastjson.JSONObject;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.GetQuoteSummaryRespBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.PathParam;

/**
 * Created by dell on 2017/9/5.
 */
@FeignClient(name = "quote-system-app", fallbackFactory = QuoteSystemFeignClientFallbackFactory.class, url = "${quote-system-app.feign.url:}")
public interface QuoteSystemFeignClient {


    /**
     * 1创建套餐
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/package", method = RequestMethod.POST)
    JSONObject createPackage(@RequestBody PackageRequestBean source);


    /**
     * 2创建套餐版本
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/package/version", method = RequestMethod.POST)
    JSONObject createVersion(@RequestBody PackageVersionRequestBean source);


    /**
     * 3创建套餐价格
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/package/price", method = RequestMethod.POST)
    JSONObject createPrice(@RequestBody PackagePriceRequestBean source);


    /**
     * 4创建套餐空间
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/package/room", method = RequestMethod.POST)
    JSONObject createRoom(@RequestBody PackageRoomRequestBean source);


    /**
     * 添加施工项
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/construct", method = RequestMethod.POST)
    JSONObject createConstruct(@RequestBody ConstructBean source);

    /**
     * 添加套餐-施工项
     *
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/construct/packageConstruct", method = RequestMethod.POST)
    JSONObject createPackageConstruct(@RequestBody PackageConstructRequestBean source);

    /**
     * 删除施工项
     * @return
     */
    @RequestMapping(path = "/api/v1/construct/{ids}", method = RequestMethod.DELETE)
    JSONObject deleteConstruct(@PathParam("ids") String ids);


    /**
     * 创建套餐礼包
     * @param source
     * @return
     */

    @RequestMapping(path = "/api/v1/package/bag", method = RequestMethod.POST)
    JSONObject createPackageBag(@RequestBody PackageBagRequestBean source);

    /**
     * 获取报价汇总信息
     * @param quoteId
     * @return
     */
    @RequestMapping(path = "/api/v1/quotes/{id}/summary", method = RequestMethod.GET)
    GetQuoteSummaryRespBean getQuoteSummaryInfo(@PathVariable("id") Long quoteId);

    /**
     * 创建主材
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/package/material", method = RequestMethod.POST)
    JSONObject createMaterial(@RequestBody MaterialRequestBean source);

    /**
     * 查询单个空间
     * @param source
     * @return
     */
    @RequestMapping(path = "/api/v1/room/single", method = RequestMethod.POST)
    JSONObject querySingleRoom(@RequestBody PackageRoomRequestBean source);

    @RequestMapping(path = "/api/v1/construct/bagConstruct", method = RequestMethod.POST)
    JSONObject createBagConstruct(@RequestBody BagConstructRequestBean requestBean);

    @RequestMapping(path = "/api/v1/quote", method = RequestMethod.POST)
    JSONObject startQuote(@RequestBody CreateQuoteRequestBean requestBean);
}
