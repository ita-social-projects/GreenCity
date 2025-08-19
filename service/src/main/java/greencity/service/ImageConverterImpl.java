package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageConverterImpl implements ImageConverter {
    private final ModelMapper modelMapper;

    @Override
    public MultipartFile convertToMultipartImage(String image) {
        try {
            return modelMapper.map(image, MultipartFile.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorMessage.MULTIPART_FILE_BAD_REQUEST + image);
        }
    }
}
