package onair.onems.models;


public class CommunicationTypeModel {

    private int CommunicationTypeID;
    private String CommunicationType;

    public CommunicationTypeModel() {
        this.CommunicationTypeID = 0;
        this.CommunicationType = "";
    }

    public CommunicationTypeModel(String CommunicationTypeID, String CommunicationType) {
        if(CommunicationTypeID.equals("null") || CommunicationTypeID.equals("")) {
            this.CommunicationTypeID = 0;
        } else {
            this.CommunicationTypeID = Integer.parseInt(CommunicationTypeID);
        }
        this.CommunicationType = CommunicationType;
    }

    public int getCommunicationTypeID() {
        return CommunicationTypeID;
    }

    public void setCommunicationTypeID(String CommunicationTypeID) {
        if(CommunicationTypeID.equals("null") || CommunicationTypeID.equals("")) {
            this.CommunicationTypeID = 0;
        } else {
            this.CommunicationTypeID = Integer.parseInt(CommunicationTypeID);
        }
    }

    public String getCommunicationType() {
        return CommunicationType;
    }

    public void setCommunicationType(String CommunicationType) {
        this.CommunicationType = CommunicationType;
    }
}