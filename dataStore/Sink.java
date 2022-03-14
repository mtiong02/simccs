package dataStore;

/**
 * @author yaw
 */
public class Sink {
    private int cellNum;
    private double openingCost;
    private double omCost;
    private double wellOpeningCost;
    private double wellOMCost;
    private double injectionCost;
    private double wellCapacity;
    private double capacity;
    private String label;

    private final DataStorer data;

    private double remainingCapacity;    //Heuristic
    private int numWells;   //Heuristic

    public Sink(DataStorer data) {
        this.data = data;
    }

    public void setOpeningCost(double openingCost) {
        this.openingCost = openingCost;
    }

    public void setOMCost(double omCost) {
        this.omCost = omCost;
    }

    public void setWellOpeningCost(double wellOpeningCost) {
        this.wellOpeningCost = wellOpeningCost;
    }

    public void setWellOMCost(double wellOMCost) {
        this.wellOMCost = wellOMCost;
    }

    //Heuristic
    public int getNumWells() {
        return numWells;
    }

    // Heuristic
    public void setNumWells(int numWells) {
        this.numWells = numWells;
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

    public double getWellOpeningCost(double crf) {
        return crf * wellOpeningCost + wellOMCost;
    }

    public double getInjectionCost() {
        return injectionCost;
    }

    public void setInjectionCost(double injectionCost) {
        this.injectionCost = injectionCost;
    }

    public double getWellCapacity() {
        return wellCapacity;
    }

    public void setWellCapacity(double wellCapacity) {
        this.wellCapacity = wellCapacity;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public boolean isSimplified() {
        return openingCost == 0 && omCost == 0 && wellOpeningCost == 0 && wellOMCost == 0;
    }
}
