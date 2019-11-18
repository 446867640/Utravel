package com.utravel.app.wechat;

import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import com.utravel.app.R;
import com.utravel.app.activities.proxy.AccountLogActivity;
import com.utravel.app.activities.proxy.MyOrderActivity;
import com.utravel.app.config.Config;
import com.utravel.app.utils.LatteLogger;
import com.utravel.app.utils.NetConnectionNew;
import com.utravel.app.utils.SharedPreferencesUtil;
import com.utravel.app.wxapi.WXPayEntryActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public abstract class BaseWXPayEntryActivity extends BaseWXActivity {
    //微信发送请求到第三方应用后的回调
    @Override
    public void onReq(BaseReq baseReq) {}
    //第三方应用发送请求到微信后的回调
    @Override
    public void onResp(BaseResp baseResp) {
        LatteLogger.e("微信支付", "onPayFinish, errCode = " + baseResp.errCode);
        switch (baseResp.errCode){
            case 0:
                Toast.makeText(this, getResources().getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                //成功
                if (SharedPreferencesUtil.getString(this, "object_type").equals("order")) {
                    Intent intent = new Intent(this, MyOrderActivity.class);
                    intent.putExtra("code", "3");
                    startActivity(intent);
                }else if (SharedPreferencesUtil.getString(this, "object_type").equals("points_order")) {
                    Intent intent = new Intent(this, MyOrderActivity.class);
                    intent.putExtra("code", "3");
                    startActivity(intent);
                }else if (SharedPreferencesUtil.getString(this, "object_type").equals("national_porcelain_order")) {
                    Intent intent = new Intent(this, MyOrderActivity.class);
                    intent.putExtra("code", "3");
                    startActivity(intent);
                }else if (SharedPreferencesUtil.getString(this, "object_type").equals("exchanged_order")) {
                    Intent intent = new Intent(this, MyOrderActivity.class);
                    intent.putExtra("code", "3");
                    startActivity(intent);
                }else if (SharedPreferencesUtil.getString(this, "object_type").equals("points_as_gift")) {
                    goToDetail("balance");
                }else if (SharedPreferencesUtil.getString(this, "object_type").equals("balance_recharge")) {
                    goToDetail("balance");
                }
                SharedPreferencesUtil.putString(this, "object_type","");
                SharedPreferencesUtil.putString(this, "payment_method_code","");
                finish();
                break;
            case -1:
                //失败
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case -2:
                //用户取消
                Toast.makeText(this, "支付取消", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void goToDetail(String detailType) {
        Intent intent = new Intent(this, AccountLogActivity.class);
        intent.putExtra("type", detailType);
        startActivity(intent);
    }
}
