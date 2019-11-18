package com.utravel.app.utils;

import android.content.Context;
import android.util.Log;
import com.utravel.app.config.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import java.io.File;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetConnectionNew {
    //get接口
    public static void get(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        GetBuilder params = OkHttpUtils
                .get()
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                params.addParams(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    //post接口
    public static void post(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        PostFormBuilder params = OkHttpUtils
                .post()
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                params.addParams(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    //put接口
    public static void put(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("wanteyunshang", "wanteyunshang");
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        RequestBody requestBody = builder.build();
        OtherRequestBuilder params = OkHttpUtils
                .put()
                .requestBody(requestBody)
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    //patch接口
    public static void patch(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("IBuy", "IBuy");
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        RequestBody requestBody = builder.build();
        OtherRequestBuilder params = OkHttpUtils
                .patch()
                .requestBody(requestBody)
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    //delete接口
    public static void delete(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("IBuy", "IBuy");
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        RequestBody requestBody = builder.build();
        OtherRequestBuilder params = OkHttpUtils
                .delete()
                .requestBody(requestBody)
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    //post图片上传接口
    public static void postUpLoad(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final List<String> filePaths,
            final String interfaceName,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        PostFormBuilder params = OkHttpUtils
                .post()
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求头");
            }
        }
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                params.addParams(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        if (filePaths != null && filePaths.size() > 0) {
            for (int i = 0; i < filePaths.size(); i++) {
                File file = new File(filePaths.get(i));
                String fileName = file.getName();
                params.addFile(interfaceName, fileName, file);
                if (Config.BASE.equals(Config.DEBUG_BASE)) {
                    Log.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
//                    LatteLogger.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
                } else if (Config.BASE.equals(Config.RELEASE_BASE)) {
                    Log.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
//                    LatteLogger.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
                }
            }
        }
        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    public static void downLoad(
            final String apiName,
            final String url,
            final String fileDir,
            final String fileNameWithHouZhui,
            final ResponseCallback responseCallback,
            final FailCallback failCallback,
            final InProgressCallback inProgressCallback){
        OkHttpUtils
            .get()
            .url(url)
            .build()
            .execute(new FileCallBack(fileDir, fileNameWithHouZhui) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(apiName + "下载失败", apiName + "下载失败");
                    if (failCallback != null) {
                        failCallback.onFail(call, e, id);
                    }
                }
                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    Log.e("下载url=" + url, "下载url=" + url);
                    Log.e("下载目录=" + fileDir, "下载目录=" + fileDir);
                    Log.e("下载文件名称=" + fileNameWithHouZhui, "下载文件名称=" + fileNameWithHouZhui);
                    if (inProgressCallback != null) {
                        inProgressCallback.onInProgress(progress, total, id);
                    }
                }
                @Override
                public void onResponse(File downloadFile, int id) {
                    Log.e(apiName + "下载成功",  "--------------------------");
                    if (responseCallback != null) {
                        responseCallback.onResponse(downloadFile, id);
                    }
                }
            });
    }

    //put图片上传接口
    public static void putUpLoad(
            final String apiName,
            final Context context,
            final String url,
            final Map<String, String> addHeader,
            final Map<String, String> addParams,
            final List<String> filePaths,
            final String interfaceName,
            final SuccessCallback successCallback,
            final FailCallback failCallback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("IBuy", "IBuy");
        if (addParams != null) {
            for (Map.Entry<String, String> entry : addParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
                showParamsLog(apiName,entry,"请求体");
            }
        }
        if (filePaths != null && filePaths.size() > 0) {
            for (int i = 0; i < filePaths.size(); i++) {
                File file = new File(filePaths.get(i));
                String fileName = file.getName();
                builder.addFormDataPart(interfaceName, fileName, RequestBody.create(MediaType.parse("image/*"), file));
                if (Config.BASE.equals(Config.DEBUG_BASE)) {
                    Log.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
//                    LatteLogger.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
                } else if (Config.BASE.equals(Config.RELEASE_BASE)) {
                    Log.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
//                    LatteLogger.e(apiName + "上传", "interfaceName=" + interfaceName + "," + "上传文件名=" + fileName + "," + "上传文件路径=" + filePaths.get(i));
                }
            }
        }
        RequestBody requestBody = builder.build();
        OtherRequestBuilder params = OkHttpUtils
                .put()
                .requestBody(requestBody)
                .url(url);
        if (addHeader != null) {
            for (Map.Entry<String, String> entry : addHeader.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
                showParamsLog(apiName, entry,"请求头");
            }
        }

        params.build().execute(new StringCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showOnBeforeLog(apiName,request);
            }

            @Override
            public void onResponse(String arg0, int arg1) {
                if (successCallback != null) {
                    successCallback.onSuccess(arg0, arg1);
                }
                showOnResponse(context,apiName,arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                if (failCallback != null) {
                    failCallback.onFail(arg0, arg1, arg2);
                }
                showOnErrorLog(apiName,arg1);
            }
        });
    }

    public static void showParamsLog(String apiName, Map.Entry<String, String> entry, String type) {
        if (Config.BASE.equals(Config.DEBUG_BASE)) {
            Log.e(apiName + type, entry.getKey() + "=" + entry.getValue());
//            LatteLogger.e(apiName + type, entry.getKey() + "=" + entry.getValue());
        }else if (Config.BASE.equals(Config.RELEASE_BASE)) {
            Log.e(apiName + type, entry.getKey() + "=" + entry.getValue());
//            LatteLogger.e(apiName + type, entry.getKey() + "=" + entry.getValue());
        }
    }

    public static void showOnBeforeLog(String apiName, Request request){
        if (Config.BASE.equals(Config.DEBUG_BASE)) {
            Log.e(apiName + "url=", request.url().toString());
//            LatteLogger.e(apiName + "url=", request.url().toString());
        } else if (Config.BASE.equals(Config.RELEASE_BASE)) {
            Log.e(apiName + "url=", request.url().toString());
//            LatteLogger.e(apiName + "url=", request.url().toString());
        }
    }

    public static void showOnResponse(Context context, String apiName, String arg0) {
        if (Config.BASE.equals(Config.DEBUG_BASE)) {
            Log.e(apiName + "成功", arg0 + "");
            Log.e("Token=", SharedPreferencesUtil.getString(context, "Token"));
//            LatteLogger.json(apiName + "成功get", arg0 + "");
//            LatteLogger.e(apiName + "成功get", arg0 + "");
//            LatteLogger.e("Token=", SharedPreferencesUtil.getString(context, "Token"));
        } else if (Config.BASE.equals(Config.RELEASE_BASE)) {
            Log.e(apiName + "get", arg0 + "");
            Log.e("Token=", SharedPreferencesUtil.getString(context, "Token"));
//            LatteLogger.json(apiName + "成功", arg0 + "");
//            LatteLogger.e(apiName + "成功", arg0 + "");
//            LatteLogger.e("Token=", SharedPreferencesUtil.getString(context, "Token"));
        }
    }

    public static void showOnErrorLog(String apiName, Exception e) {
        if (e.getMessage() != null) {
            if (Config.BASE.equals(Config.DEBUG_BASE)) {
                Log.e(apiName + "onError", e.getMessage() + "");
//                LatteLogger.e(apiName + "onError", e.getMessage() + "");
            } else if (Config.BASE.equals(Config.RELEASE_BASE)) {
                Log.e(apiName + "onError", e.getMessage() + "");
//                LatteLogger.e(apiName + "onError", e.getMessage() + "");
            }
        } else {
            Log.e(apiName + "onError", "arg1.getMessage()==null");
//            LatteLogger.e(apiName + "onError", "arg1.getMessage()==null");
        }
    }

    public static interface SuccessCallback {
        void onSuccess(String result, int arg1);
    }

    public static interface ResponseCallback {
        void onResponse(File downloadFile, int id);
    }

    public static interface FailCallback {
        void onFail(Call arg0, Exception arg1, int arg2);
    }

    public static interface InProgressCallback {
        void onInProgress(float progress, long total, int id);
    }
}
