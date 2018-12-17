package onair.onems.Services;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    @Multipart
    @POST("/api/onEms/Mobile/uploads/")
    Call<String> upload(@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/onEms/Mobile/crm/uploads/")
    Observable<String> uploadMultipleFilesDynamic(@Part List<MultipartBody.Part> files);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/spSetCRMNewClientEntry")
    Call<String> postNewClient(@Body JsonObject newClient);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMClientList/{AgentID}")
    Call<String> getClientList(@Path("AgentID") String AgentID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @POST("/api/onEms/spSetCRMCommunicationDetail")
    Call<String> postCommunicationDetail(@Body CommunicationDetailModel communicationDetailModel);

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