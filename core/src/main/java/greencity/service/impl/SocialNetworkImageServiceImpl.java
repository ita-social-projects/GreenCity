package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.CacheConstants;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.entity.Place;
import greencity.entity.SocialNetworkImage;
import greencity.repository.SocialNetworkImageRepo;
import greencity.service.FileService;
import greencity.service.SocialNetworkImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static greencity.constant.CacheConstants.SOCIAL_NETWORK_IMAGE_CACHE_NAME;

@Service
@Slf4j
@AllArgsConstructor
@EnableCaching
public class SocialNetworkImageServiceImpl implements SocialNetworkImageService {
    private  final SocialNetworkImageRepo socialNetworkImageRepo;
    private  final FileService fileService;
    private final ModelMapper modelMapper;

    /**
     * Method creates or returns existed {@link SocialNetworkImage} by given url.
     *
     * @param url a well-formed url
     * @return {@link SocialNetworkImage}
     */
    @Override
    public SocialNetworkImage getSocialNetworkImageByUrl(String url) {
        try {
            URL checkUrl = new URL(url);
            Optional<SocialNetworkImage> optionalSocialNetworkImage =
                findByHostPath(checkUrl.getHost());
            if (optionalSocialNetworkImage.isPresent()) {
                return optionalSocialNetworkImage.get();
            } else {
                return saveSocialNetworkImage(checkUrl);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            return getDefaultSocialNetworkImage();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableDto<SocialNetworkImageResponseDTO> findAll(Pageable pageable) {
        log.info(LogMessage.IN_FIND_ALL);

        Page<SocialNetworkImage> pages = socialNetworkImageRepo.findAll(pageable);
        List<SocialNetworkImageResponseDTO> socialNetworkImageResponseDTOS = pages.stream()
                .map(socialNetworkImage -> modelMapper.map(socialNetworkImage, SocialNetworkImageResponseDTO.class))
                .collect(Collectors.toList());
        return new PageableDto<>(socialNetworkImageResponseDTOS,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableDto<SocialNetworkImageResponseDTO> searchBy(Pageable paging, String query) {
        Page<SocialNetworkImage> page = socialNetworkImageRepo.searchBy(paging, query);
        List<SocialNetworkImageResponseDTO> socialNetworkImageResponseDTOS = page.stream()
                .map(socialNetworkImage -> modelMapper.map(socialNetworkImage, SocialNetworkImageResponseDTO.class))
                .collect(Collectors.toList());
        return new PageableDto<>(
                socialNetworkImageResponseDTOS,
                page.getTotalElements(),
                page.getPageable().getPageNumber(),
                page.getTotalPages()
        );
    }

    /**
     * Method search SocialNetworkImage.
     *
     * @param hostPath host adress
     * @return optional of {@link SocialNetworkImage}
     */
    @Cacheable(value = CacheConstants.SOCIAL_NETWORK_IMAGE_CACHE_NAME)
    public Optional<SocialNetworkImage> findByHostPath(String hostPath) {
        return socialNetworkImageRepo.findByHostPath(hostPath);
    }

    /**
     * Method creates, saves {@link SocialNetworkImage} by given URL.
     *
     * @param url link path
     * @return {@link SocialNetworkImage} result of creation
     */
    @CacheEvict(value = SOCIAL_NETWORK_IMAGE_CACHE_NAME, allEntries = true)
    public SocialNetworkImage saveSocialNetworkImage(URL url) throws IOException {
        String imagePath = uploadImageToCloud(url);
        SocialNetworkImage socialNetworkImage = SocialNetworkImage.builder()
            .hostPath(url.getHost())
            .imagePath(imagePath)
            .build();
        return socialNetworkImageRepo.save(socialNetworkImage);
    }

    /**
     * Method return default social network image from DB by host key DEFAULT_SOCIAL_NETWORK_IMAGE_HOST_PATH.
     *
     * @return {@link SocialNetworkImage}
     */
    public SocialNetworkImage getDefaultSocialNetworkImage() {
        return findByHostPath(AppConstant.DEFAULT_SOCIAL_NETWORK_IMAGE_HOST_PATH)
            .orElseThrow(() -> new RuntimeException(ErrorMessage.BAD_DEFAULT_SOCIAL_NETWORK_IMAGE_PATH));
    }

    /**
     * Method downloads icon from URL and transforms it into {@link File}. Calls uploadFileToCloud().
     *
     * @param url URL of give page
     * @return URL.toString() image file location
     */
    private String uploadImageToCloud(URL url) throws IOException {
        String preparedUrlHost = url.getHost();
        String preparedFaviconUrl = String.format("http://www.google.com/s2/favicons?sz=64&domain_url=%s", URLEncoder
            .encode(preparedUrlHost, "UTF-8"));
        URL faviconUrl = new URL(preparedFaviconUrl);
        BufferedImage bufferedImage = ImageIO.read(faviconUrl);
        File tempFile = new File("tempImage.png");
        ImageIO.write(bufferedImage, "png", tempFile);
        return uploadFileToCloud(tempFile);
    }

    /**
     * Method uploads file to cloud.
     *
     * @param tempFile file to upload
     * @return URL.toString() file location
     */
    private String uploadFileToCloud(File tempFile) throws IOException {
        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(tempFile.toPath()),
            false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
        try (InputStream inputStream = new FileInputStream(tempFile);
             OutputStream outputStream = fileItem.getOutputStream();) {
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        }
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        URL uploadCloud = fileService.upload(multipartFile);
        return uploadCloud.toString();
    }
}
