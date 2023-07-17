package io.github.fairyspace.core.convert.support;

import io.github.fairyspace.core.convert.converter.Converter;

public class StringToIntegerConverter implements Converter<String,Integer> {
    @Override
    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}
