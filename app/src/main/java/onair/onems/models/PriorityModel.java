package onair.onems.models;


public class PriorityModel {

    private int PriorityID;
    private String Priority;

    public PriorityModel() {
        this.PriorityID = 0;
        this.Priority = "";
    }

    public PriorityModel(String PriorityID, String Priority) {
        if(PriorityID.equals("null") || PriorityID.equals("")) {
            this.PriorityID = 0;
        } else {
            this.PriorityID = Integer.parseInt(PriorityID);
        }
        this.Priority = Priority;
    }

    public int getPriorityID() {
        return PriorityID;
    }

    public void setPriorityID(String PriorityID) {
        if(PriorityID.equals("null") || PriorityID.equals("")) {
            this.PriorityID = 0;
        } else {
            this.PriorityID = Integer.parseInt(PriorityID);
        }
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String Priority) {
        this.Priority = Priority;
    }
}