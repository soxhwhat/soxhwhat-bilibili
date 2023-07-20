package com.imooc.bilibili.service.excel;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HttpRequest {

    RequestPaasHelper requestPaasHelper = new RequestPaasHelper();


    List<String> nameList = new ArrayList<>();


    @Autowired
    private CompanyNameService companyNameService;

    public Result getCompanyDetails() {
        nameList = requestPaasHelper.getArrayList();

        CompanyName companyName = new CompanyName();
        for (String name : nameList) {
            getUrlMap(companyName);
            getDetailMap(companyName);
        }
        companyNameService.save(companyName);
        return Result.success();

    }

    public void getDetailMap(CompanyName name) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.xiniudata.com" + name.getName();

        Request request = new Request.Builder()
                .url(url)
                .header("authority", "www.xiniudata.com")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("cache-control", "max-age=0")
                .header("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; sajssdk_2020_cross_new_user=1; utoken=48WTD9FUEY0QHYPSOIATAR50SSDV3EC4; username=%E6%97%B6%E4%B8%8D%E6%88%91%E4%B8%8E; search=%E7%99%BE%E5%BA%A6%20anker%20anker%20null; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689820329")
                .header("if-none-match", "W/\"32405-tivd4Y1gDRp0H/ohzkZuL8CRpjk\"")
                .header("referer", "https://www.xiniudata.com/search2?name=%E7%99%BE%E5%BA%A6")
                .header("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Microsoft Edge\";v=\"114\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "macOS")
                .header("sec-fetch-dest", "document")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                getDetail(responseData, name);
            } else {
                name.setDetail("请求失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUrlMap(CompanyName name) {

        String url = "https://www.xiniudata.com/search2?name=" + name;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("authority", "www.xiniudata.com")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; sajssdk_2020_cross_new_user=1; utoken=48WTD9FUEY0QHYPSOIATAR50SSDV3EC4; username=%E6%97%B6%E4%B8%8D%E6%88%91%E4%B8%8E; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689819997; search=anker%20anker%20null")
                .header("if-none-match", "W/\"b94f-6YwPQgCsWRzzOm8iAnA0wZK4eZ0\"")
                .header("referer", "https://www.xiniudata.com/search2?name=reInventAI?type=-104&errorBackURI=%2Fsearch2%3Fname%3DreInventAI")
                .header("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Microsoft Edge\";v=\"114\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"macOS\"")
                .header("sec-fetch-dest", "document")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67")
                .build();


        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                getUrl(responseData, name);

            } else {
                name.setReason("请求失败，错误码：" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.getCompanyDetails();
    }

    public void getUrl(String request, CompanyName name) {

        // Parse the HTML document with Jsoup
        Document doc = Jsoup.parse(request);

        // 使用选择器组合找到第一个以 "/company" 开头的 href 属性的 a 标签
        Elements links = doc.select("a[href^=\"/company\"]");

        // 检查标签是否存在
        if (!links.isEmpty()) {
            Element link = links.first();
            String linkText = link.attr("href"); // 获取链接文本值
            if (StringUtils.isNoneBlank(linkText)) {
                name.setName(linkText);
            }
        } else {
            name.setName("未找到链接标签");
        }

    }


    public void getDetail(String request, CompanyName name) {

        // Parse the HTML document with Jsoup
        Document doc = Jsoup.parse(request);
        // 使用选择器获取 <pre> 标签内容，忽略 class 属性值
        Elements preTags = doc.select("pre");

        // 检查标签是否存在
        if (!preTags.isEmpty()) {
            Element preTag = preTags.first();
            String preContent = preTag.text();// 获取 <pre> 标签中的文本内容
            if (StringUtils.isNoneBlank(preContent)) {
                name.setDetail(preContent);
            }
        } else {
            name.setDetail("未找到详情标签");
        }
    }

}



