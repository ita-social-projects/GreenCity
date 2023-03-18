package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import greencity.dto.geocoding.AddressResponse;
import greencity.dto.geocoding.AddressLatLngResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleApiService {
    private final GeoApiContext context;
    private static final Locale localeUk = new Locale("uk");
    private static final Locale localeEn = new Locale("en");
    private static final List<Locale> locales = List.of(localeUk, localeEn);

    /**
     * Send request to the Google and receive response with geocoding.
     *
     * @param searchRequest - address to search
     * @return GeocodingResults - return result from geocoding service
     */
    public List<GeocodingResult> getResultFromGeoCode(String searchRequest) {
        List<GeocodingResult> geocodingResults = new ArrayList<>();
        locales.forEach(locale -> {
            try {
                GeocodingResult[] results = GeocodingApi.newRequest(context)
                    .address(searchRequest).language(locale.getLanguage()).await();
                Collections.addAll(geocodingResults, results);
            } catch (IOException | InterruptedException | ApiException e) {
                log.error("Occurred error during the call on google API, reason: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        });
        return geocodingResults;
    }

    /**
     * Send request to the Google and receive response with geocoding.
     *
     * @param searchCoordinates - coordinates to search
     * @return GeocodingResults - return result from geocoding service
     */
    public AddressLatLngResponse getResultFromGeoCodeByCoordinates(LatLng searchCoordinates) {
        AddressLatLngResponse addressLatLngResponse = AddressLatLngResponse
            .builder()
            .latitude(searchCoordinates.lat)
            .longitude(searchCoordinates.lng)
            .build();

        locales.forEach(locale -> {
            try {
                GeocodingResult[] results = GeocodingApi.newRequest(context)
                    .latlng(searchCoordinates).language(locale.getLanguage()).await();
                if (locale.equals(localeUk)) {
                    addressLatLngResponse.setAddressUa(getAddressAddressComponents(results[0].addressComponents));
                }
                if (locale.equals(localeEn)) {
                    addressLatLngResponse.setAddressEn(getAddressAddressComponents(results[0].addressComponents));
                }
            } catch (IOException | InterruptedException | ApiException e) {
                log.error("Occurred error during the call on google API, reason: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        });
        return addressLatLngResponse;
    }

    private AddressResponse getAddressAddressComponents(AddressComponent[] addressComponents) {
        AddressResponse addressResponse = new AddressResponse();
        for (AddressComponent component : addressComponents) {
            List<AddressComponentType> componentTypes = Arrays.asList(component.types);
            if (componentTypes.contains(AddressComponentType.STREET_NUMBER)) {
                addressResponse.setHouseNumber(component.longName);
            }
            if (componentTypes.contains(AddressComponentType.ROUTE)) {
                addressResponse.setStreet(component.longName);
            }
            if (componentTypes.contains(AddressComponentType.LOCALITY)) {
                addressResponse.setCity(component.longName);
            }
            if (componentTypes.contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                addressResponse.setRegion(component.longName);
            }
            if (componentTypes.contains(AddressComponentType.COUNTRY)) {
                addressResponse.setCountry(component.longName);
            }
        }
        return addressResponse;
    }
}
