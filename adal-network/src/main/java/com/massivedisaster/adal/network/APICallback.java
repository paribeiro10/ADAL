/*
 * ADAL - A set of Android libraries to help speed up Android development.
 * Copyright (C) 2017 ADAL.
 *
 * ADAL is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or any later version.
 *
 * ADAL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with ADAL. If not, see <http://www.gnu.org/licenses/>.
 */

package com.massivedisaster.adal.network;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public abstract class APICallback<T extends APIErrorListener> implements Callback<T> {

    private Context mContext;

    public APICallback(Context context) {
        mContext = context;
    }

    /**
     * @param t response
     */
    public abstract void onSuccess(T t);

    /**
     * @param error
     * @param isServerError
     */
    public abstract void onError(APIError error, boolean isServerError);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (call.isCanceled()) {
            return;
        }

        if (response == null) {
            processError(new APIError(mContext.getString(R.string.error_network_general)), true);
            return;
        }

        if (response.errorBody() != null) {
            try {
                Converter<ResponseBody, T> errorConverter = getRetrofitConverter(call);

                T error = errorConverter.convert(response.errorBody());

                if (error != null) {
                    processError(new APIError(error.getErrorCode(), error.getError()), true);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            processError(new APIError(mContext.getString(R.string.error_network_general)), true);
            return;
        }

        if (response.body() == null && !response.isSuccessful()) {
            processError(new APIError(mContext.getString(R.string.error_network_general)), true);
            return;
        }

        onSuccess(response.body());
    }

    @SuppressWarnings("unchecked")
    private Converter<ResponseBody, T> getRetrofitConverter(Call<T> call) throws Exception {
        Field f = call.getClass().getDeclaredField("delegate");
        f.setAccessible(true);
        Object obj = f.get(call);

        f = obj.getClass().getDeclaredField("serviceMethod");
        f.setAccessible(true);
        obj = f.get(obj);

        f = obj.getClass().getDeclaredField("responseConverter");
        f.setAccessible(true);

        return (Converter<ResponseBody, T>) f.get(obj);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

        if (call.isCanceled()) {
            return;
        }

        if (t != null) {
            t.printStackTrace();

            if ((t instanceof UnknownHostException) && t.getMessage() != null) {
                processError(new APIError(mContext.getString(R.string.error_network_no_connection)), true);
                return;
            }
        }

        processError(new APIError(mContext.getString(R.string.error_network_general)), true);
    }

    private void processError(APIError error, boolean serverError) {

        if (BuildConfig.DEBUG) {
            Log.e(APICallback.class.getCanonicalName(), error.getMessage());
        }

        onError(error, serverError);
    }
}
