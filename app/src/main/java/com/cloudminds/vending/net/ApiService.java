package com.cloudminds.vending.net;

import com.cloudminds.vending.vo.BaseResult;
import com.cloudminds.vending.vo.MetaInfo;
import com.cloudminds.vending.vo.NormalInfo;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("closedoor")
    Call<BaseResult> closeDoor(@Body NormalInfo info);

    @POST("identifyfail")
    Call<BaseResult> identifyFail(@Body MetaInfo info);

    @Multipart
    @POST("uploadmonitor")
    Call<BaseResult> uploadMonitor(@Part MultipartBody.Part meta, @Part MultipartBody.Part file);
}
