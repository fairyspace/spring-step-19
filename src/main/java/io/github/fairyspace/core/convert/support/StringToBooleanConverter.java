package io.github.fairyspace.core.convert.support;

import io.github.fairyspace.core.convert.converter.GenericConverter;

import java.util.Collections;
import java.util.Set;

public class StringToBooleanConverter implements GenericConverter {
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
         return Collections.singleton(new ConvertiblePair(String.class, Boolean.class));
    }

    @Override
    public Object convert(Object source, Class sourceType, Class targetType) {
        return Boolean.valueOf((String) source);
    }
}
