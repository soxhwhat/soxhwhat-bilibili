package com.imooc.bilibili.domain.annotation;

import com.mysql.cj.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/2/18 22:28
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
//2. 实现手机号格式校验类
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^1\\d{10}$");

    public boolean required = false;

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return !required || PHONE_NUMBER_PATTERN.matcher(value).matches()&& !StringUtils.isNullOrEmpty(value);
    }
}
