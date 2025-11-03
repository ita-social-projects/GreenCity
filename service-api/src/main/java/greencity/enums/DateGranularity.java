package greencity.enums;

public enum DateGranularity {
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    private final String granularity;

    DateGranularity(String granularity) {
        this.granularity = granularity;
    }

    @Override
    public String toString() {
        return granularity;
    }
}
