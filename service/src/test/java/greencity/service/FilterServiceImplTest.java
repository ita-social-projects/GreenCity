package greencity.service;


import greencity.ModelUtils;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import greencity.repository.FilterRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {

    @Mock
    private FilterRepo filterRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FilterServiceImpl filterServiceImpl;

    @Test
    void saveTest() {
        UserFilterDtoRequest dto = ModelUtils.getUserFilterDtoRequest();

        when(modelMapper.map(dto, Filter.class)).thenReturn(ModelUtils.getFilter());
        when(userRepo.findById(1L)).thenReturn(Optional.of(ModelUtils.getUser()));
        when(filterRepo.save(ModelUtils.getFilter())).thenReturn(ModelUtils.getFilter());
        when(modelMapper.map(ModelUtils.getFilter(), UserFilterDtoResponse.class)).thenReturn(ModelUtils.getUserFilterDtoResponse());

        UserFilterDtoResponse dtoResponse = filterServiceImpl.save(1L, dto);

        assertEquals(dtoResponse, ModelUtils.getUserFilterDtoResponse());
    }

    @Test
    void getFilterByIdTest() {
        Filter filter = ModelUtils.getFilter();
        when(filterRepo.findById(1L)).thenReturn(Optional.of(filter));
        when(modelMapper.map(filter, UserFilterDtoResponse.class)).thenReturn(ModelUtils.getUserFilterDtoResponse());

        filterServiceImpl.getFilterById(filter.getId());
    }

    @Test
    void deleteFilterByIdTest() {
        Filter filter = ModelUtils.getFilter();
        when(filterRepo.findById(1L)).thenReturn(Optional.of(filter));

        filterServiceImpl.deleteFilterById(filter.getId());
    }


}
