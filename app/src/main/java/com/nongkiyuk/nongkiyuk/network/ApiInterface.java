package com.nongkiyuk.nongkiyuk.network;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> registerRequest(@Field("name") String name,
                                       @Field("email") String email,
                                       @Field("username") String username,
                                       @Field("password") String password);

    @GET("user")
    Call<ResponseBody> profileRequest(@Header("Authorization") String token);

    @FormUrlEncoded
    @PATCH("user")
    Call<ResponseBody> saveProfileRequest(@Header("Authorization") String token,
                                          @Field("name") String name,
                                          @Field("email") String email,
                                          @Field("username") String username,
                                          @Field("password") String password);

    @Multipart
    @POST("user/picture")
    Call<ResponseBody> uploadPictureProfileRequest(@Header("Authorization") String token,
                                                   @Part MultipartBody.Part picture);

    @GET("places/favorite")
    Call<ResponseBody> getFavoritePlaces(@Header("Authorization") String token);

    @GET("places")
    Call<ResponseBody> getPlaces();

    @POST("place/{id}/favorite")
    Call<ResponseBody> sendFavotire(@Header("Authorization") String token, @Path("id") String id);
}
