package br.com.api.pitang.utils;

import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

import static com.github.dozermapper.core.DozerBeanMapperBuilder.buildDefault;

public class DozerConverter {

    private static final Mapper mapper = buildDefault();

    public static <O, D> D convertObject(O origin, Class<D> destiny) {
        return mapper.map(origin, destiny);
    }

    public static <O, D> List<D> convertObjects(List<O> origins, Class<D> destiny) {
        List<D> destinations = new ArrayList<>();
        for( O origin : origins) {
            destinations.add(mapper.map(origin, destiny));
        }

        return destinations;
    }
}