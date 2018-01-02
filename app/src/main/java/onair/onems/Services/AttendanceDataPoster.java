package onair.onems.Services;
        import java.util.List;
        import onair.onems.models.ApiResponseValue;
        import onair.onems.models.AttendanceSheetModel;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.POST;

/**
 * Created by User on 1/1/2018.
 */

public interface AttendanceDataPoster {
    @POST("setHrmSubWiseAtd")
    Call<List<ApiResponseValue>> createUser(@Body String string);
}
