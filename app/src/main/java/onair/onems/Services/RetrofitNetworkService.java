package onair.onems.Services;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import onair.onems.models.CommunicationDetailModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitNetworkService {
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
    Call<String> getDetailList(@Path("NewClientID") int NewClientID);

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/getInstituteType")
    Observable<String> getInstituteType();

    @Headers("Authorization: Request_From_onEMS_Android_app")
    @GET("/api/onEms/spGetCRMPriority")
    Observable<String> getCRMPriority();
}