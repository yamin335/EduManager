package onair.onems.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/30/2017.
 */

public class AttendanceSheetModel {

    @SerializedName("SubAtdID")
    @Expose
    private long SubAtdID;
    @SerializedName("SubjectID")
    @Expose
    private long SubjectID;
    @SerializedName("SectionID")
    @Expose
    private long SectionID;
    @SerializedName("DepartmentID")
    @Expose
    private long DepartmentID;
    @SerializedName("MediumID")
    @Expose
    private long MediumID;
    @SerializedName("ShiftID")
    @Expose
    private long ShiftID;
    @SerializedName("ClassID")
    @Expose
    private long ClassID;
    @SerializedName("AtdUserID")
    @Expose
    private long AtdUserID;
    @SerializedName("AtdDate")
    @Expose
    private String AtdDate;
    @SerializedName("InstituteID")
    @Expose
    private long InstituteID;
    @SerializedName("CreateBy")
    @Expose
    private long CreateBy;
    @SerializedName("CreateOn")
    @Expose
    private String CreateOn;
    @SerializedName("CreatePc")
    @Expose
    private String CreatePc;
    @SerializedName("UpdateBy")
    @Expose
    private long UpdateBy;
    @SerializedName("UpdateOn")
    @Expose
    private String UpdateOn;
    @SerializedName("UpdatePc")
    @Expose
    private String UpdatePc;
    @SerializedName("IsDeleted")
    @Expose
    private int IsDeleted;
    @SerializedName("DeleteBy")
    @Expose
    private long DeleteBy;
    @SerializedName("DeleteOn")
    @Expose
    private String DeleteOn;
    @SerializedName("DeletePc")
    @Expose
    private String DeletePc;
    @SerializedName("attendenceArr")
    @Expose
    private ArrayList<AttendanceStudentModel> attendenceArr;

    public AttendanceSheetModel()
    {
        this.CreateBy = 0;
        this.CreateOn = "";
        this.CreatePc = "";
        this.UpdateBy = 0;
        this.UpdateOn = "";
        this.UpdatePc = "";
        this.IsDeleted = 0;
        this.DeleteBy = 0;
        this.DeleteOn = "";
        this.DeletePc = "";
    }

    public AttendanceSheetModel(String SubAtdID, String SubjectID, String SectionID, String DepartmentID,
                                String MediumID, String ShiftID, String ClassID, String AtdUserID, String AtdDate,
                                String InstituteID, String CreateBy, String CreateOn, String CreatePc,
                                String UpdateBy, String UpdateOn, String UpdatePc, String IsDeleted,
                                String DeleteBy, String DeleteOn, String DeletePc, ArrayList<AttendanceStudentModel> attendenceArr)
    {
        this.SubAtdID = Long.parseLong(SubAtdID);
        this.SubjectID = Long.parseLong(SubjectID);
        this.SectionID = Long.parseLong(SectionID);
        this.DepartmentID = Long.parseLong(DepartmentID);
        this.MediumID = Long.parseLong(MediumID);
        this.ShiftID = Long.parseLong(ShiftID);
        this.ClassID = Long.parseLong(ClassID);
        this.AtdUserID = Long.parseLong(AtdUserID);
        this.AtdDate = AtdDate;
        this.InstituteID = Long.parseLong(InstituteID);
        this.CreateBy = Long.parseLong(CreateBy);
        this.CreateOn = CreateOn;
        this.CreatePc = CreatePc;
        this.UpdateBy = Long.parseLong(UpdateBy);
        this.UpdateOn = UpdateOn;
        this.UpdatePc = UpdatePc;
        this.IsDeleted = Integer.parseInt(IsDeleted);
        this.DeleteBy = Long.parseLong(DeleteBy);
        this.DeleteOn = DeleteOn;
        this.DeletePc = DeletePc;
        this.attendenceArr = attendenceArr;
    }

    public void setSubAtdID(String SubAtdID)
    {
        this.SubAtdID = Long.parseLong(SubAtdID);
    }

    public void setSubjectID(String SubjectID)
    {
        this.SubjectID = Long.parseLong(SubjectID);
    }

    public void setSectionID(String SectionID)
    {
        this.SectionID = Long.parseLong(SectionID);
    }

    public void setDepartmentID(String DepartmentID)
    {
        this.DepartmentID = Long.parseLong(DepartmentID);
    }

    public void setMediumID(String MediumID)
    {
        this.MediumID = Long.parseLong(MediumID);
    }

    public void setShiftID(String ShiftID)
    {
        this.ShiftID = Long.parseLong(ShiftID);
    }

    public void setClassID(String ClassID)
    {
        this.ClassID = Long.parseLong(ClassID);
    }

    public void setAtdUserID(String AtdUserID)
    {
        this.AtdUserID = Long.parseLong(AtdUserID);
    }

    public void setAtdDate(String AtdDate)
    {
        this.AtdDate = AtdDate;
    }

    public void setInstituteID(String InstituteID)
    {
        this.InstituteID = Long.parseLong(InstituteID);
    }

    public void setCreateBy(String CreateBy)
    {
        this.CreateBy = Long.parseLong(CreateBy);
    }

    public void setCreateOn(String CreateOn)
    {
        this.CreateOn = CreateOn;
    }

    public void setCreatePc(String CreatePc)
    {
        this.CreatePc = CreatePc;
    }

    public void setUpdateBy(String UpdateBy)
    {
        this.UpdateBy = Long.parseLong(UpdateBy);
    }

    public void setUpdateOn(String UpdateOn)
    {
        this.UpdateOn = UpdateOn;
    }

    public void setUpdatePc(String UpdatePc)
    {
        this.UpdatePc = UpdatePc;
    }

    public void setIsDeleted(String IsDeleted)
    {
        this.IsDeleted = Integer.parseInt(IsDeleted);
    }

    public void setDeleteBy(String DeleteBy)
    {
        this.DeleteBy = Long.parseLong(DeleteBy);
    }

    public void setDeleteOn(String DeleteOn)
    {
        this.DeleteOn = DeleteOn;
    }

    public void setDeletePc(String DeletePc)
    {
        this.DeletePc = DeletePc;
    }

    public void setattendenceArr(ArrayList<AttendanceStudentModel> attendenceArr)
    {
        this.attendenceArr = attendenceArr;
    }

    public long getSubAtdID()
    {
        return SubAtdID;
    }

    public long getSubjectID()
    {
        return SubjectID;
    }

    public long getSectionID()
    {
        return SectionID;
    }

    public long getDepartmentID()
    {
        return DepartmentID;
    }

    public long getMediumID()
    {
        return MediumID;
    }

    public long getShiftID()
    {
        return ShiftID;
    }

    public long getClassID()
    {
        return ClassID;
    }

    public long getAtdUserID()
    {
        return AtdUserID;
    }

    public String getAtdDate()
    {
        return AtdDate;
    }

    public long getInstituteID()
    {
        return InstituteID;
    }

    public long getCreateBy()
    {
        return CreateBy;
    }

    public String getCreateOn()
    {
        return CreateOn;
    }

    public String getCreatePc()
    {
        return CreatePc;
    }

    public long getUpdateBy()
    {
        return UpdateBy;
    }

    public String getUpdateOn()
    {
        return UpdateOn;
    }

    public String getUpdatePc()
    {
        return UpdatePc;
    }

    public int getIsDeleted()
    {
        return IsDeleted;
    }

    public long getDeleteBy()
    {
        return DeleteBy;
    }

    public String getDeleteOn()
    {
        return DeleteOn;
    }

    public String getDeletePc()
    {
        return DeletePc;
    }

    public ArrayList<AttendanceStudentModel> getattendenceArr()
    {
        return attendenceArr;
    }
}
