package com.imooc.bilibili.service.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RequestPaasHelper {

    /**
     * paas服务地址
     */
    @Value("${pass.url:https://mdp-sit.anker-in.com}")
    private String url;
    @Value("${pass.token:da5decad-aef4-11e9-838d-0050569d2fdf}")
    private String token;


    private String testUrl;

    private List<Company> nameList;

    /**
     * 获取单据编码
     *
     * @param id 单据id
     * @return
     */
    public String getCode(String id) {
        String path = url + "/service/seq/doc/seqs/" + id;
        String method = "GET";
        Result request = request(path, this.token, method, null);
        if (request.isHasErrors()) {
            log.error("/service/seq/doc/seqs/{}获取编码出错", id);
            return null;
        }
        return request.getData().toString();
    }

    public Result request(String url, String token, String method, RequestBody body) {
        Result result = new Result();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("x-service-token", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            result = JSON.toJavaObject(JSON.parseObject(response.body().string()), Result.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public List<String> getArrayList() {
        String filePath = "/Users/soxhwhat/IdeaProjects/soxhwhat-bilibili/imooc-bilibili-service/src/main/java/com/imooc/bilibili/service/excel/test.csv";
        return readCsvToList(filePath);
    }

    private List<String> readCsvToList(String filePath) {
        List<String> dataList = new ArrayList<>();

        CsvReader reader = CsvUtil.getReader();
//从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(filePath));
        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            dataList.add(csvRow.getRawList().get(0));

        }
        return dataList;

    }

//    public static void main(String[] args) {
//        RequestPaasHelper requestPaasHelper = new RequestPaasHelper();
//        requestPaasHelper.getArrayList();
//        System.out.println(requestPaasHelper.nameList);
//    }


}





