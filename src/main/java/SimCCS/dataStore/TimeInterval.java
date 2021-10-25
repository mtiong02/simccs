package dataStore;

import java.util.InputMismatchException;
import java.util.Vector;

public class TimeInterval {
    private Vector<Vector<Double>> intervals;

    public TimeInterval() {
        this.intervals = new Vector<Vector<Double>>(0);
    }

    public TimeInterval(double[] values, double[] years) throws InputMismatchException {
        if (values.length != years.length) {
            throw new InputMismatchException("values and years differ in size");
        }

        this.intervals = new Vector<Vector<Double>>(0);

        for (int i = 0; i < values.length; i++) {
            this.addInterval(years[i], values[i]);
        }
    }

    public void addInterval(double years, double value) {
        Vector<Double> tmp = new Vector<Double>(2);
        tmp.add(years);
        tmp.add(value);
        this.intervals.add(tmp);
    }

    public Vector<Double> getInterval(int index) {
        return this.intervals.get(index);
    }

    public int numIntervals() {
        return this.intervals.size();
    }

    public double getYears(int interval_id) {
        return this.getInterval(interval_id).get(0);
    }

    public double getValue(int interval_id) {
        return this.getInterval(interval_id).get(1);
    }

    public double sumValues() {
        double tmp = 0;
        for (int i = 0; i < this.numIntervals(); i++) {
            tmp += this.getValue(i);
        }
        return tmp;
    }

    public double sumYears() {
        double tmp = 0;
        for (int i = 0; i < this.numIntervals(); i++) {
            tmp += this.getYears(i);
        }
        return tmp;
    }
}
