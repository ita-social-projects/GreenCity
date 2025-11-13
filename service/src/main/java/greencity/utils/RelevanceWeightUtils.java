package greencity.utils;

import static greencity.constant.ErrorMessage.INVALID_RATIO_FORMAT;
import static greencity.constant.ErrorMessage.INVALID_RATIO_SUM;
import static greencity.constant.ErrorMessage.INVALID_RATIO_VALUE;
import greencity.dto.cache.CachedRelevancePools;
import java.util.Arrays;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.BeanInitializationException;

@UtilityClass
public class RelevanceWeightUtils {
    private static final double PRECISION = 1e-6;

    /**
     * Parses given ratio string and validates it.
     *
     * @param ratioString           string representing ratio divided by ":"
     * @param expectedLength        expected length of the ratio array
     * @param exceptionMessageType  exception message to be thrown if the ratio is
     *                              invalid
     * @param checkValuesNormalized flag indicating whether to check if the ratio
     *                              values are normalized
     * @param checkSumNormalized    flag indicating whether to check if the sum of
     *                              the ratio values is normalized
     *
     * @return validated ratio array
     * @throws BeanInitializationException if the ratio is invalid
     */
    public static double[] parseAndValidateRatios(String ratioString,
        int expectedLength,
        String exceptionMessageType,
        boolean checkValuesNormalized,
        boolean checkSumNormalized) {
        try {
            double[] ratios = RelevanceWeightUtils.convertRatioFromString(ratioString);

            if (ratios.length != expectedLength) {
                throw new BeanInitializationException(String.join(" ", exceptionMessageType,
                    INVALID_RATIO_FORMAT.formatted(expectedLength,
                        String.join(":", Collections.nCopies(expectedLength, "n")),
                        ratioString)));
            }

            if (checkValuesNormalized && !RelevanceWeightUtils.isRatiosNormalized(ratios)) {
                throw new BeanInitializationException(String.join(" ", exceptionMessageType,
                    INVALID_RATIO_VALUE.formatted(Arrays.toString(ratios))));
            }

            if (checkSumNormalized && !RelevanceWeightUtils.isRatiosSumNormalized(ratios)) {
                throw new BeanInitializationException(String.join(" ", exceptionMessageType,
                    INVALID_RATIO_SUM.formatted(Arrays.toString(ratios))));
            }

            return ratios;
        } catch (NumberFormatException e) {
            throw new BeanInitializationException(String.join(" ", exceptionMessageType,
                INVALID_RATIO_FORMAT.formatted(expectedLength,
                    String.join(":", Collections.nCopies(expectedLength, "n")),
                    ratioString)));
        }
    }

    /**
     * Rescale weights to sum up to 1.
     *
     * @param weights weights to normalize (sum of elements may be less than 1)
     * @return normalized weights (sum of elements is always equal to 1)
     */
    public static double[] normalizeWeights(double[] weights) {
        if (weights == null) {
            return new double[0];
        }
        double sum = Arrays.stream(weights).sum();
        if (sum == 0.0) {
            return new double[weights.length];
        }
        return Arrays.stream(weights).map(w -> w / sum).toArray();
    }

    /**
     * Rescale weights to sum up to 1, considering pools content.
     *
     * @param weights weights to normalize (sum of elements may be less than 1)
     * @param pools collections of news with different relevance strength
     * @return normalized weights (sum of elements is always equal to 1)
     */
    public static double[] normalizeWeights(double[] weights, CachedRelevancePools pools) {
        double[] normalized = normalizeWeights(weights);
        int[] poolsNewsCounts = new int[] {pools.relevantStrongNewsIds().size(), pools.relevantWeakNewsIds().size(),
            pools.nonRelevantNewsIds().size()};
        for (int i = 0; i < normalized.length; i++) {
            if (poolsNewsCounts[i] == 0) {
                double dividedWeight = normalized[i] / (normalized.length - i - 1);
                for (int j = i + 1; j < normalized.length; j++) {
                    normalized[j] += dividedWeight;
                }
                normalized[i] = 0;
            }
        }
        return normalized;
    }

    /**
     * Rescale counts by ratio to sum up to totalCount.
     *
     * @param totalCount        final count that must be met
     * @param normalizedWeights ratio in weights representation
     * @return counts scaled by ratio and final count
     */
    public static int[] distributeCounts(int totalCount, double[] normalizedWeights) {
        int[] result = new int[normalizedWeights.length];
        double[] exact = new double[normalizedWeights.length];
        int currentTotal = 0;

        for (int i = 0; i < normalizedWeights.length; i++) {
            exact[i] = normalizedWeights[i] * totalCount;
            result[i] = (int) Math.floor(exact[i]);
            currentTotal += result[i];
        }

        int remaining = totalCount - currentTotal;
        while (remaining > 0) {
            int bestIndex = -1;
            double maxFraction = -1;

            for (int i = 0; i < exact.length; i++) {
                double fraction = exact[i] - result[i];
                if (fraction > maxFraction) {
                    maxFraction = fraction;
                    bestIndex = i;
                }
            }

            if (bestIndex != -1) {
                result[bestIndex]++;
                remaining--;
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * Converts string ratio divided by ":" to double array.
     *
     * @param ratio string ratio
     * @return double array representation of the ratio
     */
    private static double[] convertRatioFromString(String ratio) {
        return Arrays.stream(ratio.split(":"))
            .mapToDouble(Double::parseDouble)
            .toArray();
    }

    /**
     * Checks if given ratios values are normalized.
     *
     * <p>
     * A valid ratio is an array of doubles where each element is greater than 0 and
     * less than or equal to 1.
     * </p>
     *
     * @param ratios array of ratios to check
     * @return true if the ratios are valid, false otherwise
     */
    private static boolean isRatiosNormalized(double[] ratios) {
        boolean anyNegative = Arrays.stream(ratios).anyMatch(w -> w < 0);
        boolean anyGreaterThanOne = Arrays.stream(ratios).anyMatch(w -> w > 1);
        return !anyNegative && !anyGreaterThanOne;
    }

    /**
     * Checks if given ratios array sum is normalized.
     *
     * <p>
     * A normalized ratio sum is an array of doubles where each element is greater
     * than 0 and less than or equal to 1, and the sum of all elements is equal to
     * 1.
     * </p>
     *
     * @param ratios array of ratios to check
     * @return true if the ratios sum is normalized, false otherwise
     */
    private static boolean isRatiosSumNormalized(double[] ratios) {
        double sum = Arrays.stream(ratios).sum();
        return Math.abs(sum - 1.0) < PRECISION;
    }
}
