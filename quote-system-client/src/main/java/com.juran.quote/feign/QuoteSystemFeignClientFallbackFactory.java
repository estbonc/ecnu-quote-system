package com.juran.quote.feign;

import com.alibaba.fastjson.JSONObject;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.request.*;
import com.juran.quote.bean.response.GetQuoteSummaryRespBean;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by dell on 2017/9/5.
 */
@Component
public class QuoteSystemFeignClientFallbackFactory implements FallbackFactory<QuoteSystemFeignClient> {

    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @Override
    public QuoteSystemFeignClient create(Throwable throwable) {

        JSONObject obj = new JSONObject();
        obj.put("success", Boolean.FALSE);
        obj.put("message", "报价系统服务开小差啦！");

        QuoteSystemFeignClient client = new QuoteSystemFeignClient() {

            @Override
            public JSONObject createPackage(@RequestBody PackageRequestBean source) {
                logger.info("createPackage熔断");
                return obj;
            }

            @Override
            public JSONObject createVersion(@RequestBody PackageVersionRequestBean source) {
                throwable.printStackTrace();
                logger.info("createVersion熔断");
                return obj;
            }

            @Override
            public JSONObject createPrice(@RequestBody PackagePriceRequestBean source) {
                logger.info("createPrice熔断");
                return obj;
            }

            @Override
            public JSONObject createRoom(@RequestBody PackageRoomRequestBean source) {
                logger.info("createRoom熔断");
                throwable.printStackTrace();
                return obj;
            }

            @Override
            public JSONObject createConstruct(@RequestBody ConstructBean source) {
                logger.info("createConstruct熔断");
                return obj;
            }

            @Override
            public JSONObject createPackageConstruct(@RequestBody PackageConstructRequestBean source) {
                logger.info("createPackageConstruct熔断");
                return obj;
            }

            @Override
            public JSONObject deleteConstruct(String ids) {
                logger.info("deleteConstruct熔断");
                return obj;
            }

            @Override
            public JSONObject createPackageBag(PackageBagRequestBean source) {
                logger.info("createPackageBag熔断");
                return obj;
            }

            @Override
            public GetQuoteSummaryRespBean getQuoteSummaryInfo(Long quoteId) {
                return null;
            }

            @Override
            public JSONObject createMaterial(MaterialRequestBean source) {
                logger.info("createMaterial熔断");
                return obj;
            }

            @Override
            public JSONObject querySingleRoom(PackageRoomRequestBean source) {
                logger.info("querySingleRoom熔断");
                return obj;
            }

            @Override
            public JSONObject createBagConstruct(BagConstructRequestBean requestBean) {
                logger.info("createBagConstruct熔断");
                return obj;
            }

            @Override
            public JSONObject startQuote(CreateQuoteRequestBean requestBean) {
                logger.info("startQuote熔断");
                return obj;
            }


        };
        return client;
    }
}
