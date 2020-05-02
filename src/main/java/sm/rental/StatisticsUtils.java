package sm.rental;
import java.text.DecimalFormat;
import java.text.NumberFormat;

    /**
     * Calculate statistics without having to maintain arrays or lists in memory
     * @link http://stackoverflow.com/questions/43675485/calculating-standard-deviation-variance-in-java
     */
    public class StatisticsUtils {
        private static final String DEFAULT_FORMAT = "0.###";
        private static final NumberFormat FORMATTER = new DecimalFormat(DEFAULT_FORMAT);

        private double sum;
        private double squares;
        private double count;
        private double max;
        private double min;
        private double last;
        private double failureCount;
        private double resetCount;
        private String lastFailureReason;

        public StatisticsUtils() {
            reset();
        }

        public synchronized void addFailure(String reason) {
            this.lastFailureReason = reason;
            this.failureCount++;
        }

        public synchronized void addValue(double x) {
            sum += x;
            squares += x * x;
            min = ((x < min) ? x : min);
            max = ((x > max) ? x : max);
            last = x;
            ++count;

            // If the sum of squares exceeds double.MAX_VALUE, this means the
            // value has overflowed; reset the state back to zero and start again.
            // All previous calculations are lost.  (Better as all doubles?)
            if (squares < 0L) {
                reset();
            }
        }

        public synchronized void reset() {
            sum = 0L;
            squares = 0L;
            count = 0L;
            max = Double.MIN_VALUE;
            min = Double.MAX_VALUE;
            last = 0L;
            this.resetCount++;
        }

        public synchronized double getMean() {
            double mean = 0.0;
            if (count > 0L) {
                mean = (double) sum/count;
            }
            return mean;
        }

        public synchronized double getVariance() {
            double variance = 0.0;
            if (count > 1L) {
                variance = (squares-(double)sum*sum/count)/(count-1);
            }
            return variance;
        }

        public synchronized double getStdDev() {
            return Math.sqrt(this.getVariance());
        }

        public synchronized double getCount() {
            return count;
        }

        public synchronized double getSum() {
            return sum;
        }

        public synchronized double getMax() {
            return max;
        }

        public synchronized double getMin() {
            return min;
        }

        public synchronized double getLast() {
            return last;
        }

        public synchronized String getLastFailureReason() {
            return lastFailureReason;
        }

        public synchronized double getFailureCount() {
            return failureCount;
        }

        public synchronized double getResetCount() {
            return resetCount;
        }

        public String toString() {
            return "StatisticsUtils{" +
                    "sum=" + sum +
                    ", min=" + min +
                    ", max=" + max +
                    ", last=" + last +
                    ", squares=" + squares +
                    ", count=" + count +
                    ", mean=" + FORMATTER.format(getMean()) +
                    ", dev=" + FORMATTER.format(getStdDev()) +
                    '}';
        }
    }
