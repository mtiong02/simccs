package dataStore;

/**
 * @author martin
 */
public class Sinkcredit {
    
    private int id_sinkcredit;
    private double sinkcredit;

    private final DataStorer data;
    
    public Sinkcredit(DataStorer data) {
        this.data = data;
    }

    public void setId_sinkcredit(int id_sinkcredit) {
        this.id_sinkcredit = id_sinkcredit;
    }

    public void setSinkcredit(double sinkcredit) {
        this.sinkcredit = sinkcredit;
    }
    
    public int getId_sinkcredit() {
        return id_sinkcredit;
    }

    public double getSinkcredit() {
        return sinkcredit;
    }

}
