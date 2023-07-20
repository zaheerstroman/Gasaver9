package com.gasaver.network;

import com.gasaver.Response.BannersResponse;
import com.gasaver.Response.BaseResponse;
import com.gasaver.Response.BaseResponseGasaverTProperty;
import com.gasaver.Response.CatResponse;
import com.gasaver.Response.CopunsResponse;
import com.gasaver.Response.GetRewardDataResponse;
import com.gasaver.Response.GetUpdatePriceingResponse;
import com.gasaver.Response.GraphReportsResponse;
import com.gasaver.Response.LocationTableResponse;
import com.gasaver.Response.OtpResponseGasaverTProperty;
import com.gasaver.Response.ProfileUserDataResponseGasaverT;
import com.gasaver.Response.StationDataResponse;
import com.gasaver.Response.UploadDataResponse;
import com.gasaver.Response.UserRegResponseGasaverTProperty;
import com.gasaver.Response.WishlistResponse;
import com.gasaver.model.GraphCitiesResponse;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {


    String BASE_URL = "https://houseofspiritshyd.in/gasaver/admin/api/";

//    ----------------------- SIGNING LOGIN ---------------------------------------------


    //    UserSignInActivity:--------------------
//    @POST("login_controller/index")
    @POST("sendOtp")
    Call<UserRegResponseGasaverTProperty> userLogin(@Body RequestBody postObj);

    //    OtpActivity:---------------------------
    @POST("verifyOtp")
    Call<OtpResponseGasaverTProperty> verifyOtp(@Body RequestBody postObj);

    //   for otp verification
//    @POST("login_controller/index")
    @POST("sendOtp")
    Call<BaseResponseGasaverTProperty> resendOtp(@Body RequestBody postObj);

    //----------------------------------------------------------------------------------------------------


    //    ----------------------- PROFILE & EDIT PROFILE ---------------------------------------------


    //    PROFILE:-------------------------- PROFILE FRAGMENT & EDIT PROFILE FRAGMENT(Use)
    @POST("getUserData")
    Call<ProfileUserDataResponseGasaverT> fetchProfileDetails(@Body JsonObject postObj);

    //    Profile:-------------------------- PROFILE MAIN ACTIVITY & USER PROFILE ACTIVITY(Not Use)
    @POST("getUserData")
    Call<ProfileUserDataResponseGasaverT> fetchProfileDetails(@Body RequestBody postObj);


    //    EDIT PROFILE:--------------------------- EDIT PROFILE FRAGMENT() ------
    @POST("userDetailsUpdate")
    Call<BaseResponseGasaverTProperty> userDetailsUpdate(@Body RequestBody postObj);

    // EditProfileFragment + fetchCities + fetchStates + fetchCountry
    @POST("getDefaultData")
    Call<LocationTableResponse> getDefaultDataLoc(@Body JsonObject postObj);
    //----------------------------------------------------------------------------------------------------


    //    FEEDBACK ACTIVITY:-
    @POST("userDetailsUpdate")
    Call<BaseResponseGasaverTProperty> updateProfile(@Body JsonObject postObj);

    @POST("getUserData")
    Call<BaseResponseGasaverTProperty> updateProfile(@Body MultipartBody postObj);

    //----------------------------------------------------------------------------------------------------


//    HOME FRAGMENT GASAVER + MAIN PICKMAN ACTIVITY
//    HomeFragmentGasaver + MainPickmanActivity:-----------------------------

    //    Category/SubCategory(Spinner(FUEL+E10))
    @POST("getDefaultData")
    Call<CatResponse> getDefaultData(@Body JsonObject postObj);



    @POST("getDefaultData")
    Call<GraphCitiesResponse> getGraphData(@Body JsonObject postObj);


    //    HomeFragmentGasaver:-------------------------
    @POST("getStationsData_v1")
    Call<StationDataResponse> getStationsData(@Body JsonObject jsonObject);


    //    MainPickmanActivity:------------------------
    @POST("updatepriceing")
    Call<BaseResponse> updatepriceing(@Body JsonObject postObj);

    @POST("getupdatepriceing")
    Call<GetUpdatePriceingResponse> getupdatepriceing(@Body JsonObject postObj);

    //    UploadActivity:---------
    @POST("getReportsData")
    Call<UploadDataResponse> getReportsData(@Body JsonObject jsonObject);

    //--------------------------------------------------------------------------------------

    //    HomeFragmentGasaver:----WISHLIST(LOVE&LIKE)
    @POST("saveWishlist")
    Call<StationDataResponse> saveWishlist(@Body JsonObject jsonObject);

    //    WishListActivity:------ WISHLIST(LOVE YES) Recyclerview, (LOVE NO) Remove from Recyclerview
    @POST("getWishlist")
    Call<StationDataResponse> getWishlist(@Body JsonObject jsonObject);

    //    removeWishList
    @POST("getWishlist")
    Call<StationDataResponse> removeWishList(@Body RequestBody jsonObject);


    //-------------------------------------


    //Admin Accept i.e., Rewards will come
    @POST("getRewards")
    Call<GetRewardDataResponse> getRewardsData(@Body JsonObject jsonObject);

    //-----------------------------

    //Settings + HomeFragmentGasaver (saveUserDatSettings, setSetting, pushNotifacation)
    @POST("userSettings")
    Call<BaseResponse> settUserSettings(@Body JsonObject jsonObject);

    //allow_email,allow_sms,allow_push
    @POST("getUserData")
    Call<ProfileUserDataResponseGasaverT> setUserDatSettings(@Body JsonObject postObj);

    //--------------------------------------------------

    @POST("feedback")
    Call<BaseResponse> feedback(@Body JsonObject jsonObject);

    //    adds
    @POST("adds")
    Call<BannersResponse> fetchBanners(@Body JsonObject postObj);

    //--------------------------

    @POST("adds")
    Call<BannersResponse> fetchBanners1(@Body RequestBody postObj);

    //------------------------------------------------------------------------------------

    //    GraphReportsResponse:--
    @POST("getGraphReports")
    Call<GraphReportsResponse> fetchGraphReports(@Body JsonObject postObj);

//----------------------------------------------------------------------------------------------------------

    @POST("copuns")
    Call<CopunsResponse> getCopunsData(@Body JsonObject jsonObject);

    //--


}
