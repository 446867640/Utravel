package com.utravel.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.utravel.app.config.Config;
import com.utravel.app.wechat.LatteWeChat;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class WXShareUtil {
    //这个APP_ID是我个人的，可以供大家使用
    public static final String ACTION_SHARE_RESPONSE = "action_wx_share_response";
    public static final String EXTRA_RESULT = "result";

    private final Context context;
    private final IWXAPI api;
    private OnResponseListener listener;
    private ResponseReceiver receiver;

    public WXShareUtil(Context context) {
        api = LatteWeChat.getInstance().getWXAPI();
        this.context = context;
    }

    public WXShareUtil register() {
        // 微信分享
        api.registerApp(Config.WE_CHAT_APP_ID);
        receiver = new ResponseReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SHARE_RESPONSE);
        context.registerReceiver(receiver, filter);
        return this;
    }

    public void unregister() {
        try {
            api.unregisterApp();
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IWXAPI getApi() {
        return LatteWeChat.getInstance().getWXAPI();
    }

    public void setListener(OnResponseListener listener) {
        this.listener = listener;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Response response = intent.getParcelableExtra(EXTRA_RESULT);
            LatteLogger.e("type: " + response.getType(),"type: " + response.getType());
            LatteLogger.e("errCode: " + response.errCode,"errCode: " + response.errCode);
            String result;
            if (listener != null) {
                if (response.errCode == BaseResp.ErrCode.ERR_OK) {
                    listener.onSuccess();
                } else if (response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                    listener.onCancel();
                } else {
                    switch (response.errCode) {
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            result = "发送被拒绝";
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            result = "不支持错误";
                            break;
                        default:
                            result = "发送返回";
                            break;
                    }
                    listener.onFail(result);
                }
            }
        }
    }

    public static class Response extends BaseResp implements Parcelable {

        public int errCode;
        public String errStr;
        public String transaction;
        public String openId;

        private int type;
        private boolean checkResult;

        public Response(BaseResp baseResp) {
            errCode = baseResp.errCode;
            errStr = baseResp.errStr;
            transaction = baseResp.transaction;
            openId = baseResp.openId;
            type = baseResp.getType();
            checkResult = baseResp.checkArgs();
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public boolean checkArgs() {
            return checkResult;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.errCode);
            dest.writeString(this.errStr);
            dest.writeString(this.transaction);
            dest.writeString(this.openId);
            dest.writeInt(this.type);
            dest.writeByte(this.checkResult ? (byte) 1 : (byte) 0);
        }

        protected Response(Parcel in) {
            this.errCode = in.readInt();
            this.errStr = in.readString();
            this.transaction = in.readString();
            this.openId = in.readString();
            this.type = in.readInt();
            this.checkResult = in.readByte() != 0;
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel source) {
                return new Response(source);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
    }

    public WXShareUtil share(String text) {
        com.tencent.mm.opensdk.modelmsg.WXTextObject textObj = new com.tencent.mm.opensdk.modelmsg.WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        //        msg.title = "Will be ignored";
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        boolean result = api.sendReq(req);
        LatteLogger.e("text shared: " + result,"");
        return this;
    }

    //flag用来判断是分享到微信好友还是分享到微信朋友圈，
    //0代表分享到微信好友，1代表分享到朋友圈
    public WXShareUtil shareUrl(
            Context context,//上下文
            int flag,//微信好友还是朋友圈
            int icon_imageResource,//小图片
            String url,//分享的链接
            String title,//分享的标题
            String descroption){//分享的描述内容
        //初始化一个WXWebpageObject填写url
        com.tencent.mm.opensdk.modelmsg.WXWebpageObject webpageObject = new com.tencent.mm.opensdk.modelmsg.WXWebpageObject();
        webpageObject.webpageUrl = url;
        //用WXWebpageObject对象初始化一个WXMediaMessage，天下标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = descroption;
        //这块需要注意，图片的像素千万不要太大，不然的话会调不起来微信分享，
        //我在做的时候和我们这的UIMM说随便给我一张图，她给了我一张1024*1024的图片
        //当时也不知道什么原因，后来在我的机智之下换了一张像素小一点的图片好了！
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), icon_imageResource);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        return this;
    }

    public WXShareUtil shareUrl1(
            Context context,//上下文
            int flag,//微信好友还是朋友圈
            Bitmap bitmap,//小图片
            String url,//分享的链接
            String title,//分享的标题
            String descroption){//分享的描述内容
        //初始化一个WXWebpageObject填写url
        com.tencent.mm.opensdk.modelmsg.WXWebpageObject webpageObject = new com.tencent.mm.opensdk.modelmsg.WXWebpageObject();
        webpageObject.webpageUrl = url;
        //用WXWebpageObject对象初始化一个WXMediaMessage，天下标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = descroption;
        //这块需要注意，图片的像素千万不要太大，不然的话会调不起来微信分享，
        //我在做的时候和我们这的UIMM说随便给我一张图，她给了我一张1024*1024的图片
        //当时也不知道什么原因，后来在我的机智之下换了一张像素小一点的图片好了！
        //设置缩略图
//		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
//		bitmap.recycle();
        msg.thumbData = bmpToByteArray(bitmap, true);  //设置缩略图
        msg.setThumbImage(bitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        return this;
    }

    public void sharePicWithPath(int shareType,String path) {
        if (fileIsExists(path)) {
            //初始化WXImageObject和WXMediaMessage对象
            WXImageObject imageObject = new WXImageObject();
            imageObject.setImagePath(path);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imageObject;
            //构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("imgshareappdata");
            req.message = msg;
            //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
            req.scene = shareType==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
            //调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    public void sharePicWithBitmap(int shareType, Bitmap bitmap) {
        if (bitmap!=null) {
            //初始化WXImageObject和WXMediaMessage对象
            WXImageObject imageObject = new WXImageObject(bitmap);;
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imageObject;
            //设置缩略图
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 200, true);
            bitmap.recycle();
            msg.thumbData = bmpToByteArray(scaledBitmap, true);  //设置缩略图
            //构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("imgshareappdata");
            req.message = msg;
            //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
            req.scene = shareType==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
            //调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    public void sharePicWithBitmapAndPath(int shareType, Bitmap bitmap, String path) {
        //初始化WXImageObject和WXMediaMessage对象
        WXImageObject imageObject;
        WXMediaMessage msg = new WXMediaMessage();
        if (fileIsExists(path)) {
            imageObject = new WXImageObject();
            imageObject.setImagePath(path);
        } else {
            imageObject = new WXImageObject(bitmap);
            //设置缩略图
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 200, true);
            bitmap.recycle();
            msg.thumbData = bmpToByteArray(scaledBitmap, true);  //设置缩略图
        }
        msg.mediaObject = imageObject;
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imgshareappdata");
        req.message = msg;
        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        req.scene = shareType==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        //调用api接口发送数据到微信
        api.sendReq(req);
    }

    public void sharePicture(int shareType) {//shareType：0代表分享到微信好友，1代表分享到朋友圈
        FileInputStream fis;
        try {
            fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/share.png");
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            WXImageObject imgObj = new WXImageObject(bitmap);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
//	        bitmap.recycle();
            msg.thumbData = bmpToByteArray(bitmap, true);  //设置缩略图
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("imgshareappdata");
            req.message = msg;
            req.scene = shareType==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public static boolean fileIsExists(String strFile){
        try{
            File f=new File(strFile);
            if(!f.exists()){
                return false;
            }
        }
        catch (Exception e){ return false; }
        return true;
    }

    // 删除单个文件
    public static boolean deleteSingleFile(Context context, String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LatteLogger.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                LatteLogger.e("删除单个文件" + filePath$Name + "失败！", "...");
                return false;
            }
        } else {
            LatteLogger.e("删除单个文件失败：" + filePath$Name + "不存在！", "...");
            return false;
        }
    }

    public interface OnResponseListener {
        //分享成功的回调
        void onSuccess();
        //分享取消的回调
        void onCancel();
        //分享失败的回调
        void onFail(String message);
    }
}
