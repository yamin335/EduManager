package onair.onems.models;

import java.util.ArrayList;

public class WorkOrderModel {
    private int WrkOrdrID;
    private String WrkOrdrNo;
    private int AgentID;
    private int NewClientID;
    private String WrkOrdrDate;
    private double WrkOrdrValue;
    private String InstlmntStrtDate;
    private int NoOfInstlmnt;
    private int PaymentMethodID;
    private String PaymentMethodName;
    private String SalesPerson;
    private ArrayList<DocumentModel> DocumentList;

    public WorkOrderModel() {
        WrkOrdrID = 0;
        WrkOrdrNo = "";
        AgentID = 0;
        NewClientID = 0;
        WrkOrdrDate = "";
        WrkOrdrValue = 0.0;
        InstlmntStrtDate = "";
        NoOfInstlmnt = 0;
        PaymentMethodID = 0;
        PaymentMethodName = "";
        SalesPerson = "";
        DocumentList = new ArrayList<>();
    }

    public void setWrkOrdrID(String WrkOrdrID) {
        if(WrkOrdrID.equals("null")) {
            this.WrkOrdrID = 0;
        } else {
            this.WrkOrdrID = Integer.parseInt(WrkOrdrID);
        }
    }

    public int getWrkOrdrID() {
        return WrkOrdrID;
    }

    public void setWrkOrdrNo(String wrkOrdrNo) {
        WrkOrdrNo = wrkOrdrNo;
    }

    public String getWrkOrdrNo() {
        return WrkOrdrNo;
    }

    public void setAgentID(String AgentID) {
        if(AgentID.equals("null")) {
            this.AgentID = 0;
        } else {
            this.AgentID = Integer.parseInt(AgentID);
        }
    }

    public int getAgentID() {
        return AgentID;
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

    public void setWrkOrdrDate(String wrkOrdrDate) {
        WrkOrdrDate = wrkOrdrDate;
    }

    public String getWrkOrdrDate() {
        return WrkOrdrDate;
    }

    public void setWrkOrdrValue(String WrkOrdrValue) {
        if(WrkOrdrValue.equals("null")) {
            this.WrkOrdrValue = 0;
        } else {
            this.WrkOrdrValue = Double.parseDouble(WrkOrdrValue);
        }
    }

    public double getWrkOrdrValue() {
        return WrkOrdrValue;
    }

    public void setInstlmntStrtDate(String instlmntStrtDate) {
        InstlmntStrtDate = instlmntStrtDate;
    }

    public String getInstlmntStrtDate() {
        return InstlmntStrtDate;
    }

    public void setNoOfInstlmnt(String NoOfInstlmnt) {
        if(NoOfInstlmnt.equals("null")) {
            this.NoOfInstlmnt = 0;
        } else {
            this.NoOfInstlmnt = Integer.parseInt(NoOfInstlmnt);
        }
    }

    public int getNoOfInstlmnt() {
        return NoOfInstlmnt;
    }

    public void setPaymentMethodID(String PaymentMethodID) {
        if(PaymentMethodID.equals("null")) {
            this.PaymentMethodID = 0;
        } else {
            this.PaymentMethodID = Integer.parseInt(PaymentMethodID);
        }
    }

    public int getPaymentMethodID() {
        return PaymentMethodID;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        PaymentMethodName = paymentMethodName;
    }

    public String getPaymentMethodName() {
        return PaymentMethodName;
    }

    public void setSalesPerson(String salesPerson) {
        SalesPerson = salesPerson;
    }

    public String getSalesPerson() {
        return SalesPerson;
    }

    public void setDocumentList(ArrayList<DocumentModel> documentList) {
        DocumentList = documentList;
    }

    public ArrayList<DocumentModel> getDocumentList() {
        return DocumentList;
    }
}
