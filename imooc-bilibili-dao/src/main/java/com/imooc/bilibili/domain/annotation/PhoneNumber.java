package com.imooc.bilibili.domain.annotation;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * ConstraintComposition注解可以应用于类、方法或字段上，用于组合多个约束条件，以实现更复杂的限制。
 * ConstraintComposition注解的CompositionType属性用于指定组合类型，有AND和OR两种类型。
 * AND表示所有约束条件都必须满足，OR表示只要有一个约束条件满足即可。
 */

//1.定义注解类
@ConstraintComposition(CompositionType.OR)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    boolean required() default true;
    String message() default "Invalid phone number";
    Class[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
