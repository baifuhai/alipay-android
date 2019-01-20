package com.alipay.sdk.pay.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.R;
import com.alipay.sdk.pay.demo.model.AuthResult;
import com.alipay.sdk.pay.demo.model.PayResult;
import com.alipay.sdk.pay.demo.util.PayUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  重要说明：
 *  
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 */
public class PayDemoActivity extends AppCompatActivity {

	/**
	 * 用于支付宝支付业务的入参 app_id。
	 */
	public static final String APPID = "2016092500590658";

	/**
	 * 用于支付宝账户登录授权业务的入参 pid。
	 */
	public static final String PID = "";

	/**
	 * 用于支付宝账户登录授权业务的入参 target_id。
	 */
	public static final String TARGET_ID = "";

	/**
	 *  pkcs8 格式的商户私钥。
	 *
	 * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
	 * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
	 * 	RSA2_PRIVATE。
	 *
	 * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
	 * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
	 */
	public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAEi2xNJ/oHRQBLzlJDNuVOQL2h3HUsfQWHOJKSdYg5lhegK4gYxeJvVWT3rZK7bwm27SI77zMKAUzBC9PmcXQ0fPOw7b6g+ee84HwZVIG3EU6SFDExxfd5oY3ZTWgNiAp9TSIt+cEDtYlxy3qPhmSGqT0DIlo0/3OflgTneaPewNBw90RUFwgp+yloNRiXVs6lTlf98lCtoVFc8glb8Hy8tqr8LCBqbDhuOFm8csu/eRsoY3+zPS6zw9tPBobwqI8lv+I8aAVvr9KqzmJwaImb/H192ZE0ZOciTuRqvegzl5dYxSY2n2tskm801GMUj++ecL700RxPYfv1+KCT6UxAgMBAAECggEAKDZmHJiw9e7IlmrlqnanrnlONoErAuXy/YI0mmsVCrRPQhHc4uj8L5lVRm01a0CUyOnsxVK0C2ZBmGnC4f6G3O5oBh0RvSdKogvHY6ZC4y7Qy6ACUQCB7bJq3UQyUwWh/EbbJdii5EWa7jPiWA2CWwV7DaFmT2060TXAiPLhJ56zkvgy/Gq+rhUm5/8VYFrEeQ3ygoh4sFBooepcA+oRwDkDSzV2Cke3sLM/ZTJSGXLGONgP1V3SdnRK3UrQCglAyuI0oiyCQs48ndrl/7GMZ+DxK2UP6uhNsU6ysOVetGwt2YDTG2ZJ1vunub7ygsA81PznTZAC3VFcO339JUqVxQKBgQDqpXFEHgTAEFgE/BfCesVVR4De+YfLCI1UsXR/W2TrIccG0PxG4ewPwcAUybKyXAxloHys8qL9XFK8wSS/uSxgaiKWIhZkJM9+ab7dHOSGaYS0RAxTPaZ+f3jsuYECFRoLXhGvHWKmb675dy9jXHsLcTdXmLSXyoMSk2S8BXTRjwKBgQDRjNwas7crIgVxY66LxHpJTnmUD8YJl/gaUEoHF0hvE2QlH5TrL8zRMpMYZJH33EZDOyatHZizsWxFgfy4+0sblxrrqV4qItDHHiqWLYxppNNW/QKwXhbwlt8W5eULsjJxrR5zMP+gN1MGI19guyk/zy1BpD9V9lX6NJyh/s89PwKBgQCuq+2/OWFr4D7FpyvAuEfBjfLfCX4OpBUhYOBKFizv5DsWVegWlAN4T1C/qM3/fAo2dNuamxy94kYtANJsblocg6WvgTyN2+EiR3Gvg9ySOmZxEt7h8FjKffX0srrYZAD5SVN8ujE/mI/2YMTEAIoQIH2EFccJ39TmtRYd6Snm/QKBgD4dBLkdgDPW18oug/SF/sFv83XB3y/EzhPurKLBcgUKuGqQm/HCr3FrDlLyrJnNvz36cJJr7XGGK9PGngSu6Cp7dc1Y3MKco0oCVRlC0xYVd1eXk453tVEHR4VgC66V2YH8kMQQSBVTkNaG8aSYlbeGT6Hfb66pX/7UTYFvKDq9AoGBAMaHOT+w16KhL659YK5aTIK0kPU2saPqqwdB39TymEFiclkOL3US++RRepRShrpWv0xNpQSXQwXO1M8YjduSDDUhimoI2PEtFGE5LEOLqg5GDqD4/MBy/w3tP8zXQgxJ+jCPcOGs0/PTnoCIFsdUQvxMQJRQXXV5ojb5rQOQglxG";
	public static final String RSA_PRIVATE = "";

	public static final String SIGN_TYPE = "RSA2";

	public static final boolean IS_RSA2 = RSA2_PRIVATE.length() > 0;
	public static final String PRIVATE_KEY = IS_RSA2 ? RSA2_PRIVATE : RSA_PRIVATE;

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

				//支付结果
				case SDK_PAY_FLAG: {
					//对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					PayResult payResult = new PayResult((Map<String, String>) msg.obj);
					String resultStatus = payResult.getResultStatus();
					String resultInfo = payResult.getResult();
					if (TextUtils.equals(resultStatus, "9000")) {
						showAlert(PayDemoActivity.this, getString(R.string.pay_success) + resultInfo);
					} else {
						showAlert(PayDemoActivity.this, getString(R.string.pay_failed) + resultInfo);
					}
					break;
				}

				//授权结果
				case SDK_AUTH_FLAG: {
					AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
					String resultStatus = authResult.getResultStatus();
					String resultCode = authResult.getResultCode();
					String resultInfo = authResult.getResult();
					if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(resultCode, "200")) {
						// 获取alipay_open_id，调支付时作为参数extern_token的value
						// 传入，则支付账户为该授权账户
						showAlert(PayDemoActivity.this, getString(R.string.auth_success) + resultInfo);
					} else {
						// 其他状态值则为授权失败
						showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + resultInfo);
					}
					break;
				}

				default:
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		requestPermission();
	}

	/**
	 * 获取权限使用的 RequestCode
	 */
	private static final int PERMISSIONS_REQUEST_CODE = 1002;

	/**
	 * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
	 * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
	 * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
	 */
	private void requestPermission() {
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
			|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.READ_PHONE_STATE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE
					}, PERMISSIONS_REQUEST_CODE);

		} else {
			showToast(this, getString(R.string.permission_already_granted));
		}
	}

	/**
	 * 权限获取回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_CODE: {
				// 用户取消了权限弹窗
				if (grantResults.length == 0) {
					showToast(this, getString(R.string.permission_rejected));
					return;
				}

				// 用户拒绝了某些权限
				for (int x : grantResults) {
					if (x == PackageManager.PERMISSION_DENIED) {
						showToast(this, getString(R.string.permission_rejected));
						return;
					}
				}

				// 所需的权限均正常获取
				showToast(this, getString(R.string.permission_granted));
			}
		}
	}

	/**
	 * 支付宝支付业务示例
	 *
	 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
	 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
	 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
	 * orderInfo 的获取必须来自服务端；
	 */
	public void payV2(View v) throws Exception {
		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
			showAlert(this, getString(R.string.error_missing_appid_rsa_private));
			return;
		}

		Map<String, String> params = new HashMap<>();
		params.put("app_id", APPID);
		params.put("charset", "utf-8");
		params.put("method", "alipay.trade.app.pay");
		params.put("sign_type", SIGN_TYPE);
		params.put("timestamp", sdf.format(new Date()));
		params.put("version", "1.0");

		Map<String, String> bizMap = new HashMap<>();
		bizMap.put("out_trade_no", PayUtil.getOutTradeNo());
		bizMap.put("product_code", "QUICK_MSECURITY_PAY");
		bizMap.put("total_amount", "0.01");
		bizMap.put("subject", "测试");
		bizMap.put("body", "商品");
		bizMap.put("timeout_express", "30m");

		params.put("biz_content", new Gson().toJson(bizMap));

		final String orderInfo = PayUtil.sign(params, PRIVATE_KEY, SIGN_TYPE);

		// 必须异步调用
		new Thread(new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("pay_result", result.toString());

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 支付宝账户授权业务示例
	 *
	 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
	 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
	 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
	 * authInfo 的获取必须来自服务端；
	 */
	public void authV2(View v) throws Exception {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE)) || TextUtils.isEmpty(TARGET_ID)) {
			showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
			return;
		}

		Map<String, String> params = new HashMap<>();

		// 商户签约拿到的app_id，如：2013081700024223
		params.put("app_id", APPID);

		// 商户签约拿到的pid，如：2088102123816631
		params.put("pid", PID);

		// 服务接口名称， 固定值
		params.put("apiname", "com.alipay.account.auth");

		// 商户类型标识， 固定值
		params.put("app_name", "mc");

		// 业务类型， 固定值
		params.put("biz_type", "openservice");

		// 产品码， 固定值
		params.put("product_id", "APP_FAST_LOGIN");

		// 授权范围， 固定值
		params.put("scope", "kuaijie");

		// 商户唯一标识，如：kkkkk091125
		params.put("target_id", TARGET_ID);

		// 授权类型， 固定值
		params.put("auth_type", "AUTHACCOUNT");

		// 签名类型
		params.put("sign_type", SIGN_TYPE);

		final String authInfo = PayUtil.sign(params, PRIVATE_KEY, SIGN_TYPE);

		// 必须异步调用
		new Thread(new Runnable() {
			@Override
			public void run() {
				AuthTask authTask = new AuthTask(PayDemoActivity.this);
				Map<String, String> result = authTask.authV2(authInfo, true);
				Log.i("auth_result", result.toString());

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 将 H5 网页版支付转换成支付宝 App 支付的示例
	 */
	public void h5Pay(View v) {
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();

		/*
		 * URL 是要测试的网站，在 Demo App 中会使用 H5PayDemoActivity 内的 WebView 打开。
		 *
		 * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
		 * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
		 * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
		 * 进行测试。
		 * 
		 * H5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
		 * 可以参考它实现自定义的 URL 拦截逻辑。
		 */
		String url = "https://m.taobao.com";
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}

	/**
	 * 获取支付宝 SDK 版本号。
	 */
	public void showSdkVersion(View v) {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		showAlert(this, getString(R.string.alipay_sdk_version_is) + version);
	}

	private static void showAlert(Context ctx, String info) {
		showAlert(ctx, info, null);
	}

	private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
		new AlertDialog.Builder(ctx)
				.setMessage(info)
				.setPositiveButton(R.string.confirm, null)
				.setOnDismissListener(onDismiss)
				.show();
	}

	private static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

}
