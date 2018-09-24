package com.jimmy.data.remote.api;

import com.jimmy.data.db.model.Item;
import com.jimmy.data.db.model.ItemCls;
import com.jimmy.data.remote.model.HttpResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SyncApi {

    @GET("yunpos/sync/item/GetItemList.form")
    Observable<HttpResult<Item>> getItems(@Query("MerchantId") String merchantId,
                                          @Query("BranchId") String branchId,
                                          @Query("PageSize") int pageSize,
                                          @Query("MaxFlowId") String maxFlowId,
                                          @Query("HashCode") String hashCode);

    @GET("yunpos/sync/itemcls/GetItemClsList.form")
    Observable<HttpResult<ItemCls>> getItemClses(@Query("MerchantId") String merchantId,
                                                 @Query("BranchId") String branchId,
                                                 @Query("PageSize") int pageSize,
                                                 @Query("MaxFlowId") String maxFlowId,
                                                 @Query("HashCode") String hashCode);
}
