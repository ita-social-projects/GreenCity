package greencity.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ModelMapperImpl implements ModelMapperUtil {

    private ModelMapper modelMapper;

    public <S, D> D map(final S srcObj, Class<D> destClass) {
        return modelMapper.map(srcObj, destClass);
    }

    public <S, D> D map(final S srcObj, D destObj) {
        modelMapper.map(srcObj, destObj);
        return destObj;
    }

    public <S, D> List<D> mapAll(final Collection<S> srcList, Class<D> destClass) {
        return srcList.stream().map(src -> map(src, destClass)).collect(Collectors.toList());
    }
}
