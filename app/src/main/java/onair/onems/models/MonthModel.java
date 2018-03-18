package onair.onems.models;

/**
 * Created by onAir on 12-Mar-18.
 */

public class MonthModel {
    private int MonthID = 0;
    private String MonthName = "";

    public MonthModel(){
        super();
    }

    public MonthModel(String MonthID, String MonthName){
        if(MonthID.equals("null"))
        {
            this.MonthID = 0;
        }
        else
        {
            this.MonthID = Integer.parseInt(MonthID);
        }

        this.MonthName = MonthName;
    }

    public void setMonthID(String MonthID){
        if(MonthID.equals("null"))
        {
            this.MonthID = 0;
        }
        else
        {
            this.MonthID = Integer.parseInt(MonthID);
        }
    }

    public void setMonthName(String MonthName){
        this.MonthName = MonthName;
    }

    public int getMonthID(){
        return MonthID;
    }

    public String getMonthName(){
        return MonthName;
    }
}
