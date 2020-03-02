package com.ecnu.paper.quotesystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.dto.TaoBaoResponseBody;
import com.juran.quote.bean.dto.TaoBaoResponseData;
import com.juran.quote.bean.dto.TaoBaoResponseModel;
import com.juran.quote.utils.JsonUtility;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AplatformWeakgetRequest;
import com.taobao.api.response.AplatformWeakgetResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaoBaoService {
    protected final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    @Value("${tb.serverUrl}")
    private String serverUrl;

    @Value("${tb.appKey}")
    private String appKey;

    @Value("${tb.appSecret}")
    private String appSecret;


    /**
     * 调用淘宝sdk
     *
     * @param modelId 模型id
     * @return .
     */
    public TaoBaoResponseData getTBDataByModelId(String modelId) {

        try {
            if (StringUtils.isEmpty(modelId)) {
                logger.info("getTBDataByModelId input parameter modelId is null");
                return null;
            }
            TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
            AplatformWeakgetRequest req = new AplatformWeakgetRequest();
            AplatformWeakgetRequest.RichClientInfo obj1 = new AplatformWeakgetRequest.RichClientInfo();
            req.setParamRichClientInfo(obj1);
            AplatformWeakgetRequest.ParamDto obj2 = new AplatformWeakgetRequest.ParamDto();
            obj2.setBizParam("{\"modelId\": " + modelId + "}");
            obj2.setBizType("HomeAI4EasyHome.getRelatedItems");
            req.setParamDto(obj2);
            AplatformWeakgetResponse rsp = client.execute(req, null);
            JSONObject jsonObject = JSON.parseObject(rsp.getBody());
            if(jsonObject.get("aplatform_weakget_response") == null){
                return null;
            }
            String response = jsonObject.get("aplatform_weakget_response").toString();
            TaoBaoResponseBody body = JsonUtility.jsonToObject(response, TaoBaoResponseBody.class);
            TaoBaoResponseModel taoBaoResponseModel = JsonUtility.jsonToObject(body.getResult().getModel(), TaoBaoResponseModel.class);
            TaoBaoResponseData data = taoBaoResponseModel.getRelatedItems().getData();
            logger.info("getTBDataByModelId success modelId: {}, taoBaoResponseData: {}", modelId, data);
            return data;

        } catch (ApiException ex) {
            ex.printStackTrace();
            logger.info("getTBDataByModelId failed ,modelId : ", modelId);
        }

        return null;
    }
}
