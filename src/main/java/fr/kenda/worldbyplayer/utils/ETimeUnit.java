package fr.kenda.worldbyplayer.utils;

public enum ETimeUnit {

    SECONDS(1), MINUTES(60 * SECONDS.toSecond), HOURS(60 * MINUTES.toSecond), DAYS(24 * HOURS.toSecond);

    private final int toSecond;
    ETimeUnit(int toSecond) {
        this.toSecond = toSecond;
    }

    public int getToSecond() {
        return toSecond;
    }
    public long toMillis(){
        return toSecond * 1000L;
    }

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
}
