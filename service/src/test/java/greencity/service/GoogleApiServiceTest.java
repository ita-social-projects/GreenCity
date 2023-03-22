package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.ApiConfig;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import greencity.ModelUtils;
import static org.mockito.Mockito.when;

import org.aspectj.weaver.ast.Var;
import org.awaitility.reflect.WhiteboxImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleApiServiceTest {

    @Mock
    GeocodingApiRequest geocodingApiRequest;
    @InjectMocks
    GoogleApiService googleApiService;

    @Test
    void getResultFromGeoCode() {
    }
    /*
     * @BeforeEach void init() throws NoSuchFieldException, IllegalAccessException{
     * var lookup = MethodHandles.privateLookupIn(Field.class,
     * MethodHandles.lookup()); VarHandle modifiers =
     * lookup.findVarHandle(Field.class, "modifiers", int.class); ApiConfig
     * apiConfig = new ApiConfig("/maps/api/geocode/json"); Field config =
     * GeocodingApiRequest.class.getDeclaredField("API_CONFIG");
     * modifiers.set(config, config.getModifiers() & ~Modifier.FINAL);
     * config.setAccessible(true); config.set(null, apiConfig); }
     */

    @Test
    void getResultFromGeoCodeByCoordinates()
        throws IOException, InterruptedException, ApiException {
        LatLng searchCoordinates = new LatLng(50.613864770394684, 26.258814294011582);
        String address1 = "fake address";
        GeocodingResult geocod = ModelUtils.getGeocodingResult().get(0);
        GeocodingResult[] geocodingResponse = new GeocodingResult[] {geocod};

        when(geocodingApiRequest.latlng(searchCoordinates)).thenReturn(geocodingApiRequest);
        when(geocodingApiRequest.language(eq(anyString()))).thenReturn(geocodingApiRequest);
        when(geocodingApiRequest.await()).thenReturn(geocodingResponse);
        List<GeocodingResult> geocodingResults = googleApiService
            .getResultFromGeoCodeByCoordinates(searchCoordinates);
        assertEquals(address1, geocodingResults.get(0).formattedAddress);
    }

}