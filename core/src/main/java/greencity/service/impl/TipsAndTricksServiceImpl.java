package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.TipsAndTricksRepo;
import greencity.service.FileService;
import greencity.service.TipsAndTricksService;
import greencity.service.TipsAndTricksTagsService;
import greencity.service.UserService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final TipsAndTricksRepo tipsAndTricksRepo;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final TipsAndTricksTagsService tipsAndTricksTagsService;

    private final FileService fileService;

    /**
     * {@inheritDoc}
     */
    @Override
    public TipsAndTricksDtoResponse save(TipsAndTricksDtoRequest tipsAndTricksDtoRequest, MultipartFile image,
                                         String email) {
        TipsAndTricks toSave = modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class);
        toSave.setAuthor(userService.findByEmail(email));
        if (tipsAndTricksDtoRequest.getImage() != null) {
            image = convertToMultipartImage(tipsAndTricksDtoRequest.getImage());
        }
        if (image != null) {
            toSave.setImagePath(fileService.upload(image).toString());
        }
        toSave.setTipsAndTricksTags(tipsAndTricksDtoRequest.getTipsAndTricksTags()
            .stream()
            .map(tipsAndTricksTagsService::findByName)
            .collect(Collectors.toList())
        );

        try {
            tipsAndTricksRepo.save(toSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.TIPS_AND_TRICKS_NOT_SAVED);
        }

        return modelMapper.map(toSave, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoResponse> findAll(Pageable page) {
        Page<TipsAndTricks> pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);

        return getPagesWithTipsAndTricksResponseDto(pages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<TipsAndTricksDtoResponse> find(Pageable page, Optional<String> tags) {
        Page<TipsAndTricks> pages;
        if (tags.isPresent()) {
            pages = tipsAndTricksRepo.find(page, tags);
        } else {
            pages = tipsAndTricksRepo.findAllByOrderByCreationDateDesc(page);
        }
        return getPagesWithTipsAndTricksResponseDto(pages);
    }

    private PageableDto<TipsAndTricksDtoResponse> getPagesWithTipsAndTricksResponseDto(Page<TipsAndTricks> pages) {
        List<TipsAndTricksDtoResponse> tipsAndTricksDtos = pages
            .stream()
            .map(tipsAndTricks -> modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class))
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
    public TipsAndTricksDtoResponse findDtoById(Long id) {
        TipsAndTricks tipsAndTricks = findById(id);
        return modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        tipsAndTricksRepo.deleteById(findById(id).getId());
    }

    private TipsAndTricks findById(Long id) {
        return tipsAndTricksRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
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
