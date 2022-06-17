package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import greencity.exception.exceptions.GoogleApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleApiService {
    private final GeoApiContext context;
    private static final List<Locale> locales = List.of(new Locale("uk"), new Locale("en"));

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
                throw new GoogleApiException(e.getMessage());
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
    public List<GeocodingResult> getResultFromGeoCodeByCoordinates(LatLng searchCoordinates) {
        List<GeocodingResult> geocodingResults = new ArrayList<>();
        locales.forEach(locale -> {
            try {
                GeocodingResult[] results = GeocodingApi.newRequest(context)
                    .latlng(searchCoordinates).language(locale.getLanguage()).await();
                Collections.addAll(geocodingResults, results);
            } catch (IOException | InterruptedException | ApiException e) {
                log.error("Occurred error during the call on google API, reason: {}", e.getMessage());
                throw new GoogleApiException(e.getMessage());
            }
        });
        return geocodingResults;
    }
}
