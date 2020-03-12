package com.juran.quote.bean.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class PackageInfoRespBean implements Serializable {
    private static final long serialVersionUID = 7141467801121340533L;

    private String packageId;
    private String packageName;
}
