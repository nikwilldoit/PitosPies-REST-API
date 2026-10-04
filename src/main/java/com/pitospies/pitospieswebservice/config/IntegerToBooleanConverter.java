package com.pitospies.pitospieswebservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

    @Override
    public Boolean convert(Integer source) {
        return source != null && source != 0;
    }
}