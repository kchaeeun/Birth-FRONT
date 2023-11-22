package kr.ac.duksung.birth.Retrofit;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NumApiService {

    @GET("/number/{serial}")
    Call<Serial> getBySerial(@Path("serial") String serial);
}
