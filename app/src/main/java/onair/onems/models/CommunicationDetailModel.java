package onair.onems.models;

import java.util.ArrayList;

public class CommunicationDetailModel {
    private int ComDetailID = 0;
    private int NewClientID = 0;
    private String CommunicationType = "";
    private int CommunicationTypeID = 0;
    private String Priority = "";
    private int PriorityID = 0;
    private String CommunicationDate = "";
    private String NextMeetingDate = "";
    private String CommunicationDetails = "";
    private ArrayList<DocumentModel> Document;

    public CommunicationDetailModel() {
        Document = new ArrayList<>();
    }

    public void setComDetailID(String ComDetailID) {
        if(ComDetailID.equals("null")) {
            this.ComDetailID = 0;
        } else {
            this.ComDetailID = Integer.parseInt(ComDetailID);
        }
    }

    public int getComDetailID() {
        return ComDetailID;
    }

    public void setNewClientID(String NewClientID) {
        if(NewClientID.equals("null")) {
            this.NewClientID = 0;
        } else {
            this.NewClientID = Integer.parseInt(NewClientID);
        }
    }

    public int getNewClientID() {
        return NewClientID;
    }

    public void setCommunicationType(String communicationType) {
        CommunicationType = communicationType;
    }

    public String getCommunicationType() {
        return CommunicationType;
    }

    public void setCommunicationTypeID(String CommunicationTypeID) {
        if(CommunicationTypeID.equals("null")) {
            this.CommunicationTypeID = 0;
        } else {
            this.CommunicationTypeID = Integer.parseInt(CommunicationTypeID);
        }
    }

    public int getCommunicationTypeID() {
        return CommunicationTypeID;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriorityID(String PriorityID) {
        if(PriorityID.equals("null")) {
            this.PriorityID = 0;
        } else {
            this.PriorityID = Integer.parseInt(PriorityID);
        }
    }

    public int getPriorityID() {
        return PriorityID;
    }

    public void setCommunicationDate(String CommunicationDate) {
        this.CommunicationDate = CommunicationDate;
    }

    public String getCommunicationDate() {
        return CommunicationDate;
    }

    public void setNextMeetingDate(String NextMeetingDate) {
        this.NextMeetingDate = NextMeetingDate;
    }

    public String getNextMeetingDate() {
        return NextMeetingDate;
    }

    public void setCommunicationDetails(String CommunicationDetails) {
        this.CommunicationDetails = CommunicationDetails;
    }

    public String getCommunicationDetails() {
        return CommunicationDetails;
    }

    public void setDocument(ArrayList<DocumentModel> document) {
        Document = document;
    }

    public ArrayList<DocumentModel> getDocument() {
        return Document;
    }
}
