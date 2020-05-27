package greencity.service.impl;

import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.AddTipsAndTricksDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDto;
import greencity.entity.TipsAndTricks;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.TipsAndTricksRepo;
import greencity.service.FileService;
import greencity.service.NewsSubscriberService;
import greencity.service.TipsAndTricksService;
import greencity.service.TipsAndTricksTagsService;
import greencity.service.UserService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Service
@RequiredArgsConstructor
public class TipsAndTricksServiceImpl implements TipsAndTricksService {
    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;

    private final TipsAndTricksRepo tipsAndTricksRepo;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final RabbitTemplate rabbitTemplate;

    private final NewsSubscriberService newsSubscriberService;

    private final TipsAndTricksTagsService tipsAndTricksTagsService;

    private final FileService fileService;

    /**
     * {@inheritDoc}
     */
    @Override
    public AddTipsAndTricksDtoResponse save(AddTipsAndTricksDtoRequest addTipsAndTricksDtoRequest, MultipartFile image,
                                            String email) {
        TipsAndTricks toSave = modelMapper.map(addTipsAndTricksDtoRequest, TipsAndTricks.class);
        toSave.setAuthor(userService.findByEmail(email));
        if (addTipsAndTricksDtoRequest.getImage() != null) {
            image = convertToMultipartImage(addTipsAndTricksDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }
        toSave.setTipsAndTricksTags(addTipsAndTricksDtoRequest.getTipsAndTricksTags()
            .stream()
            .map(tipsAndTricksTagsService::findByName)
            .collect(Collectors.toList())
        );

        try {
            tipsAndTricksRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.TIPS_AND_TRICKS_NOT_SAVED);
        }

        return modelMapper.map(toSave, AddTipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDto> findAll(Pageable page) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);
        List<TipsAndTricksDto> tipsAndTricksDtos = pages
            .stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDto> find(Pageable page, List<String> tags) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.find(page, tags);

        List<TipsAndTricksDto> tipsAndTricksDtos = pages.stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            tipsAndTricksDtos,
            pages.getTotalElements(),
            pages.getPageable().getPageNumber()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricks findById(Long id) {
        return tipsAndTricksRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDto findDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findById(id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = CacheConstants.NEWEST_TIPS_AND_TRICKS_CACHE_NAME, allEntries = true)
    @Override
    public void delete(Long id) {
        tipsAndTricksRepo.deleteById(findById(id).getId());
    }


    private MultipartFile convertToMultipartImage(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        try {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            return new MockMultipartFile(tempFile.getPath(), new FileInputStream(tempFile));
        } catch (IOException e) {
            throw new NotSavedException("Cannot convert to BASE64 image");
        }
    }
}
