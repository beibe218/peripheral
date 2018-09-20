package com.jimmy.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JService {

    @GET("yunpos/sync/item/GetItemList.form?")
    Call<ResponseBody> getItems(@Query("MerchantId") String merchantId,
                                @Query("BranchId") String branchId,
                                @Query("PageSize") int pageSize,
                                @Query("MaxFlowId") String maxFlowId,
                                @Query("HashCode") String hashCode
    );
}
