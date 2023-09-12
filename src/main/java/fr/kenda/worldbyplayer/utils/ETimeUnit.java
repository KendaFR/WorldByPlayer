package fr.kenda.worldbyplayer.utils;

public enum ETimeUnit {

    SECONDS(1), MINUTES(60 * SECONDS.toSecond), HOURS(60 * MINUTES.toSecond), DAYS(24 * HOURS.toSecond);

    private final int toSecond;

    ETimeUnit(int toSecond) {
        this.toSecond = toSecond;
    }

    /**
     * Calculates and returns the time remaining between two beats in millis
     *
     * @param timeReached Time to reach (in file of world)
     * @param currentTime current time
     * @return The number of days between
     */
    public static int remainingTimeBetween(long timeReached, long currentTime) {
        long timeBetweenInMillis = timeReached - currentTime;
        int timeBetweenInSeconds = (int) (timeBetweenInMillis / 1000);
        int days = 0;
        while (timeBetweenInSeconds > ETimeUnit.DAYS.toSecond) {
            timeBetweenInSeconds -= ETimeUnit.DAYS.toSecond;
            days++;
        }
        return days;
    }

    /**
     * Get a time to seconds
     *
     * @return Integer
     */
    public int getToSecond() {
        return toSecond;
    }

    /**
     * Get a time to millis
     *
     * @return second * 1000
     */
    public long toMillis() {
        return toSecond * 1000L;
    }
}
