package dataStore;

/**
 * @author yaw
 */
public class LinearComponent {
    private double conSlope; //con = construction
    private double conIntercept;
    private double rowSlope; //row = right of way
    private double rowIntercept;
    private double maxCapacity;

    private final DataStorer data;

    public LinearComponent(DataStorer data) {
        this.data = data;
    }

    public double getConSlope() {
        return conSlope;
    }

    public void setConSlope(double conSlope) {
        this.conSlope = conSlope;
    }

    public double getRowSlope() {
        return rowSlope;
    }

    public void setRowSlope(double rowSlope) {
        this.rowSlope = rowSlope;
    }

    public double getConIntercept() {
        return conIntercept;
    }

    public void setConIntercept(double conIntercept) {
        this.conIntercept = conIntercept;
    }

    public double getRowIntercept() {
        return rowIntercept;
    }

    public void setRowIntercept(double rowIntercept) {
        this.rowIntercept = rowIntercept;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
