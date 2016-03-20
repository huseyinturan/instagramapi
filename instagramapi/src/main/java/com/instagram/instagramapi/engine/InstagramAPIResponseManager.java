package com.instagram.instagramapi.engine;

import com.google.gson.Gson;
import com.instagram.instagramapi.interfaces.InstagramAPIResponseCallback;
import com.instagram.instagramapi.objects.IGPagInfo;
import com.instagram.instagramapi.objects.InstagramAPIResponse;
import com.instagram.instagramapi.objects.InstagramException;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sayyam on 3/18/16.
 */
public class InstagramAPIResponseManager<T> implements Callback<InstagramAPIResponse> {

    private Type type;

    public Type getMyType() {
        return this.type;
    }

    InstagramAPIResponseCallback<T> instagramAPIResponseCallback;

    public InstagramAPIResponseManager(InstagramAPIResponseCallback instagramAPIResponseCallback, Type type) {
        this.instagramAPIResponseCallback = instagramAPIResponseCallback;
        this.type = type;
    }

    @Override
    public void onFailure(Call<InstagramAPIResponse> call, Throwable t) {
        instagramAPIResponseCallback.onFailure(new InstagramException(t.getMessage()));
    }

    @Override
    public void onResponse(Call<InstagramAPIResponse> call, Response<InstagramAPIResponse> response) {

        try {

            //String meta = new Gson().toJson(response.body().getMeta());
            //IGMeta meta = response.body().getMeta();

            if (/*igMeta.getCode().equals("200")*/ response.code() == 200) {

                String data = new Gson().toJson(response.body().getData());
                IGPagInfo pagination = response.body().getPagination();

                if (null != data && !data.isEmpty()) {

                    T responseData = new Gson().fromJson(data, getMyType());

                    instagramAPIResponseCallback.onResponse(responseData, pagination);
                } else {
                    instagramAPIResponseCallback.onFailure(new InstagramException("Invalid Response"));
                }
            } else {
                instagramAPIResponseCallback.onFailure(new InstagramException("Please check if you have valid access token for this request"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            instagramAPIResponseCallback.onFailure(new InstagramException("Invalid Response"));
        }

    }

}