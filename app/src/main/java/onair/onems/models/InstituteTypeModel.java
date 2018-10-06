package onair.onems.models;


public class InstituteTypeModel {

    private int InstituteTypeID;
    private String InstituteTypeName;

    public InstituteTypeModel() {
        this.InstituteTypeID = 0;
        this.InstituteTypeName = "";
    }

    public InstituteTypeModel(String InstituteTypeID, String InstituteTypeName) {
        if(InstituteTypeID.equals("null") || InstituteTypeID.equals("")) {
            this.InstituteTypeID = 0;
        } else {
            this.InstituteTypeID = Integer.parseInt(InstituteTypeID);
        }
        this.InstituteTypeName = InstituteTypeName;
    }

    public int getInstituteTypeID() {
        return InstituteTypeID;
    }

    public void setInstituteTypeID(String InstituteTypeID) {
        if(InstituteTypeID.equals("null") || InstituteTypeID.equals("")) {
            this.InstituteTypeID = 0;
        } else {
            this.InstituteTypeID = Integer.parseInt(InstituteTypeID);
        }
    }

    public String getInstituteTypeName() {
        return InstituteTypeName;
    }

    public void setInstituteTypeName(String InstituteTypeName) {
        this.InstituteTypeName = InstituteTypeName;
    }
}