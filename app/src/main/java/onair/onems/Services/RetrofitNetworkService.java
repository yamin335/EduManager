package onair.onems.Services;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import onair.onems.models.CommunicationDetailModel;
import onair.onems.models.WorkOrderModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Observable<String> getClassWiseInsExame(@Path("InstituteID") long InstituteID,
                                            @Path("MediumID") long MediumID, @Path("ClassID") long ClassID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/ClassWiseDepartmentDDL/{InstituteID}/{ClassID}/{MediumID}")
    Observable<String> ClassWiseDepartmentDDL(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID, @Path("MediumID") long MediumID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsDepertment/{InstituteID}")
    Observable<String> getInsDepertment(@Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsSection/{InstituteID}/{ClassID}/{DepartmentID}")
    Observable<String> getInsSection(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID, @Path("DepartmentID") long DepartmentID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getMonth")
    Observable<String> getMonth();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getStudentMonthlyDeviceAttendance/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{MonthID}/{UserID}/{InstituteID}/{SessionID}")
    Observable<String> getStudentMonthlyDeviceAttendance(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                                         @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                                         @Path("DepartmentID") long DepartmentID, @Path("MonthID") int MonthID,
                                                         @Path("UserID") String UserID, @Path("InstituteID") long InstituteID,
                                                         @Path("SessionID") long SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/gethrmDeviceByParmsforStudent/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{date}/{IsPresent}/{IsAbsent}/{InstituteID}/{UserTypeID}/{SessionID}")
    Observable<String> gethrmDeviceByParmsforStudent(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                              @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                              @Path("DepartmentID") long DepartmentID, @Path("date") String date,
                                               @Path("IsPresent") int IsPresent, @Path("IsAbsent") int IsAbsent,
                                              @Path("InstituteID") long InstituteID, @Path("UserTypeID") long UserTypeID,  @Path("SessionID") long SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmTotalStudentAttendance/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{date}/{InstituteID}/{UserTypeID}")
    Observable<String> getHrmTotalStudentAttendance(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                                    @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                                    @Path("DepartmentID") long DepartmentID, @Path("date") String date,
                                                    @Path("InstituteID") long InstituteID, @Path("UserTypeID") long UserTypeID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdDetail/{InstituteID}/{MediumID}/{ShiftID}/{ClassID}/{SectionID}/{DepartmentID}/{SessionID}")
    Observable<String> getHrmSubWiseAtdDetail(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                              @Path("ShiftID") long ShiftID, @Path("ClassID") long ClassID,
                                              @Path("SectionID") long SectionID, @Path("DepartmentID") long DepartmentID,
                                              @Path("SessionID") long SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdByStudentID/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{UserID}/{Date}/{InstituteID}")
    Observable<String> getHrmSubWiseAtdByStudentID(@Path("ShiftID") long ShiftID, @Path("MediumID") long MediumID,
                                                         @Path("ClassID") long ClassID, @Path("SectionID") long SectionID,
                                                         @Path("DepartmentID") long DepartmentID, @Path("UserID") String UserID,
                                                         @Path("Date") String Date, @Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getHrmSubWiseAtdDetail/{InstituteID}/{MediumID}/{ShiftID}/{ClassID}/{SectionID}/{SubjectID}/{DepartmentID}/{date}/{SessionID}")
    Observable<String> getHrmSubWiseAtdDetail(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                              @Path("ShiftID") long ShiftID, @Path("ClassID") long ClassID,
                                              @Path("SectionID") long SectionID, @Path("SubjectID") long SubjectID,
                                              @Path("DepartmentID") long DepartmentID, @Path("date") String date,
                                              @Path("SessionID") long SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getSubAttendence/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{ShiftID}/{SessionID}/{InstituteID}/{DayID}/{TeacherID}")
    Observable<String> getSubAttendence(@Path("MediumID") long MediumID, @Path("ClassID") long ClassID,
                                        @Path("DepartmentID") long DepartmentID, @Path("SectionID") long SectionID,
                                        @Path("ShiftID") long ShiftID, @Path("SessionID") long SessionID,
                                        @Path("InstituteID") long InstituteID, @Path("DayID") int DayID,
                                        @Path("TeacherID") String TeacherID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInsSubject/{InstituteID}/{DepartmentID}/{MediumID}/{ClassID}")
    Observable<String> getInsSubject(@Path("InstituteID") long InstituteID, @Path("DepartmentID") long DepartmentID,
                                              @Path("MediumID") long MediumID, @Path("ClassID") long ClassID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getSubjectWiseMarks/{UserID}/{InstituteID}/{ClassID}/{SectionID}/{DepartmentID}/{MediumID}/{ShiftID}/{SubjectID}/{ExamID}/{SessionID}")
    Observable<String> getSubjectWiseMarks(@Path("UserID") String UserID, @Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID,
                                                         @Path("SectionID") long SectionID, @Path("DepartmentID") long DepartmentID,
                                                         @Path("MediumID") long MediumID, @Path("ShiftID") long ShiftID,
                                                         @Path("SubjectID") long SubjectID, @Path("ExamID") long ExamID, @Path("SessionID") long SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/GetAccFeesCollectionReport/{InstituteID}/{BranchID}/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{ShiftID}/{fromDate}" +
            "/{toDate}/{MonthID}/{StatusID}/{SessionID}")
    Observable<String> GetAccFeesCollectionReport(@Path("InstituteID") String InstituteID, @Path("BranchID") String BranchID,
                                                  @Path("MediumID") String MediumID, @Path("ClassID") String ClassID,
                                                  @Path("DepartmentID") String DepartmentID, @Path("SectionID") String SectionID,
                                                  @Path("ShiftID") String ShiftID, @Path("fromDate") String fromDate,
                                                  @Path("toDate") String toDate, @Path("MonthID") String MonthID,
                                                  @Path("StatusID") String StatusID, @Path("SessionID") String SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/GetAccFeesCollectionReportTopSheet/{InstituteID}/{BranchID}/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{ShiftID}/{fromDate}" +
            "/{toDate}/{MonthID}/{StatusID}/{SessionID}")
    Observable<String> GetAccFeesCollectionReportTopSheet(@Path("InstituteID") String InstituteID, @Path("BranchID") String BranchID,
                                                  @Path("MediumID") String MediumID, @Path("ClassID") String ClassID,
                                                  @Path("DepartmentID") String DepartmentID, @Path("SectionID") String SectionID,
                                                  @Path("ShiftID") String ShiftID, @Path("fromDate") String fromDate,
                                                  @Path("toDate") String toDate, @Path("MonthID") String MonthID,
                                                  @Path("StatusID") String StatusID, @Path("SessionID") String SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getMyInsHomeWork/{InstituteID}/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{ShiftID}/{date}")
    Observable<String> getMyInsHomeWork(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                        @Path("ClassID") long ClassID, @Path("DepartmentID") long DepartmentID,
                                        @Path("SectionID") long SectionID, @Path("ShiftID") long ShiftID,
                                        @Path("date") String date);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getStudents/{InstituteID}/{ClassID}/{SectionID}/{DepartmentID}/{MediumID}/{ShiftID}/{UserID}/{SessionID}")
    Observable<String> getStudent(@Path("InstituteID") long InstituteID, @Path("ClassID") String ClassID,
                                  @Path("SectionID") String SectionID, @Path("DepartmentID") String DepartmentID,
                                  @Path("MediumID") String MediumID, @Path("ShiftID") String ShiftID,
                                  @Path("UserID") String UserID, @Path("SessionID") String SessionID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getDateWiseLessonPlan/{InstituteID}/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{date}")
    Observable<String> getDateWiseLessonPlan(@Path("InstituteID") long InstituteID, @Path("MediumID") long MediumID,
                                        @Path("ClassID") long ClassID, @Path("DepartmentID") long DepartmentID,
                                        @Path("SectionID") long SectionID, @Path("date") String date);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getAcademicClassDayForMySyllabus/{InstituteID}/{MediumID}/{ClassID}/{DepartmentID}/{SectionID}/{SubjectID}/{fromDate}/{toDate}/{ExamID}")
    Observable<String> getAcademicClassDayForMySyllabus(@Path("InstituteID") long InstituteID,
                                                        @Path("MediumID") long MediumID,
                                                        @Path("ClassID") long ClassID,
                                                        @Path("DepartmentID") long DepartmentID,
                                                        @Path("SectionID") long SectionID,
                                                        @Path("SubjectID") String SubjectID,
                                                        @Path("ExamID") String ExamID,
                                                        @Path("fromDate") String fromDate,
                                                        @Path("toDate") String toDate);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getUrlMasterByID/{SyllabusID}")
    Observable<String> getUrlMasterByID(@Path("SyllabusID") String SyllabusID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getDateWiseLessonPlanDetail/{SyllabusDetailID}")
    Observable<String> getDateWiseLessonPlanDetail(@Path("SyllabusDetailID") String SyllabusDetailID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getUrlTopicDetailByID/{SyllabusID}/{SyllabusDetailID}")
    Observable<String> getUrlTopicDetailByID(@Path("SyllabusID") String SyllabusID, @Path("SyllabusDetailID") String SyllabusDetailID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/resetPassword/{UserID}/{LoginID}/{Password}/{InstituteID}")
    Observable<String> resetPassword(@Path("UserID") String UserID, @Path("LoginID") String LoginID,
                                  @Path("Password") String Password, @Path("InstituteID") String InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getStudentInformation/{RFID}/{UserID}")
    Observable<String> getStudentInformation(@Path("RFID") String RFID, @Path("UserID") String UserID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getUserEmailPhoneIfExist/{emailPhoneOrLoginId}")
    Observable<String> getUserEmailPhoneIfExist(@Path("emailPhoneOrLoginId") String emailPhoneOrLoginId);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/sendCodeThrouMail/{EmailOrPhone}/{verificationCode}")
    Observable<String> sendCodeThrouMail(@Path("EmailOrPhone") String EmailOrPhone, @Path("verificationCode") int verificationCode);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInstituteAvailableSMS/{InstituteID}")
    Observable<String> getInstituteAvailableSMS(@Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api.php")
    Observable<String> sendVerificationCodeViaSMS(@Query("token") String Token, @Query("to") String PhoneNo,
                                                  @Query("message") String verificationCode);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @DELETE("/api/onEms/deleteFcmToken/{UserID}/{DeviceType}/{DeviceUUID}")
    Observable<String> deleteFcmToken(@Path("UserID") String UserID, @Path("DeviceType") String DeviceType,
                                     @Path("DeviceUUID") String DeviceUUID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getinsGradeForReport/{InstituteID}/{MediumID}/{ClassID}")
    Observable<String> getinsGradeForReport(@Path("InstituteID") long InstituteID, @Path("MediumID") String MediumID,
                                      @Path("ClassID") String ClassID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/SubjectWiseMarksByStudent/{UserID}/{InstituteID}/{ClassID}/{SectionID}/{DepartmentID}/{MediumID}/{ShiftID}/{SessionID}/{ExamID}")
    Observable<String> SubjectWiseMarksByStudent(@Path("UserID") String UserID, @Path("InstituteID") String InstituteID,
                                                          @Path("ClassID") String ClassID, @Path("SectionID") String SectionID,
                                                          @Path("DepartmentID") String DepartmentID, @Path("MediumID") String MediumID,
                                                          @Path("ShiftID") String ShiftID, @Path("SessionID") String SessionID,
                                                          @Path("ExamID") String ExamID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCommonClassExamRoutine/{InstituteID}/{ExamID}/{MediumID}/{ClassID}/{DepartmentID}/{SessionID}/{isViva}")
    Observable<String> spGetCommonClassExamRoutine(@Path("InstituteID") long InstituteID, @Path("ExamID") long ExamID,
                                                   @Path("MediumID") long MediumID, @Path("ClassID") long ClassID,
                                                   @Path("DepartmentID") long DepartmentID, @Path("SessionID") long SessionID,
                                                   @Path("isViva") boolean isViva);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetDashClassRoutine/{ShiftID}/{MediumID}/{ClassID}/{SectionID}/{DepartmentID}/{DayID}/{InstituteID}")
    Observable<String> spGetDashClassRoutine(@Path("ShiftID") String ShiftID, @Path("MediumID") String MediumID,
                                             @Path("ClassID") String ClassID, @Path("SectionID") String SectionID,
                                             @Path("DepartmentID") String DepartmentID, @Path("DayID") String DayID,
                                             @Path("InstituteID") String InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/gethrmDeviceByParmsforTeacher/{BranchID}/{DepartmentID}/{Date}/{InstituteID}/{UserTypeID}")
    Observable<String> gethrmDeviceByParmsforTeacher(@Path("BranchID") long BranchID, @Path("DepartmentID") long DepartmentID,
                                             @Path("Date") String Date, @Path("InstituteID") long InstituteID,
                                             @Path("UserTypeID") int UserTypeID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCommonClassRoutine/{InstituteID}/{ShiftID}")
    Observable<String> spGetCommonClassRoutine(@Path("InstituteID") long InstituteID, @Path("ShiftID") long ShiftID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetTeacherStudentMyClassRoutine/{InstituteID}/{ClassID}/{UserID}/{ShiftID}/{SectionID}/{MediumID}")
    Observable<String> spGetTeacherStudentMyClassRoutine(@Path("InstituteID") long InstituteID, @Path("ClassID") long ClassID,
                                                         @Path("UserID") String UserID, @Path("ShiftID") long ShiftID,
                                                         @Path("SectionID") long SectionID, @Path("MediumID") long MediumID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getMonthlyTeacherAttendance/{InstituteID}/{UserType}/{MonthID}/{FromDate}/{ToDate}")
    Observable<String> getMonthlyTeacherAttendance(@Path("InstituteID") long InstituteID, @Path("UserType") int UserType,
                                                         @Path("MonthID") long MonthID, @Path("FromDate") String FromDate,
                                                         @Path("ToDate") String ToDate);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/setStudentBasicInfo")
    Observable<String> setStudentBasicInfo(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getMyInsHomeWorkDetail/{HomeWorkID}")
    Observable<String> getMyInsHomeWorkDetail(@Path("HomeWorkID") String HomeWorkID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetDashNotice/{UserTypeID}/{InstituteID}")
    Observable<String> spGetDashNotice(@Path("UserTypeID") int UserTypeID, @Path("InstituteID") long InstituteID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getallsession")
    Observable<String> getallsession();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/setHrmSubWiseAtd")
    Observable<String> setHrmSubWiseAtd(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/indivisualSetMark")
    Observable<String> indivisualSetMark(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/getStudentList")
    Observable<String> getStudentList(@Body JsonObject jsonObject);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMCommunicationType")
    Observable<String> spGetCRMCommunicationType();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getallgender")
    Observable<String> getallgender();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getreligion")
    Observable<String> getreligion();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getbloodgroups")
    Observable<String> getbloodgroups();

    @Multipart
    @POST("/api/onEms/Mobile/uploads/")
    Call<String> upload(@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/onEms/Mobile/uploads/")
    Observable<String> uploadSingleImage(@Part MultipartBody.Part file);

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