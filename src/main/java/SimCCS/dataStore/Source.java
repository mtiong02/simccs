package dataStore;

/**
 * @author yaw
 */
public class Source {
    private int cellNum;
    private double openingCost;
    private double omCost;
    private double captureCost;
    private double productionRate;
    private String label;

    private final DataStorer data;

    private double remainingCapacity;    //Heuristic

    public Source(DataStorer data) {
        this.data = data;
    }

    public void setOpeningCost(double openingCost) {
        this.openingCost = openingCost;
    }

    public void setOMCost(double omCost) {
        this.omCost = omCost;
    }

    // Heuristic
    public double getRemainingCapacity() {
        return remainingCapacity;
    }

    // Heuristic
    public void setRemainingCapacity(double remaingCapacity) {
        this.remainingCapacity = remaingCapacity;
    }

    public int getCellNum() {
        return cellNum;
    }

    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getOpeningCost(double crf) {
        return crf * openingCost + omCost;
    }

    public double getCaptureCost() {
        return captureCost;
    }

    public void setCaptureCost(double captureCost) {
        this.captureCost = captureCost;
    }

    public double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(double productionRates) {
        this.productionRate = productionRates;
    }

    public boolean isSimplified() {
        return openingCost == 0 && omCost == 0;
    }
}
