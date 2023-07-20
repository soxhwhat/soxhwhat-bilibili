package com.imooc.bilibili.service.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
public class HttpRequest {


    List<String> nameList = new ArrayList<>();


    @Autowired
    private CompanyNameService companyNameService;

    public Result getCompanyDetails() {
        nameList = getArrayList();
//        List<CompanyName> companyNameList = new CopyOnWriteArrayList<>();

        ForkJoinPool forkJoinPool = new ForkJoinPool(); // 创建ForkJoinPool
        forkJoinPool.submit(() ->
                        nameList.parallelStream().forEach(name -> {
                            CompanyName companyName = new CompanyName();
                            companyName.setName(name);
                            getUrlMap(companyName, name);
                            if (StringUtils.isNotBlank(companyName.getUrl())) {
                                getDetailMap(companyName, name);
                            }
//                    companyNameList.add(companyName);
                            CompanyName one = companyNameService.getOne(new QueryWrapper<CompanyName>().lambda().eq(CompanyName::getName, name));
                            if (one != null) {
                                companyName.setId(one.getId());
                                companyNameService.updateById(companyName);
                            } else {
                                companyNameService.save(companyName);
                            }

                        })
        ).join(); // 等待并发处理完成

        return Result.success();

    }

    public Result getDetailFix() {

        Map<String, String> map = readCsvToMap();
        List<CompanyName> companyNameList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            CompanyName companyName = new CompanyName();
            companyName.setName(entry.getKey());
            companyName.setUrl(entry.getValue());
            getDetailMap(companyName, entry.getKey());
            companyNameList.add(companyName);
            companyNameService.updateById(companyName);
        }

        return Result.success();
    }

    public void getDetailMap(CompanyName companyName, String name) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.xiniudata.com" + companyName.getUrl();

        Request request = new Request.Builder()
                .url(url)
                .header("authority", "www.xiniudata.com")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("cache-control", "max-age=0")
                //jaffe
//                .header("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; sajssdk_2020_cross_new_user=1; utoken=48WTD9FUEY0QHYPSOIATAR50SSDV3EC4; username=%E6%97%B6%E4%B8%8D%E6%88%91%E4%B8%8E; search=%E7%99%BE%E5%BA%A6%20anker%20anker%20null; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689820329")
                //nannan
                .header("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; utoken=5M008QX4YS7GYRH1UK71WO9L2KAU4EEF; username=%E5%B0%A4%E9%87%8C%E5%8D%A1; search=%E6%B5%99%E6%B1%9F%E9%98%AE%E4%BB%95%E7%8F%8D%E7%8F%A0%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E5%8D%97%E6%98%8C%E8%8B%97%E6%97%BA%E5%AE%9E%E4%B8%9A%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E9%9D%96%E6%B5%B7%E9%9B%86%E5%9B%A2%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E7%99%BE%E5%BA%A6%20anker%20anker%20null; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689912467")
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
                getDetail(responseData, companyName);
            } else {
                companyName.setDetail("请求失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUrlMap(CompanyName companyName, String name) {

        String url = "https://www.xiniudata.com/search2?name=" + name;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("authority", "www.xiniudata.com")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                //jaffe
                //.header("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; sajssdk_2020_cross_new_user=1; utoken=48WTD9FUEY0QHYPSOIATAR50SSDV3EC4; username=%E6%97%B6%E4%B8%8D%E6%88%91%E4%B8%8E; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689819997; search=anker%20anker%20null")
                .addHeader("cookie", "btoken=DOPAA1L4ZKP20RDY7B8VQZNFJX7V6F79; Hm_lvt_42317524c1662a500d12d3784dbea0f8=1689816761; hy_data_2020_id=18970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1; hy_data_2020_js_sdk=%7B%22distinct_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%2C%22site_id%22%3A211%2C%22user_company%22%3A105%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%2218970ed840028-086738de5d9deb-4f65167d-1296000-18970ed840111a1%22%7D; utoken=5M008QX4YS7GYRH1UK71WO9L2KAU4EEF; username=%E5%B0%A4%E9%87%8C%E5%8D%A1; Hm_lpvt_42317524c1662a500d12d3784dbea0f8=1689912107; search=%E6%B5%99%E6%B1%9F%E9%98%AE%E4%BB%95%E7%8F%8D%E7%8F%A0%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E5%8D%97%E6%98%8C%E8%8B%97%E6%97%BA%E5%AE%9E%E4%B8%9A%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E9%9D%96%E6%B5%B7%E9%9B%86%E5%9B%A2%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%20%E7%99%BE%E5%BA%A6%20anker%20anker%20null")
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
                getUrl(responseData, companyName);

            } else {
                companyName.setReason("请求失败，错误码：" + response.code());
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
                name.setUrl(linkText);
            }
        } else {
            name.setReason("未找到链接标签");
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
            name.setDetail("该公司不存在详情信息");
        } else {
            name.setDetail("未找到详情标签");
        }
    }

    public List<String> getArrayList() {
        String filePath = "/Users/anker/IdeaProjects/master-data-platform/src/main/java/com/anker/mdp/service/excel/test11.csv";
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

    public Map<String, String> readCsvToMap() {
        String filePath = "/Users/anker/IdeaProjects/master-data-platform/src/main/java/com/anker/mdp/service/excel/test.csv";
        Map<String, String> map = new HashMap<>();

        CsvReader reader = CsvUtil.getReader();
//从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(filePath));
        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            map.put(csvRow.getRawList().get(0), csvRow.getRawList().get(1));

        }
        return map;
    }


}



