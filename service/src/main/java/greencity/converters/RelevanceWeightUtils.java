package greencity.converters;

import java.util.Arrays;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelevanceWeightUtils {

    /**
     * Converts string ratio divided by ":" to double array.
     *
     * <p>
     * If sum of the parts of the ratio is not equal to 1, then the parts are
     * divided by the sum to get the correct ratio.
     * </p>
     *
     * @param ratio string ratio
     * @return double array representation of the ratio
     */
    public static double[] convertRatioFromString(String ratio) {
        double[] ratioDoubles = Arrays.stream(ratio.split(":"))
            .mapToDouble(Double::parseDouble)
            .toArray();
        double sum = Arrays.stream(ratioDoubles).sum();
        return sum <= 1 ? ratioDoubles : Arrays.stream(ratioDoubles)
            .map(d -> d / sum)
            .toArray();
    }

    public static double[] normalizeWeights(double[] weights) {
        double sum = Arrays.stream(weights).sum();
        if (sum == 0.0) {
            return new double[weights.length];
        }
        return Arrays.stream(weights).map(w -> w / sum).toArray();
    }
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


}
