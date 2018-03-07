package com.sunanang.sunwallpaper.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.sunanang.sunwallpaper.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import java.util.ArrayList;


/**
 * Created by ${Sunanang} on 18/1/17.
 * 分享qq微信等功能
 */

public class ShareToTencentUtils {
    private static final String appName="拍拍壁纸";
    private static final String downLoad="http://paipaiwall.bmob.site/";
    private static final String summary = "拍拍壁纸，最自由的手机壁纸";
    private static final String thumb = "http://bmob-cdn-15467.b0.upaiyun.com/2018/01/17/f89168bb40e2a08f8017e966ad7a292e.png";
    public static final String app_id = "wx73b21dcabc0a9653";

    /**
     * 分享QQ
     */
    public static void onClickStory(Activity context, Tencent mTencent) {


        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, appName);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  downLoad);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,thumb);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  appName);
        mTencent.shareToQQ(context, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
//                Log.i("Sunanang","onComplete" + o.toString());
            }

            @Override
            public void onError(UiError uiError) {
//                Log.i("Sunanang","onError" + uiError.toString());
            }

            @Override
            public void onCancel() {
//                Log.i("Sunanang","onCancel" );
            }
        });
    }

    /**
     * 分享空间
     */
    public static void shareToQzone (Activity activity,Tencent mTencent) {
        Bundle params = new Bundle();
        ArrayList<String> list = new ArrayList<>();
        list.add(thumb);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, appName);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, downLoad);//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,list);
        mTencent.shareToQzone(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public static void setWechatShare(int type, Context context, IWXAPI api){
        WXWebpageObject object = new WXWebpageObject();
        object.webpageUrl = downLoad;
        WXMediaMessage message = new WXMediaMessage(object);
        message.title = appName;
        message.description = summary;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource
                (context.getResources(), R.drawable.ic_laun),
                120, 120, true);
        message.setThumbImage(thumbBmp);


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        if(type == 1){
            //分享好友
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }else if(type == 2) {
            //分享朋友圈
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }else {

        }
        api.sendReq(req);
    }



    /**
     * 构建一个唯一标志
     *
     * @param type 分享的类型分字符串
     * @return 返回唯一字符串
     */
    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /****************
     *
     * 发起添加群流程。群号：拍拍壁纸吐槽群(244587320) 的 key 为： UBWOce6o4B5jBaPY1lbHvsqaH4yXwdRF
     * 调用 joinQQGroup(UBWOce6o4B5jBaPY1lbHvsqaH4yXwdRF) 即可发起手Q客户端申请加群 拍拍壁纸吐槽群(244587320)
     *
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context  context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "UBWOce6o4B5jBaPY1lbHvsqaH4yXwdRF"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
