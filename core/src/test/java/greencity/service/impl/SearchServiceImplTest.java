package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.service.EcoNewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SearchServiceImplTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @InjectMocks
    private ModelMapper modelMapper;

    @Mock
    private EcoNewsService ecoNewsService;

    @Test
    void searchTest() {
        List<SearchNewsDto> listSearchNewsDto = new ArrayList<>();
        listSearchNewsDto.add(modelMapper.map(ModelUtils.getEcoNews(), SearchNewsDto.class));

        when(ecoNewsService.search(anyString())).thenReturn(listSearchNewsDto);
        assertEquals(listSearchNewsDto, searchService.search("test").getEcoNews());
    }
}