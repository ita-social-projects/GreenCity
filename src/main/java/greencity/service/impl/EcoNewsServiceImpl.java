package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EcoNewsRepo;
import greencity.service.EcoNewsService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcoNewsServiceImpl implements EcoNewsService {
    EcoNewsRepo ecoNewsRepo;
    ModelMapper modelMapper;

    /**
     * njkkl .
     */
    @Autowired
    public EcoNewsServiceImpl(EcoNewsRepo ecoNewsRepo, ModelMapper modelMapper) {
        this.ecoNewsRepo = ecoNewsRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * asdas.
     * fsddggrweas.
     *
     * @return
     */
    @Override
    public AddEcoNewsDtoResponse save(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews toSave = modelMapper.map(addEcoNewsDtoRequest, EcoNews.class);
        toSave.setCreationDate(ZonedDateTime.now());
        toSave = Optional.of(ecoNewsRepo.save(toSave))
            .orElseThrow(() -> new NotSavedException(ErrorMessage.ECO_NEWS_NOT_SAVED));
        return modelMapper.map(toSave, AddEcoNewsDtoResponse.class);
    }

    /**
     * dasdas.
     *
     * @return dsadas.
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
     * dsad.
     */
    @Override
    public List<EcoNewsDto> findAll() {
        return ecoNewsRepo.findAll()
            .stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .collect(Collectors.toList());
    }

    /**
     * dsa.
     *
     * @param id dsadsa.
     * @return dsa.
     */
    @Override
    public EcoNews findById(Long id) {
        return ecoNewsRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + id));
    }

    /**
     * dsadsa.
     */
    public Long delete(Long id) {
        Optional<EcoNews> byId = ecoNewsRepo.findById(id);
        if (byId.isPresent()) {
            ecoNewsRepo.deleteById(id);
            return id;
        } else {
            throw new NotDeletedException(ErrorMessage.ECO_NEWS_NOT_DELETED);
        }
    }
}
