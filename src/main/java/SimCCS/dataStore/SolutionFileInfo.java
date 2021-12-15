package dataStore;

public class SolutionFileInfo {
    private String filename = "";
    private Integer current_time_interval = 0;
    private Integer total_time_intervals = 0;

    public SolutionFileInfo() {
        this.filename = "";
        this.current_time_interval = 0;
        this.total_time_intervals = 0;
    }

    public SolutionFileInfo(String filename) {
        this.filename = filename;
        this.current_time_interval = 0;
        this.total_time_intervals = 0;
    }

    public SolutionFileInfo(String filename, Integer current_interval, Integer total_intervals) {
        this.filename = filename;
        this.current_time_interval = current_interval;
        this.total_time_intervals = total_intervals;
    }

    public String getFilename() {
       return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getTotalTimeIntervals() {
        return this.total_time_intervals;
    }

    public void setTotalTimeIntervals(Integer intervals) {
        this.total_time_intervals = intervals;
    }

    public Integer getCurrentTimeInterval() {
        return this.current_time_interval;
    }

    public void setCurrentTimeInterval(Integer interval) {
        this.current_time_interval = interval;
    }
}
