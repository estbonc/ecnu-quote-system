package com.juran.quote.resource.v1;

import com.juran.core.log.contants.LoggerName;
import com.juran.core.log.eventlog.aop.RestLog;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.bean.response.CommonRespBean;
import com.juran.quote.service.QuoteService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "第三方对接服务")
@RestLog
@Component
@Path("/v1/thirdparty")
@Produces(MediaType.APPLICATION_JSON)
public class ThirdPartyResource {
    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @Autowired
    private QuoteService quoteService;

    @ApiOperation(value = "报价结果接口", notes = "报价结果接口获取报价详情")
    @GET
    @Path("/quote/{designId}")
    public Response getCaseQuotationInfo(@ApiParam(value = "案例id") @PathParam("designId") String designId) {
        try {
            CommonRespBean quoteSummary = quoteService.getCaseQuotation(designId);
            return Response.status(Response.Status.OK).entity(quoteSummary).build();
        } catch (QuoteException e) {
            CommonRespBean resp = new CommonRespBean(new CommonRespBean.Status(CommonRespBean.Status.EXCEPTION, e.getErrorMsg()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }
    }

}
