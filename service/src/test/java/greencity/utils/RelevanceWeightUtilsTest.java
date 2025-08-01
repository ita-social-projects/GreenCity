package greencity.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RelevanceWeightUtilsTest {
    @Test
    void testConvertRatioFromString_NormalizedInput() {
        String input = "0.2:0.3:0.5";
        double[] expected = new double[] {0.2, 0.3, 0.5};

        double[] result = RelevanceWeightUtils.convertRatioFromString(input);

        assertArrayEquals(expected, result, 1e-6);
    }

    @Test
    void testConvertRatioFromString_UnnormalizedInput() {
        String input = "2:3:5";
        double[] result = RelevanceWeightUtils.convertRatioFromString(input);

        assertArrayEquals(new double[] {0.2, 0.3, 0.5}, result, 1e-6);
    }

    @Test
    void testConvertRatioFromString_SingleValue() {
        String input = "1";
        double[] result = RelevanceWeightUtils.convertRatioFromString(input);

        assertArrayEquals(new double[] {1.0}, result, 1e-6);
    }

    @Test
    void testConvertRatioFromString_ZeroValues() {
        String input = "0:0:0";
        double[] result = RelevanceWeightUtils.convertRatioFromString(input);

        assertArrayEquals(new double[] {0.0, 0.0, 0.0}, result, 1e-6);
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
}
