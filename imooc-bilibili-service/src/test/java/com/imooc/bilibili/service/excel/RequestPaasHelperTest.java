package com.imooc.bilibili.service.excel;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestPaasHelperTest {
    @Autowired
    private RequestPaasHelper requestPaasHelper;

    @Test
    public void getArrayList() {
        requestPaasHelper.getArrayList();
    }
}