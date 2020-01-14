package com.dawandeapp.pillreminder.utilities;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

/**
 * first is hours
 * second is seconds
 */

public class TimePair extends Pair<Integer, Integer> implements Comparable<TimePair> {
    /**
     * Constructor for a Pair.
     *
     * @param hour  the first object in the Pair
     * @param minute the second object in the pair
     */
    public TimePair(@Nullable Integer hour, @Nullable Integer minute) {
        super(hour, minute);
    }

    public int compare(int h1, int m1, int h2, int m2) {
        return (h1 < h2) ? -1 :
                (h1 > h2) ? 1 :
                        (m1 < m2) ? -1 :
                                (m1 > m2) ? 1 : 0;
    }

    @Override
    public int compareTo(TimePair anotherTimePair) {
        return compare(first, second, anotherTimePair.first, anotherTimePair.second);
    }
}