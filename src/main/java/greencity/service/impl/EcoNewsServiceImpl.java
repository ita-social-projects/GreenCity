package greencity.service.impl;

import greencity.annotations.EventPublishing;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.events.SendNewsEvent;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EcoNewsRepo;
import greencity.service.EcoNewsService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class EcoNewsServiceImpl implements EcoNewsService {
    private final EcoNewsRepo ecoNewsRepo;
    private final ModelMapper modelMapper;

    /**
     * Constructor with parameters.
     *
     * @author Yuriy Olkhovskyi.
     */
    @Autowired
    public EcoNewsServiceImpl(EcoNewsRepo ecoNewsRepo, ModelMapper modelMapper) {
        this.ecoNewsRepo = ecoNewsRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @EventPublishing(eventClass = {SendNewsEvent.class})
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setCreationDate(ZonedDateTime.now());
        try {
            ecoNewsRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED);
        }

        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public List<EcoNewsDto> getThreeLastEcoNews() {
        List<EcoNews> ecoNewsList = ecoNewsRepo.getThreeLastEcoNews();
        if (ecoNewsList.isEmpty()) {
            throw new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND);
        }
        return ecoNewsList
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public List<EcoNewsDto> findAll() {
        return ecoNewsRepo.findAll()
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    @Override
    public EcoNews findById(Long id) {
        return ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     *
     * @author Yuriy Olkhovskyi.
     */
    public void delete(Long id) {
        ecoNewsRepo.deleteById(findById(id).getId());
    }
}
