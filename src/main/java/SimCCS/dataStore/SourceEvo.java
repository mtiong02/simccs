package dataStore;
import java.util.ArrayList;


/**
 * @author martin
 */

public class SourceEvo {
//    private int cellNum;

    public int numTimeInterval;
    public ArrayList<Integer> SourceEvo_indicator = new ArrayList<Integer>();
    private final DataStorer data;

    public SourceEvo(DataStorer data) {
        this.data = data;
    }

    public void setNumTimeInterval(int numTimeInterval) {
        this.numTimeInterval = numTimeInterval;
    }

    public void setSourceEvo_indicator(ArrayList<Integer> SourceEvo_indicator) {
        this.SourceEvo_indicator = SourceEvo_indicator;
    }

    public int getNumTimeInterval() {
        return numTimeInterval;
    }

    public ArrayList<Integer> getSourceEvo_indicator() {
        return SourceEvo_indicator;
    }
}
