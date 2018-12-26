package onair.onems.Services;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import onair.onems.models.CommunicationDetailModel;
import onair.onems.models.WorkOrderModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitNetworkService {

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getLoginInformation/{userName}/{password}")
    Observable<String> getLoginInformation(@Path("userName") String userName, @Path("password") String password);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getDashBoardCmnUserType/{UserID}")
    Observable<String> getDashBoardCmnUserType(@Path("UserID") String UserID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetDashUserDetail/{UserID}/{InstituteID}")
    Observable<String> spGetDashUserDetail(@Path("UserID") String UserID, @Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getFcmTokenByUserID/{UserID}")
    Observable<String> getFcmTokenByUserID(@Path("UserID") String UserID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/setFcmToken")
    Observable<String> setFcmToken(@Body JsonObject tokenJsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getTotalTeacherAndTotalAttendence/{today}/{InstituteID}/{TeacherTypeID}")
    Observable<String> getTotalTeacherAndTotalAttendence(@Path("today") String today, @Path("InstituteID") long InstituteID, @Path("TeacherTypeID") String TeacherTypeID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getTotalStudentAndTotalAttendenceDashBoard/{today}/{InstituteID}/{StudentTypeID}")
    Observable<String> getTotalStudentAndTotalAttendenceDashBoard(@Path("today") String today, @Path("InstituteID") long InstituteID, @Path("StudentTypeID") String StudentTypeID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsShift/{InstituteID}")
    Observable<String> getInsShift(@Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInstituteMediumDdl/{InstituteID}")
    Observable<String> getInstituteMediumDdl(@Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/MediumWiseClassDDL/{InstituteID}/{MediumID}")
    Observable<String> MediumWiseClassDDL(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getClassWiseInsExame/{InstituteID}/{MediumID}/{ClassID}")
    Observable<String> getClassWiseInsExame(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID, @Path("ClassID") long ClassID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/ClassWiseDepartmentDDL/{InstituteID}/{ClassID}/{MediumID}")
    Observable<String> ClassWiseDepartmentDDL(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID, @Path("MediumID") long MediumID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsSection/{InstituteID}/{ClassID}/{DepartmentID}")
    Observable<String> getInsSection(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID, @Path("DepartmentID") long DepartmentID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getMonth")
    Observable<String> getMonth();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getStudentMonthlyDeviceAttendance/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{MonthID}/{UserID}/{InstituteID}")
    Observable<String> getStudentMonthlyDeviceAttendance(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                                         @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                                         @Path("DepartmentID") long DepartmentID, @Path("MonthID") int MonthID,
                                                         @Path("UserID") String UserID, @Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdDetail/{InstituteID}/{MediumID}/{ShiftID}/{ClassID}/{SectionID}/{DepartmentID}")
    Observable<String> getHrmSubWiseAtdDetail(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                              @Path("ShiftID") long ShiftID, @Path("ClassID") long ClassID,
                                              @Path("SectionID") long SectionID, @Path("DepartmentID") long DepartmentID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdByStudentID/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{UserID}/{Date}/{InstituteID}")
    Observable<String> getHrmSubWiseAtdByStudentID(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                                         @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                                         @Path("DepartmentID") long DepartmentID, @Path("UserID") String UserID,
                                                         @Path("Date") String Date, @Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdDetail/{InstituteID}/{MediumID}/{ShiftID}/{ClassID}/{SectionID}/{SubjectID}/{DepartmentID}/{date}")
    Observable<String> getHrmSubWiseAtdDetail(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                              @Path("ShiftID") long ShiftID, @Path("ClassID") long ClassID,
                                              @Path("SectionID") long SectionID, @Path("SubjectID") long SubjectID,
                                              @Path("DepartmentID") long DepartmentID, @Path("date") String date);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsSubject/{InstituteID}/{DepartmentID}/{MediumID}/{ClassID}")
    Observable<String> getInsSubject(@Path("InstituteID") long InstituteID, @Path("DepartmentID") long DepartmentID,
                                              @Path("MediumID") long MediumID, @Path("ClassID") long ClassID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getSubjectWiseMarks/0/{InstituteID}/{ClassID}/{SectionID}/{DepartmentID}/{MediumID}/{ShiftID}/{SubjectID}/{ExamID}")
    Observable<String> getSubjectWiseMarks(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID,
                                                         @Path("SectionID") long SectionID, @Path("DepartmentID") long DepartmentID,
                                                         @Path("MediumID") long MediumID, @Path("ShiftID") long ShiftID,
                                                         @Path("SubjectID") long SubjectID, @Path("ExamID") long ExamID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/setHrmSubWiseAtd")
    Observable<String> setHrmSubWiseAtd(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/setMarks")
    Observable<String> setMarks(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMCommunicationType")
    Observable<String> spGetCRMCommunicationType();

    @Multipart
    @POST("/api/onEms/Mobile/uploads/")
    Call<String> upload(@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/onEms/Mobile/crm/uploads/")
    Observable<String> uploadMultipleFilesDynamic(@Part List<MultipartBody.Part> files);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/spSetCRMNewClientEntry")
    Observable<String> postNewClient(@Body JsonObject newClient);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMClientList/{AgentID}")
    Observable<String> getClientList(@Path("AgentID") String AgentID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/spSetCRMCommunicationDetail")
    Observable<String> postCommunicationDetail(@Body CommunicationDetailModel communicationDetailModel);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMCommunicationDetailList/{NewClientID}")
    Observable<String> getDetailList(@Path("NewClientID") int NewClientID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInstituteType")
    Observable<String> getInstituteType();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMPriority")
    Observable<String> getCRMPriority();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getPaymentMethod")
    Observable<String> getPaymentMethod();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/spSetCRMWorkOrder")
    Observable<String> postWorkOrder(@Body WorkOrderModel workOrderModel);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMWrkOrdr/{NewClientID}")
    Observable<String> getWorkOrder(@Path("NewClientID") int NewClientID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getBranchByInsID/{InstituteID}")
    Observable<String> getBranchByInsID(@Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getCmnYear")
    Observable<String> getCmnYear();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getIncomeStatementReport/{FromDate}/{TODate}/{InstituteID}/{BranchID}/{FiscalYearID}")
    Observable<String> getIncomeStatementReport(@Path("FromDate") String FromDate, @Path("TODate") String TODate, @Path("InstituteID") long InstituteID, @Path("BranchID") long BranchID, @Path("FiscalYearID") String FiscalYearID);
}