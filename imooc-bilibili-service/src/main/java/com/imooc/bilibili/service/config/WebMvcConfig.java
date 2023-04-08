package com.imooc.bilibili.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //SpringMVC（客户端和服务器端）使用HttpMessageConverters来协商HTTP交换中的内容转换。
//        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = new ObjectMapper();
//        // 未知字段报错关闭
////        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        // add(0, converter) 提高优先级
//        converters.add(0, jackson2HttpMessageConverter);


    /**
     * 长整型数字处理
     * JavaSript说在(-2^{53}, 2^{53})范围内，双精度数表示和整数是一对一的，在这个范围以内，所有的整数都有唯一的浮点数表示，这叫做安全整数
     * 而Java的Long类型的有效位数是63位（扣除一位符号位）
     *
     * 解决方法：自定义ObjectMapper，将Long类型的数据转换成字符串
     * 可以单独根据类型进行设置，只对Long型数据进行处理，转换成字符串，而对其他类型的数字不做处理。Jackson提供了这种支持，即对ObjectMapper进行定制。
     */
    @Bean("jackSon2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        //lambda表达式优化
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
//        return new Jackson2ObjectMapperBuilderCustomizer() {
//            @Override
//            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
//                jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance)
//                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
//            }
//        };
    }
}
