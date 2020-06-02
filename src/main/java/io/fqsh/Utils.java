package io.fqsh;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String convertTime(long nanoseconds) {
        String fillForm = "%.3f %s";

        if (nanoseconds < 1000) {
            return String.format(
                fillForm,
                (double) nanoseconds,
                "ns"
            );
        } else if (nanoseconds < 1_000_000) {
            return String.format(
                fillForm,
                (nanoseconds / 1_000.0),
                "µs"
            );
        } else if (nanoseconds < 1_000_000_000) {
            return String.format(
                fillForm,
                (nanoseconds / 1_000_000.0),
                "ms"
            );
        } else {
            long hours = TimeUnit.NANOSECONDS.toHours(nanoseconds);
            nanoseconds -= TimeUnit.HOURS.toNanos(hours);

            long minutes = TimeUnit.NANOSECONDS.toMinutes(nanoseconds);
            nanoseconds -= TimeUnit.MINUTES.toNanos(minutes);

            long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoseconds);
            nanoseconds -= TimeUnit.SECONDS.toNanos(seconds);

            long milliseconds = TimeUnit.NANOSECONDS.toMillis(nanoseconds);

            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
        }
    }

    public static boolean areSamplesInAscendingOrder(List<Integer> samples) {
        for (int i = 0; i < samples.size() - 1; i++) {
            if (samples.get(i) > samples.get(i + 1)) {
                return false;
            }
        }

        return true;
    }
}
