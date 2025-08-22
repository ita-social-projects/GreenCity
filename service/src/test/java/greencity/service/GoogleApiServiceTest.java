package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PlacesApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.errors.ApiException;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.filter.FilterPlacesApiDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.GoogleApiException;
import greencity.exception.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GoogleApiServiceTest {
    @Mock
    GeoApiContext context;

    @Mock
    GeocodingApiRequest request;

    @InjectMocks
    GoogleApiService googleApiService;

    private final AddressType[] addressTypes =
        {AddressType.LOCALITY, AddressType.ADMINISTRATIVE_AREA_LEVEL_1, AddressType.COUNTRY};
    private final String languageUa = "uk";
    private final LatLng coordinates = new LatLng(20.000000, 20.000000);

    @Test
    void getResultFromGeoCodeByCoordinatesTest() throws IOException, InterruptedException, ApiException {
        AddressLatLngResponse expected = ModelUtils.getAddressLatLngResponse();
        LatLng latLng = new LatLng(expected.getLatitude(), expected.getLongitude());
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {
            GeocodingApiRequest geocodingApiRequest = mock(GeocodingApiRequest.class);
            GeocodingApiRequest requestEn = mock(GeocodingApiRequest.class);
            when(GeocodingApi.newRequest(context)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.latlng(latLng)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.language(Locale.of("uk").getLanguage())).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.language(Locale.of("en").getLanguage())).thenReturn(requestEn);
            when(geocodingApiRequest.await()).thenReturn(ModelUtils.getGeocodingResultUk());
            when(requestEn.await()).thenReturn(ModelUtils.getGeocodingResultEn());
            assertEquals(expected, googleApiService.getResultFromGeoCodeByCoordinates(latLng));
            verify(geocodingApiRequest, times(1)).await();
            verify(requestEn, times(1)).await();
        }
    }

    @Test
    void getResultFromGeoCodeByCoordinatesWithInvalidUkrainianRequestTest()
        throws IOException, InterruptedException, ApiException {
        AddressLatLngResponse expected = ModelUtils.getAddressLatLngResponse();
        LatLng latLng = new LatLng(expected.getLatitude(), expected.getLongitude());
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {
            GeocodingApiRequest geocodingApiRequest = mock(GeocodingApiRequest.class);
            when(GeocodingApi.newRequest(context)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.latlng(latLng)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.language(Locale.of("uk").getLanguage())).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.await()).thenThrow(ApiException.class);
            assertThrows(BadRequestException.class, () -> googleApiService.getResultFromGeoCodeByCoordinates(latLng));
            verify(geocodingApiRequest, times(1)).await();
        }
    }

    @Test
    void getResultFromGeoCodeByCoordinatesWithInvalidCoordinatesTest()
        throws IOException, InterruptedException, ApiException {
        LatLng latLng = new LatLng(0.0, 0.0);
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {
            GeocodingApiRequest geocodingApiRequest = mock(GeocodingApiRequest.class);
            when(GeocodingApi.newRequest(context)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.latlng(latLng)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.language(Locale.of("uk").getLanguage())).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.await()).thenReturn(new GeocodingResult[0]);

            assertThrows(BadRequestException.class, () -> googleApiService.getResultFromGeoCodeByCoordinates(latLng));

            verify(geocodingApiRequest).latlng(latLng);
            verify(geocodingApiRequest).language(Locale.of("uk").getLanguage());
            verify(geocodingApiRequest).await();
        }
    }

    @Test
    void getResultFromGeoCodeByCoordinatesWithNullResultsTest()
        throws IOException, InterruptedException, ApiException {
        LatLng latLng = new LatLng(0.0, 0.0);
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {
            GeocodingApiRequest geocodingApiRequest = mock(GeocodingApiRequest.class);
            when(GeocodingApi.newRequest(context)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.latlng(latLng)).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.language(Locale.of("uk").getLanguage())).thenReturn(geocodingApiRequest);
            when(geocodingApiRequest.await()).thenReturn(null);

            assertThrows(BadRequestException.class, () -> googleApiService.getResultFromGeoCodeByCoordinates(latLng));

            verify(geocodingApiRequest).latlng(latLng);
            verify(geocodingApiRequest).language(Locale.of("uk").getLanguage());
            verify(geocodingApiRequest).await();
        }
    }

    @Test
    void getResultsFromGeocodeTest() throws IOException, InterruptedException, ApiException {
        Locale localeUk = Locale.forLanguageTag("uk");
        Locale localeEn = Locale.forLanguageTag("en");
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {

            GeocodingApiRequest requestUk = mock(GeocodingApiRequest.class);
            GeocodingApiRequest requestEn = mock(GeocodingApiRequest.class);

            String searchRequest = "testSearchRequest";

            when(GeocodingApi.newRequest(context)).thenReturn(requestUk, requestEn);

            when(requestUk.address(searchRequest)).thenReturn(requestUk);
            when(requestUk.language(localeUk.getLanguage())).thenReturn(requestUk);
            when(requestUk.await()).thenReturn(ModelUtils.getGeocodingResultUk());

            when(requestEn.address(searchRequest)).thenReturn(requestEn);
            when(requestEn.language(localeEn.getLanguage())).thenReturn(requestEn);
            when(requestEn.await()).thenReturn(ModelUtils.getGeocodingResultEn());

            List<GeocodingResult> actual = googleApiService.getResultFromGeoCode(searchRequest);

            assertEquals(ModelUtils.getGeocodingResultUk().length + ModelUtils.getGeocodingResultEn().length,
                actual.size());
            verify(requestUk, times(1)).await();
            verify(requestEn, times(1)).await();
        }
    }

    @Test
    void getResultsFromGeocodeThrowsTest() throws IOException, InterruptedException, ApiException {
        String searchRequest = "testSearchRequest";

        Locale localeUk = Locale.forLanguageTag("uk");
        Locale localeEn = Locale.forLanguageTag("en");
        try (MockedStatic<GeocodingApi> geocodingApiMockedStatic = mockStatic(GeocodingApi.class)) {

            GeocodingApiRequest requestUk = mock(GeocodingApiRequest.class);
            GeocodingApiRequest requestEn = mock(GeocodingApiRequest.class);

            when(GeocodingApi.newRequest(context)).thenReturn(requestUk, requestEn);

            when(requestUk.address(searchRequest)).thenReturn(requestUk);
            when(requestUk.language(localeUk.getLanguage())).thenReturn(requestUk);
            when(requestUk.await()).thenThrow(ApiException.class);

            when(requestEn.address(searchRequest)).thenReturn(requestEn);
            when(requestEn.language(localeEn.getLanguage())).thenReturn(requestEn);
            when(requestEn.await()).thenThrow(ApiException.class);

            assertDoesNotThrow(() -> googleApiService.getResultFromGeoCode(searchRequest));

            verify(requestUk, times(1)).await();
            verify(requestEn, times(1)).await();
        }
    }

    @Test
    void getResultFromPlacesApiTest() throws IOException, InterruptedException, ApiException {
        FilterPlacesApiDto filterDto = ModelUtils.getFilterPlacesApiDto();
        UserVO userVO = ModelUtils.getUserVO();

        Locale localeUk = Locale.forLanguageTag("uk");
        Locale localeEn = Locale.forLanguageTag("en");
        try (MockedStatic<PlacesApi> placesApiMockedStatic = mockStatic(PlacesApi.class)) {
            NearbySearchRequest requestUk = mock(NearbySearchRequest.class);
            NearbySearchRequest requestEn = mock(NearbySearchRequest.class);

            when(PlacesApi.nearbySearchQuery(context, filterDto.getLocation())).thenReturn(requestUk, requestEn);

            when(requestUk.radius(filterDto.getRadius())).thenReturn(requestUk);
            when(requestUk.language(localeUk.getLanguage())).thenReturn(requestUk);
            when(requestUk.keyword(filterDto.getKeyword())).thenReturn(requestUk);
            when(requestUk.type(filterDto.getType())).thenReturn(requestUk);
            when(requestUk.rankby(filterDto.getRankBy())).thenReturn(requestUk);
            when(requestUk.minPrice(filterDto.getMinPrice())).thenReturn(requestUk);
            when(requestUk.maxPrice(filterDto.getMaxPrice())).thenReturn(requestUk);
            when(requestUk.openNow(filterDto.isOpenNow())).thenReturn(requestUk);
            when(requestUk.name(filterDto.getName())).thenReturn(requestUk);

            when(requestEn.radius(filterDto.getRadius())).thenReturn(requestEn);
            when(requestEn.language(localeEn.getLanguage())).thenReturn(requestEn);
            when(requestEn.keyword(filterDto.getKeyword())).thenReturn(requestEn);
            when(requestEn.type(filterDto.getType())).thenReturn(requestEn);
            when(requestEn.rankby(filterDto.getRankBy())).thenReturn(requestEn);
            when(requestEn.minPrice(filterDto.getMinPrice())).thenReturn(requestEn);
            when(requestEn.maxPrice(filterDto.getMaxPrice())).thenReturn(requestEn);
            when(requestEn.openNow(filterDto.isOpenNow())).thenReturn(requestEn);
            when(requestEn.name(filterDto.getName())).thenReturn(requestEn);

            when(requestUk.await()).thenReturn(ModelUtils.getPlacesSearchResponseUk());
            when(requestEn.await()).thenReturn(ModelUtils.getPlacesSearchResponseEn());

            List<PlacesSearchResult> actual = googleApiService.getResultFromPlacesApi(filterDto, userVO);

            List<PlacesSearchResult> expected = List.of(ModelUtils.getPlacesSearchResultUk().getFirst(),
                ModelUtils.getPlacesSearchResultEn().getFirst());

            assertEquals(actual.size(), expected.size());

            verify(requestUk, times(1)).await();
            verify(requestEn, times(1)).await();
        }
    }

    @Test
    void getResultFromPlacesApiNullLocationTest() throws IOException, InterruptedException, ApiException {
        FilterPlacesApiDto filterDto = ModelUtils.getFilterPlacesApiDto();
        filterDto.setLocation(null);
        UserVO userVO = ModelUtils.getUserVO();
        userVO.getUserLocation().setLongitude(null);

        Locale localeUk = Locale.forLanguageTag("uk");
        Locale localeEn = Locale.forLanguageTag("en");
        try (MockedStatic<PlacesApi> placesApiMockedStatic = mockStatic(PlacesApi.class)) {
            NearbySearchRequest requestUk = mock(NearbySearchRequest.class);
            NearbySearchRequest requestEn = mock(NearbySearchRequest.class);

            when(PlacesApi.nearbySearchQuery(context, filterDto.getLocation())).thenReturn(requestUk, requestEn);

            when(requestUk.radius(filterDto.getRadius())).thenReturn(requestUk);
            when(requestUk.language(localeUk.getLanguage())).thenReturn(requestUk);
            when(requestUk.keyword(filterDto.getKeyword())).thenReturn(requestUk);
            when(requestUk.type(filterDto.getType())).thenReturn(requestUk);
            when(requestUk.rankby(filterDto.getRankBy())).thenReturn(requestUk);
            when(requestUk.minPrice(filterDto.getMinPrice())).thenReturn(requestUk);
            when(requestUk.maxPrice(filterDto.getMaxPrice())).thenReturn(requestUk);
            when(requestUk.openNow(filterDto.isOpenNow())).thenReturn(requestUk);
            when(requestUk.name(filterDto.getName())).thenReturn(requestUk);

            when(requestEn.radius(filterDto.getRadius())).thenReturn(requestEn);
            when(requestEn.language(localeEn.getLanguage())).thenReturn(requestEn);
            when(requestEn.keyword(filterDto.getKeyword())).thenReturn(requestEn);
            when(requestEn.type(filterDto.getType())).thenReturn(requestEn);
            when(requestEn.rankby(filterDto.getRankBy())).thenReturn(requestEn);
            when(requestEn.minPrice(filterDto.getMinPrice())).thenReturn(requestEn);
            when(requestEn.maxPrice(filterDto.getMaxPrice())).thenReturn(requestEn);
            when(requestEn.openNow(filterDto.isOpenNow())).thenReturn(requestEn);
            when(requestEn.name(filterDto.getName())).thenReturn(requestEn);

            when(requestUk.await()).thenReturn(ModelUtils.getPlacesSearchResponseUk());
            when(requestEn.await()).thenReturn(ModelUtils.getPlacesSearchResponseEn());

            assertThrows(NotFoundException.class, () -> googleApiService.getResultFromPlacesApi(filterDto, userVO));

            verify(requestUk, times(0)).await();
            verify(requestEn, times(0)).await();
        }
    }

    @Test
    void getResultFromPlacesApiThrowsApiExceptionTest() throws IOException, InterruptedException, ApiException {
        FilterPlacesApiDto filterDto = ModelUtils.getFilterPlacesApiDto();
        UserVO userVO = ModelUtils.getUserVO();

        Locale localeUk = Locale.forLanguageTag("uk");
        Locale localeEn = Locale.forLanguageTag("en");
        try (MockedStatic<PlacesApi> placesApiMockedStatic = mockStatic(PlacesApi.class)) {
            NearbySearchRequest requestUk = mock(NearbySearchRequest.class);
            NearbySearchRequest requestEn = mock(NearbySearchRequest.class);

            when(PlacesApi.nearbySearchQuery(context, filterDto.getLocation())).thenReturn(requestUk, requestEn);

            when(requestUk.radius(filterDto.getRadius())).thenReturn(requestUk);
            when(requestUk.language(localeUk.getLanguage())).thenReturn(requestUk);
            when(requestUk.keyword(filterDto.getKeyword())).thenReturn(requestUk);
            when(requestUk.type(filterDto.getType())).thenReturn(requestUk);
            when(requestUk.rankby(filterDto.getRankBy())).thenReturn(requestUk);
            when(requestUk.minPrice(filterDto.getMinPrice())).thenReturn(requestUk);
            when(requestUk.maxPrice(filterDto.getMaxPrice())).thenReturn(requestUk);
            when(requestUk.openNow(filterDto.isOpenNow())).thenReturn(requestUk);
            when(requestUk.name(filterDto.getName())).thenReturn(requestUk);

            when(requestEn.radius(filterDto.getRadius())).thenReturn(requestEn);
            when(requestEn.language(localeEn.getLanguage())).thenReturn(requestEn);
            when(requestEn.keyword(filterDto.getKeyword())).thenReturn(requestEn);
            when(requestEn.type(filterDto.getType())).thenReturn(requestEn);
            when(requestEn.rankby(filterDto.getRankBy())).thenReturn(requestEn);
            when(requestEn.minPrice(filterDto.getMinPrice())).thenReturn(requestEn);
            when(requestEn.maxPrice(filterDto.getMaxPrice())).thenReturn(requestEn);
            when(requestEn.openNow(filterDto.isOpenNow())).thenReturn(requestEn);
            when(requestEn.name(filterDto.getName())).thenReturn(requestEn);

            when(requestUk.await()).thenReturn(ModelUtils.getPlacesSearchResponseUk());
            when(requestEn.await()).thenThrow(ApiException.class);

            assertDoesNotThrow(() -> googleApiService.getResultFromPlacesApi(filterDto, userVO));

            verify(requestUk, times(1)).await();
            verify(requestEn, times(1)).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.latlng(coordinates)).thenReturn(request);
            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenReturn(ModelUtils.getGeocodingResult().toArray(GeocodingResult[]::new));
            assertDoesNotThrow(
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).latlng(coordinates);
            verify(request).language(languageUa);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsNotFoundExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.latlng(coordinates)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new InvalidRequestException("message"));
            String formattedCoordinates = "%.8f,%.8f".formatted(coordinates.lat, coordinates.lng);
            NotFoundException exception =
                assertThrows(NotFoundException.class,
                    () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                        addressTypes));

            assertEquals(ErrorMessage.NOT_FOUND_ADDRESS_BY_COORDINATES + formattedCoordinates, exception.getMessage());
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsGoogleApiExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new GoogleApiException("something went wrong"));
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }

    @Test
    @SneakyThrows
    void getLocationByCoordinatesThrowsInterruptedExceptionTest() {
        try (MockedStatic<GeocodingApi> utilities = Mockito.mockStatic(GeocodingApi.class)) {
            utilities.when(() -> GeocodingApi.newRequest(context))
                .thenReturn(request);

            when(request.language(languageUa)).thenReturn(request);
            when(request.resultType(addressTypes)).thenReturn(request);
            when(request.await()).thenThrow(new InterruptedException());
            when(request.latlng(coordinates)).thenReturn(request);

            assertThrows(GoogleApiException.class,
                () -> googleApiService.getLocationByCoordinates(coordinates.lat, coordinates.lng, languageUa,
                    addressTypes));
            verify(request).language(languageUa);
            verify(request).latlng(coordinates);
            verify(request).await();
        }
    }
}
