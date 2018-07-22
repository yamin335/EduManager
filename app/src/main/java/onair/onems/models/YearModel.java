package onair.onems.models;


public class YearModel {

    private String YearID;
    private String YearName;

    public YearModel() {
        this.YearID = "0";
        this.YearName = "";
    }

    public YearModel(String YearID, String YearName) {
        this.YearID = YearID;
        this.YearName = YearName;
    }

    public String getYearID() {
        return YearID;
    }

    public void setYearID(String YearID) {
        this.YearID = YearID;
    }

    public String getYearName() {
        return YearName;
    }

    public void setYearName(String YearName) {
        this.YearName = YearName;
    }
}