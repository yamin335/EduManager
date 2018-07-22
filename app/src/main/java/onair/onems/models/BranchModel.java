package onair.onems.models;

public class BranchModel {
    private long BrunchID;
    private String BrunchNo;
    private String BrunchName;
    private long ParentID;
    private long InstituteID;
    private String ParentBranch;
    private String Institute;

    public BranchModel() {
        this.BrunchID = 0;
        this.BrunchNo = "0";
        this.BrunchName = "";
        this.ParentID = 0;
        this.InstituteID = 0;
        this.ParentBranch = "";
        this.Institute = "";
    }

    public BranchModel(String BrunchID, String BrunchNo, String BrunchName,
                       String ParentID, String InstituteID, String ParentBranch,
                       String Institute) {
        if(BrunchID.equals("null")) {
            this.BrunchID = 0;
        } else {
            this.BrunchID = Long.parseLong(BrunchID);
        }

        this.BrunchNo = BrunchNo;
        this.BrunchName = BrunchName;

        if(ParentID.equals("null")) {
            this.ParentID = 0;
        } else {
            this.ParentID = Long.parseLong(ParentID);
        }

        if(InstituteID.equals("null")) {
            this.InstituteID = 0;
        } else {
            this.InstituteID = Long.parseLong(InstituteID);
        }

        this.ParentBranch = ParentBranch;
        this.Institute = Institute;
    }

    public long getBrunchID() {
        return BrunchID;
    }

    public void setBrunchID(String BrunchID) {
        if(BrunchID.equals("null")) {
            this.BrunchID = 0;
        } else {
            this.BrunchID = Long.parseLong(BrunchID);
        }
    }

    public String getBrunchNo() {
        return BrunchNo;
    }

    public void setBrunchNo(String BrunchNo) {
        this.BrunchNo = BrunchNo;
    }

    public String getBrunchName() {
        return BrunchName;
    }

    public void setBrunchName(String BrunchName) {
        this.BrunchName = BrunchName;
    }

    public long getParentID() {
        return ParentID;
    }

    public void setParentID(String ParentID) {
        if(ParentID.equals("null")) {
            this.ParentID = 0;
        } else {
            this.ParentID = Long.parseLong(ParentID);
        }
    }

    public long getInstituteID() {
        return InstituteID;
    }

    public void setInstituteID(String InstituteID) {
        if(InstituteID.equals("null")) {
            this.InstituteID = 0;
        } else {
            this.InstituteID = Long.parseLong(InstituteID);
        }
    }

    public String getParentBranch() {
        return ParentBranch;
    }

    public void setParentBranch(String ParentBranch) {
        this.ParentBranch = ParentBranch;
    }

    public String getInstitute() {
        return Institute;
    }

    public void setInstitute(String Institute) {
        this.Institute = Institute;
    }
}
