package emailSender;

import java.util.Calendar;

public class Chronometer {
    /** Used locally to tag Logs */
    @SuppressWarnings("unused")
    private static final String TAG = Chronometer.class.getSimpleName();

    private long mTimestampStart;

    private long mSumElapsedTime;
    private int mNbElapsedTime;

    /**
     * Start the chronometer. Call getElapsedTime() when you want to get the time
     */
    public void start() {
        mTimestampStart = System.currentTimeMillis();
    }

    /**
     * @return the time (in ms) since the last call of start()
     */
    public long getElapsedTime() {
        return getElapsedTime(Calendar.MILLISECOND);
    }

    /**
     *
     * @param field
     *            is a Calandar field to determine the value return. It could be Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND and
     *            Calendar.MILLISECOND (default value
     *            if you select an other).
     * @return the time since the last call of start()
     */
    public long getElapsedTime(int field) {
        long diff = System.currentTimeMillis() - mTimestampStart;
        mSumElapsedTime += diff;
        mNbElapsedTime++;
        return diff / getFactorTime(field);
    }

    /**
     * @return the mean time return by each call of getElapsedTime() since the creation of this Chronometer or the last reset() called.
     */
    public long getMeanTime() {
        return getMeanTime(Calendar.MILLISECOND);
    }

    /**
     *
     * @param field
     *            is a Calandar field to determine the value return. It could be Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND and
     *            Calendar.MILLISECOND (default value
     *            if you select an other).
     * @return the mean time return by each call of getElapsedTime() since the creation of this Chronometer or the last reset() called.
     */
    public long getMeanTime(int field) {
        if (mNbElapsedTime > 0) {
            return (mSumElapsedTime / mNbElapsedTime) / getFactorTime(field);
        } else {
            return 0;
        }
    }

    /**
     * @return the number of getElapsedTime() called since the creation of this Chronometer or the last reset() called.
     */
    public int getNbElapsedTime() {
        return mNbElapsedTime;
    }

    private int getFactorTime(int field) {
        int factor;
        if (field == Calendar.HOUR) {
            factor = 60 * 60 * 1000;
        } else if (field == Calendar.MINUTE) {
            factor = 60 * 1000;
        } else if (field == Calendar.SECOND) {
            factor = 1000;
        } else {
            factor = 1;
        }
        return factor;
    }

    /**
     * Reset the mean time saved
     */
    public void resetMeanTime() {
        mNbElapsedTime = 0;
        mSumElapsedTime = 0;
    }
}