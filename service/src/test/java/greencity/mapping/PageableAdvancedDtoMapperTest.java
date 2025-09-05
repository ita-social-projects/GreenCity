package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import greencity.dto.PageableAdvancedDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PageableAdvancedDtoMapperTest {
    private PageableAdvancedDtoMapper<String> mapper;

    @BeforeEach
    void setUp() {
        mapper = new PageableAdvancedDtoMapper<>();
    }

    @Test
    void convertTest() {
        List<String> content = List.of("one", "two", "three");
        PageRequest pageRequest = PageRequest.of(1, 3);
        Page<String> page = new PageImpl<>(content, pageRequest, 10);

        PageableAdvancedDto<String> dto = mapper.convert(page);

        assertNotNull(dto);
        assertEquals(content, dto.getPage());
        assertEquals(10, dto.getTotalElements());
        assertEquals(1, dto.getCurrentPage());
        assertEquals(page.getTotalPages(), dto.getTotalPages());
        assertEquals(page.getNumber(), dto.getNumber());
        assertEquals(page.hasPrevious(), dto.isHasPrevious());
        assertEquals(page.hasNext(), dto.isHasNext());
        assertEquals(page.isFirst(), dto.isFirst());
        assertEquals(page.isLast(), dto.isLast());
    }

    @Test
    void convertEmptyPageTest() {
        List<String> content = List.of();
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<String> page = new PageImpl<>(content, pageRequest, 0);

        PageableAdvancedDto<String> dto = mapper.convert(page);

        assertNotNull(dto);
        assertTrue(dto.getPage().isEmpty());
        assertEquals(0, dto.getTotalElements());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(0, dto.getTotalPages());
        assertEquals(0, dto.getNumber());
        assertTrue(dto.isFirst());
        assertTrue(dto.isLast());
        assertFalse(dto.isHasPrevious());
        assertFalse(dto.isHasNext());
    }
}