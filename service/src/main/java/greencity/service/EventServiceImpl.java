package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.entity.Event;
import greencity.entity.EventImages;
import greencity.entity.User;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl  implements  EventService{

    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final FileService fileService;


    @Override
    public AddEventDtoResponse save(AddEventDtoRequest addEventDtoRequest, String email) {
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        toSave.setOrganizer(organizer);

        List<MultipartFile> multipartFiles = new ArrayList<>();
        if(addEventDtoRequest.getImages() != null && !addEventDtoRequest.getImages().isEmpty()){
            multipartFiles = addEventDtoRequest.getImages().stream().map(image -> fileService.convertToMultipartImage(image)).collect(Collectors.toList());
        }

        if(multipartFiles != null){
            toSave.setImages(multipartFiles.stream().map(image -> fileService.upload(image)).map(eventImage ->
                            EventImages.builder().event(toSave).link(eventImage).build()
                    ).collect(Collectors.toList()));
        }

        toSave.setDateTime(ZonedDateTime.now());

        return modelMapper.map(eventRepo.save(toSave), AddEventDtoResponse.class);
    }

    @Override
    public void addAttender(Long eventId, String email) {
        Event event = eventRepo.getOne(eventId);
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);

        if(!event.getAttenders().contains(currentUser)){
            event.getAttenders().add(currentUser);
            eventRepo.save(event);
        }
    }


}
