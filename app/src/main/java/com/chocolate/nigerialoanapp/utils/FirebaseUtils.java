package com.chocolate.nigerialoanapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chocolate.nigerialoanapp.global.App;
import com.chocolate.nigerialoanapp.global.Constant;
import com.chocolate.nigerialoanapp.log.LogSaver;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseUtils {
    /**
     * Branch回调	后台	安装（归因）	SERVICE_INSTALL
     * 打开页面	前端	打开任意页面，都会触发此埋点	OPEN_ALL_PAGE
     * 未登录/已登陆（仅主产品）	前端	安装（通过监听INSTALL_REFERRER的Receiver实现）	SYSTEN_INSTALL_$APPSSID
     * 	前端	打开APP	SYSTEM_INIT_OPEN_$APPSSID
     * 	前端	隐藏APP	SYSTEM_HIDE_$APPSSID
     * 权限文案页（仅主产品）	前端	进入权限文案页	SYSTEM_PERMISSION_ENTER
     * 	前端	点击权限页隐私权限	CLICK_PERMISSIONPAGE_PRIVACYPOLICY
     * 	前端	点击权限页用户协议	CLICK_PERMISSIONPAGE_TERMSCONDITIONS
     * 	前端	点击权限文案页同意按钮	CLICK_PERMISSION_AGREE
     * 	前端	权限文案页授权结果（当弹出授权弹窗时）	SYSTEM_PERMISSION_RESULT
     * 	前端	点击权限文案页拒绝按钮	CLICK_PERMISSION_REJECT
     * 填写电话页（仅主产品）	前端	进入填写电话页	SYSTEM_TELEPHONE_ENTER
     * 	前端	点击登录/注册按钮	CLICK_LOGIN_REGISTER
     * 注册/登录页-USSD（仅主产品）	前端	进入注册页	SYSTEM_REGISTER_ENTER_USSD
     * 	前端	点击发送验证码按钮（包括自动发送）	CLICK_SEND_OTP_USSD
     * 	前端	发送验证码成功并用尽次数	SERVICE_OTP_OUT_OF_TIMES_USSD
     * 	前端	点击返回按钮	SYSTEM_LOGIN_REGISTER_BACK_USSD
     * 	前端	点击注册按钮	CLICK_REGISTER_USSD
     * 	前端	注册并登录成功	SERVICE_LOGIN_REGISTER_USSD
     * 注册/登录页-SMS（仅主产品）	前端	进入注册页	SYSTEM_REGISTER_ENTER_SMS
     * 	前端	点击发送验证码按钮（包括自动发送）	CLICK_SEND_OTP_SMS
     * 	前端	发送验证码成功并用尽次数	SERVICE_OTP_OUT_OF_TIMES_SMS
     * 	前端	点击返回按钮	SYSTEM_LOGIN_REGISTER_BACK_SMS
     * 	前端	点击注册按钮	CLICK_REGISTER_SMS
     * 	前端	注册并登录成功	SERVICE_LOGIN_REGISTER_SMS
     * 登录页（填密码页）（仅主产品）	前端	进入登录页	SYSTEM_LOGIN_ENTER
     * 	前端	点击登录按钮	CLICK_LOGIN
     * 	前端	点击返回按钮	SYSTEM_LOGIN_BACK
     * 	前端	密码登录成功	SERVICE_LOGIN_PASSWORD_SUCCESS
     * 我的页（仅主产品）	前端	点击我的页退出按钮	CLICK_LOGOUT
     * 未登录首页	前端	未登陆首页点击客服按钮	CLICK_HOMEPAGE_CUSTOMERSERVICE
     * 	前端	未登陆首页点击申请按钮	CLICK_HOMEPAGE_APPLY
     * 首页	前端	进入首贷首页（每次）	SYSTEM_INDEX_ENTER
     * 	前端	单产品首页点击客服按钮（登陆成功）	CLICK_SINGLEPRODUCTHOMEPAGE_CUSTOMERSERVICE
     * 	前端	点击申请按钮	CLICK_INDEX_APPLY
     * 个人信息页	前端	进入个人信息页	SYSTEM_BASIC_INF_ENTER
     * 	前端	个人信息页数据加载完成	SYSTEM_BASIC_INF_LOAD
     * 	前端	点击个人信息页提交按钮	CLICK_BASIC_INF_SUBMIT
     * 	前端	个人信息页返回上一页	SYSTEM_BASIC_INF_BACK
     * 	前端	无个人信息时点击提交	CLICK_BASIC_INF_NP
     * 工作信息页	前端	进入工作信息页	SYSTEM_WORK_INF_ENTER
     * 	前端	工作信息页数据加载完成	SYSTEM_WORK_INF_LOAD
     * 	前端	点击工作信息页提交按钮	CLICK_WORK_INF_SUBMIT
     * 	前端	工作信息页返回上一页	SYSTEM_WORK_INF_BACK
     * 	前端	无工作信息时点击提交	CLICK_WORK_INF_NP
     * 联系人信息页	前端	进入联系人信息页	SYSTEM_CONTACT_INF_ENTER
     * 	前端	联系人信息页数据加载完成	SYSTEM_CONTACT_INF_LOAD
     * 	前端	点击联系人页提交按钮	CLICK_CONTACT_INF_SUBMIT
     * 	前端	点击数据抓取授权同意按钮	CLICK_DEVICE_AGREE
     * 	前端	点击数据抓取授权拒绝按钮	CLICK_DEVICE_CANCEL
     * 	前端	当弹出授权弹窗时，数据抓取授权结果	SYSTEM_DEVICE_RESULT
     * 	前端	数据抓取授权全部通过	SYSTEM_DEVICE_INFO_NP
     * 	前端	开始上传设备数据	SYSTEM_DEVICE_INFO_UPLOAD
     * 	前端	完成上传设备数据	SYSTEM_DEVICE_INFO_SUCCESS
     * 	前端	联系人信息页返回上一页	SYSTEM_CONTACT_INF_BACK
     * 	前端	无联系人信息时进入页面（与其他信息页合并时改为点击首个输入框） 	CLICK_CONTACT_INF_NP
     * 银行卡信息页	前端	进入银行卡信息页	SYSTEM_BANK_CARD_ENTER
     * 	前端	银行卡信息页数据加载完成	SYSTEM_BANK_CARD_LOAD
     * 	前端	放款失败时，点击银行卡更新页提交按钮	CLICK_BANK_UPDATE_SUBMIT
     * 	前端	点击银行卡页提交按钮	CLICK_BANK_CARD_SUBMIT
     * 	前端	银行卡页返回上一页	SYSTEM_BANK_CARD_BACK
     * 	后端	填写银行账户	SERVICE_GET_BANK_CARD_INFO
     * 	前端	无银行卡信息时点击提交	CLICK_BANK_CARD_NP
     * 活体校验	后端	进入活体校验页	SYSTEM_LIVENESSTEST_ENTER
     * 	前端	活体校验页数据加载完成	SYSTEM_ILIVENESSTEST_LOAD
     * 	前端	点击开始活体校验按钮	CLICK_START_SUBMIT
     * 	前端	活体校验成功	LIVENESSTEST_SUBMIT_1_T
     * 	前端	活体校验失败	LIVENESSTEST_SUBMIT_1_F
     * 	前端	点击活体校验重试	SYSTEM_LIVENESSTEST_RETRY
     * 	前端	活体校验页返回上一页	SYSTEM_LIVENESSTEST_BACK
     * 金额期限页	前端	进入金额期限页	SYSTEM_LOAN_ENTER
     * 	前端	金额期限页数据加载完成	SYSTEM_LOAN_LOAD
     * 	前端	金额期限页修改金额（含自动选择）	CLICK_LOAN_AMOUNT
     * 	前端	金额期限页修改期限（含自动选择）	CLICK_LOAN_TERM
     * 	前端	点击金额期限页确认按钮	CLICK_LOAN_CONFIRM
     * 	前端	金额期限页返回上一页	SYSTEM_LOAN_BACK
     * 贷款确认页	前端	进入贷款确认页	SYSTEM_CONFIRM_ENTER
     * 	前端	取消勾选贷款合同	CLICK_CONTRACT_CANCEL
     * 	前端	点击贷款确认页确认按钮	CLICK_LOAN_SUBMIT
     * 	前端	贷款确认页返回上一页（取消）	CLICK_CONFIRM_BACK
     *
     *
     */

    private static final String TAG = "KudiCreditFirebase";

    public static void setUserId(Context context, String userId) {
        FirebaseAnalytics.getInstance(context).setUserId(userId);
        if (!Constant.INSTANCE.isAabBuild()) {
            Toast.makeText(context, "setUserId = " + userId, Toast.LENGTH_SHORT).show();
            LogSaver.logToFile("setUserId = " + userId);
        }
    }

    public static void logEvent(String event) {
        if (App.Companion.getInstance() == null) {
            return;
        }
        Context context = App.Companion.getInstance();
        Bundle params = new Bundle();
        if (!Constant.INSTANCE.isAabBuild()) {
            Log.e(TAG, " log event = " + event);
            Toast.makeText(context, "埋点 = " + event, Toast.LENGTH_SHORT).show();
            LogSaver.logToFile("log event = " + event);
        }
        // TODO
//        FirebaseAnalytics.getInstance(context).logEvent(event, params);
    }

    public static void logEvent(String event, String paramsKey, String paramsValue, String pKey2, String pValue2) {
        if (App.Companion.getInstance() == null) {
            return;
        }
        Context context = App.Companion.getInstance();
        Bundle params = new Bundle();
        params.putString(paramsKey, paramsValue);
        params.putString(pKey2, pValue2);

        if (!Constant.INSTANCE.isAabBuild()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(paramsKey, paramsValue);
                jsonObject.put(pKey2, pValue2);
            } catch (JSONException e) {

            }
            String result =  event + " " + jsonObject.toString();
            Log.e(TAG, " log event = " + result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        }
        // TODO
//        FirebaseAnalytics.getInstance(context).logEvent(event, params);
    }
}
