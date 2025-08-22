package greencity.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.BeanInitializationException;

class RelevanceWeightUtilsTest {
    private static final double PRECISION = 1e-6;

    @Test
    void testConvertRatioFromString_NormalizedInput() {
        String input = "0.2:0.3:0.5";
        double[] expected = new double[] {0.2, 0.3, 0.5};

        double[] result = RelevanceWeightUtils.parseAndValidateRatios(input, expected.length,
            "", true, true);

        assertArrayEquals(expected, result, PRECISION);
    }

    @Test
    void testConvertRatioFromString_SingleValue() {
        String input = "1";
        double[] expected = new double[] {1.0};
        double[] result = RelevanceWeightUtils.parseAndValidateRatios(input, expected.length,
            "", true, true);

        assertArrayEquals(expected, result, PRECISION);
    }

    @Test
    void testConvertRatioFromString_ZeroValuesWithoutSum() {
        String input = "0:0:0";
        double[] expected = new double[] {0.0, 0.0, 0.0};
        double[] result = RelevanceWeightUtils.parseAndValidateRatios(input, expected.length,
            "", true, false);

        assertArrayEquals(expected, result, 1e-6);
    }

    @Test
    void testConvertRatioFromString_ZeroValuesWithSum() {
        String input = "0:0:0";
        int length = 3;
        assertThrows(BeanInitializationException.class, () -> RelevanceWeightUtils.parseAndValidateRatios(input,
            length, "", true, true));
    }

    @Test
    void testNormalizeWeights_NormalCase() {
        double[] input = new double[] {2, 3, 5};
        double[] expected = new double[] {0.2, 0.3, 0.5};

        double[] result = RelevanceWeightUtils.normalizeWeights(input);

        assertArrayEquals(expected, result, 1e-6);
    }

    @Test
    void testNormalizeWeights_SumZero() {
        double[] input = new double[] {0, 0, 0};
        double[] expected = new double[] {0, 0, 0};

        double[] result = RelevanceWeightUtils.normalizeWeights(input);

        assertArrayEquals(expected, result, 1e-6);
    }

    @Test
    void testNormalizeWeights_NullInput() {
        double[] result = RelevanceWeightUtils.normalizeWeights(null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testDistributeCounts_NormalCase() {
        int total = 10;
        double[] weights = new double[] {0.2, 0.3, 0.5};

        int[] result = RelevanceWeightUtils.distributeCounts(total, weights);

        assertEquals(10, result[0] + result[1] + result[2]);
        assertArrayEquals(new int[] {2, 3, 5}, result);
    }

    @Test
    void testDistributeCounts_RoundingRequired() {
        int total = 7;
        double[] weights = new double[] {0.333, 0.333, 0.334};

        int[] result = RelevanceWeightUtils.distributeCounts(total, weights);

        assertEquals(7, result[0] + result[1] + result[2]);
        assertTrue(Math.abs(result[0] - result[1]) <= 1);
        assertTrue(Math.abs(result[1] - result[2]) <= 1);
        assertTrue(Math.abs(result[0] - result[2]) <= 1);
    }

    @Test
    void testDistributeCounts_EmptyWeights() {
        int[] result = RelevanceWeightUtils.distributeCounts(5, new double[0]);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testDistributeCounts_ZeroTotal() {
        int[] result = RelevanceWeightUtils.distributeCounts(0, new double[] {0.5, 0.5});

        assertArrayEquals(new int[] {0, 0}, result);
    }

    @Test
    void testIsLengthValid() {
        String input = "1";
        int length = 1;
        assertDoesNotThrow(() -> RelevanceWeightUtils.parseAndValidateRatios(input, length,
            "", true, true));
    }

    @Test
    void testIsLengthInvalid() {
        String input = "1";
        int length = 2;
        assertThrows(BeanInitializationException.class, () -> RelevanceWeightUtils.parseAndValidateRatios(input, length,
            "", true, true));
    }

    @ParameterizedTest
    @MethodSource("prepareForValidRatios")
    void testIsRatiosValuesNormalized(String ratios, Integer expectedSize, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> RelevanceWeightUtils.parseAndValidateRatios(ratios,
                expectedSize, "", true, false));
        } else {
            assertThrows(BeanInitializationException.class, () -> RelevanceWeightUtils.parseAndValidateRatios(ratios,
                expectedSize, "", true, false));
        }
    }

    @ParameterizedTest
    @MethodSource("prepareForNormalizedRatiosSum")
    void testIsRatiosSumNormalized(String ratios, Integer expectedSize, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> RelevanceWeightUtils.parseAndValidateRatios(ratios,
                expectedSize, "", false, true));
        } else {
            assertThrows(BeanInitializationException.class, () -> RelevanceWeightUtils.parseAndValidateRatios(ratios,
                expectedSize, "", false, true));
        }
    }

    private static Stream<Arguments> prepareForValidRatios() {
        return Stream.of(
            Arguments.of("0.4:0.6", 2, true),
            Arguments.of("-0.1:1.1", 2, false),
            Arguments.of("0.7:1.2", 2, false),
            Arguments.of("-0.1:0.2", 2, false),
            Arguments.of("", 0, false));
    }

    private static Stream<Arguments> prepareForNormalizedRatiosSum() {
        return Stream.of(
            Arguments.of("0.5:0.5", 2, true),
            Arguments.of("0.2:0.3:0.5", 3, true),
            Arguments.of("0.333333:0.333333:0.333334", 3, true),
            Arguments.of("0.6:0.5", 2, false),
            Arguments.of("0.3:0.3", 2, false),
            Arguments.of("", 0, false));
    }
}
