package br.com.slmm.desenho2;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApiEndpointInterface {

    @POST("teste")
    Call<ResponseBody> getCmd(@Body Comando cmd);
    @POST("teste")
    Call<Comando> getCmd1(@Body Comando cmd);
    @GET("users/{username}")
    @Headers({"Cache-Control: max-age=640000", "User-Agent: My-App-Name"})
    Call<Comando> getUser(@Path("username") String username,@Body Comando cmd);

    @POST("users/new")
    Call<Comando> createUser(@Body Comando cmd);
}
