/*
 * Tencent is pleased to support the open source community by making BK-JOB 蓝鲸作业平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-JOB 蓝鲸作业平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tencent.bk.job.common.web.validation;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Configuration
public class ValidationConfiguration {

    @Bean
    @Primary
    public LocalValidatorFactoryBean localValidatorFactoryBean(
        @Qualifier("messageSource") MessageSource messageSource) {
        return new JobLocalValidatorFactoryBean(messageSource);
    }

    public class JobLocalValidatorFactoryBean extends LocalValidatorFactoryBean {
        private MessageSource messageSource;

        JobLocalValidatorFactoryBean(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @Override
        protected Object getRejectedValue(String field, ConstraintViolation<Object> violation,
                                          BindingResult bindingResult) {
            return violation.getInvalidValue();
        }

        @Override
        protected void postProcessConfiguration(javax.validation.Configuration<?> configuration) {
            if (configuration instanceof HibernateValidatorConfiguration) {
                HibernateValidatorConfiguration hibernateValidatorConfiguration =
                    (HibernateValidatorConfiguration) configuration;
                hibernateValidatorConfiguration.propertyNodeNameProvider(new JacksonPropertyNodeNameProvider());
                hibernateValidatorConfiguration.failFast(true);
                hibernateValidatorConfiguration.messageInterpolator(new ResourceBundleMessageInterpolator(
                    new MessageSourceResourceBundleLocator(messageSource), getLocales(), Locale.ENGLISH,
                    LocaleResolver(), true));
            }
        }

        private Set<Locale> getLocales() {
            Set<Locale> locales = new HashSet<>();
            locales.add(Locale.CHINESE);
            locales.add(Locale.SIMPLIFIED_CHINESE);
            locales.add(Locale.ENGLISH);
            locales.add(Locale.US);
            return locales;
        }
    }

    @Bean("localeResolverForValidation")
    public LocaleResolver LocaleResolver() {
        return context -> LocaleContextHolder.getLocale();
    }


}
