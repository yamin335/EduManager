package onair.onems.models;

import java.util.ArrayList;

/**
 * Created by User on 12/30/2017.
 */

public class AttendanceSheetModel {

    private long SubAtdID;

    private long SubjectID;

    private long SectionID;

    private long DepartmentID;

    private long MediumID;

    private long ShiftID;

    private long ClassID;

    private long AtdUserID;

    private String AtdDate;

    private long InstituteID;

//    private long CreateBy;

//    private String CreateOn;
//
//    private String CreatePc;
//
//    private long UpdateBy;
//
//    private String UpdateOn;
//
//    private String UpdatePc;
//
//    private int IsDeleted;
//
//    private long DeleteBy;
//
//    private String DeleteOn;
//
//    private String DeletePc;

    private long LoggedUserID;

    private ArrayList<AttendanceStudentModel> attendenceArr;

    public AttendanceSheetModel()
    {
//        this.CreateBy = 0;
//        this.CreateOn = "";
//        this.CreatePc = "";
//        this.UpdateBy = 0;
//        this.UpdateOn = "";
//        this.UpdatePc = "";
//        this.IsDeleted = 0;
//        this.DeleteBy = 0;
//        this.DeleteOn = "";
//        this.DeletePc = "";
        this.LoggedUserID = 0;
    }

    public AttendanceSheetModel(String SubAtdID, String SubjectID, String SectionID, String DepartmentID,
                                String MediumID, String ShiftID, String ClassID, String AtdUserID, String AtdDate,
                                String InstituteID, String CreateBy, String CreateOn, String CreatePc,
                                String UpdateBy, String UpdateOn, String UpdatePc, String IsDeleted,
                                String DeleteBy, String DeleteOn, String DeletePc, ArrayList<AttendanceStudentModel> attendenceArr)
    {
        if(SubAtdID.equals("null"))
        {
            this.SubAtdID = 0;
        }
        else
        {
            this.SubAtdID = Long.parseLong(SubAtdID);
        }

        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }

        if(SectionID.equals("null"))
        {
            this.SectionID = -2;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }

        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = -2;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }

        if(MediumID.equals("null"))
        {
            this.MediumID = -2;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }

        if(ShiftID.equals("null"))
        {
            this.ShiftID = -2;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }

        if(ClassID.equals("null"))
        {
            this.ClassID = -2;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }

        if(AtdUserID.equals("null"))
        {
            this.AtdUserID = 0;
        }
        else
        {
            this.AtdUserID = Long.parseLong(AtdUserID);
        }

        this.AtdDate = AtdDate;

        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }
//
//        if(CreateBy.equals("null"))
//        {
//            this.CreateBy = 0;
//        }
//        else
//        {
//            this.CreateBy = Long.parseLong(CreateBy);
//        }
//
//        this.CreateOn = CreateOn;
//        this.CreatePc = CreatePc;
//
//        if(UpdateBy.equals("null"))
//        {
//            this.UpdateBy = 0;
//        }
//        else
//        {
//            this.UpdateBy = Long.parseLong(UpdateBy);
//        }
//
//        this.UpdateOn = UpdateOn;
//        this.UpdatePc = UpdatePc;
//
//        if(IsDeleted.equals("null"))
//        {
//            this.IsDeleted = 0;
//        }
//        else
//        {
//            this.IsDeleted = Integer.parseInt(IsDeleted);
//        }
//
//        if(DeleteBy.equals("null"))
//        {
//            this.DeleteBy = 0;
//        }
//        else
//        {
//            this.DeleteBy = Long.parseLong(DeleteBy);
//        }
//
//        this.DeleteOn = DeleteOn;
//        this.DeletePc = DeletePc;
        this.attendenceArr = attendenceArr;
    }

    public void setSubAtdID(String SubAtdID)
    {
        if(SubAtdID.equals("null"))
        {
            this.SubAtdID = 0;
        }
        else
        {
            this.SubAtdID = Long.parseLong(SubAtdID);
        }
    }

    public void setSubjectID(String SubjectID)
    {
        if(SubjectID.equals("null"))
        {
            this.SubjectID = 0;
        }
        else
        {
            this.SubjectID = Long.parseLong(SubjectID);
        }
    }

    public void setSectionID(String SectionID)
    {
        if(SectionID.equals("null"))
        {
            this.SectionID = -2;
        }
        else
        {
            this.SectionID = Long.parseLong(SectionID);
        }
    }

    public void setDepartmentID(String DepartmentID)
    {
        if(DepartmentID.equals("null"))
        {
            this.DepartmentID = -2;
        }
        else
        {
            this.DepartmentID = Long.parseLong(DepartmentID);
        }
    }

    public void setMediumID(String MediumID)
    {
        if(MediumID.equals("null"))
        {
            this.MediumID = -2;
        }
        else
        {
            this.MediumID = Long.parseLong(MediumID);
        }
    }

    public void setShiftID(String ShiftID)
    {
        if(ShiftID.equals("null"))
        {
            this.ShiftID = -2;
        }
        else
        {
            this.ShiftID = Long.parseLong(ShiftID);
        }
    }

    public void setClassID(String ClassID)
    {
        if(ClassID.equals("null"))
        {
            this.ClassID = -2;
        }
        else
        {
            this.ClassID = Long.parseLong(ClassID);
        }
    }

    public void setAtdUserID(String AtdUserID)
    {
        if(AtdUserID.equals("null"))
        {
            this.AtdUserID = 0;
        }
        else
        {
            this.AtdUserID = Long.parseLong(AtdUserID);
        }
    }

    public void setAtdDate(String AtdDate)
    {
        this.AtdDate = AtdDate;
    }

    public void setInstituteID(String InstituteID)
    {
        if(InstituteID.equals("null"))
        {
            this.InstituteID = 0;
        }
        else
        {
            this.InstituteID = Long.parseLong(InstituteID);
        }
    }

    public void setLoggedUserID(String LoggedUserID)
    {
        this.LoggedUserID = Long.parseLong(LoggedUserID);
    }
//
//    public void setCreateBy(String CreateBy)
//    {
//        if(CreateBy.equals("null"))
//        {
//            this.CreateBy = 0;
//        }
//        else
//        {
//            this.CreateBy = Long.parseLong(CreateBy);
//        }
//    }
//
//    public void setCreateOn(String CreateOn)
//    {
//        this.CreateOn = CreateOn;
//    }
//
//    public void setCreatePc(String CreatePc)
//    {
//        this.CreatePc = CreatePc;
//    }
//
//    public void setUpdateBy(String UpdateBy)
//    {
//        if(UpdateBy.equals("null"))
//        {
//            this.UpdateBy = 0;
//        }
//        else
//        {
//            this.UpdateBy = Long.parseLong(UpdateBy);
//        }
//    }
//
//    public void setUpdateOn(String UpdateOn)
//    {
//        this.UpdateOn = UpdateOn;
//    }
//
//    public void setUpdatePc(String UpdatePc)
//    {
//        this.UpdatePc = UpdatePc;
//    }
//
//    public void setIsDeleted(String IsDeleted)
//    {
//        if(IsDeleted.equals("null"))
//        {
//            this.IsDeleted = 0;
//        }
//        else
//        {
//            this.IsDeleted = Integer.parseInt(IsDeleted);
//        }
//    }
//
//    public void setDeleteBy(String DeleteBy)
//    {
//        if(DeleteBy.equals("null"))
//        {
//            this.DeleteBy = 0;
//        }
//        else
//        {
//            this.DeleteBy = Long.parseLong(DeleteBy);
//        }
//    }
//
//    public void setDeleteOn(String DeleteOn)
//    {
//        this.DeleteOn = DeleteOn;
//    }
//
//    public void setDeletePc(String DeletePc)
//    {
//        this.DeletePc = DeletePc;
//    }

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

    public long getLoggedUserID()
    {
        return LoggedUserID;
    }

//    public long getCreateBy()
//    {
//        return CreateBy;
//    }
//
//    public String getCreateOn()
//    {
//        return CreateOn;
//    }
//
//    public String getCreatePc()
//    {
//        return CreatePc;
//    }
//
//    public long getUpdateBy()
//    {
//        return UpdateBy;
//    }
//
//    public String getUpdateOn()
//    {
//        return UpdateOn;
//    }
//
//    public String getUpdatePc()
//    {
//        return UpdatePc;
//    }
//
//    public int getIsDeleted()
//    {
//        return IsDeleted;
//    }
//
//    public long getDeleteBy()
//    {
//        return DeleteBy;
//    }
//
//    public String getDeleteOn()
//    {
//        return DeleteOn;
//    }
//
//    public String getDeletePc()
//    {
//        return DeletePc;
//    }

    public ArrayList<AttendanceStudentModel> getattendenceArr()
    {
        return attendenceArr;
    }
}
