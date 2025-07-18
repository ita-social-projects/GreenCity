package greencity.converters;

import java.util.Arrays;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RatioConverter {

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
}
