package com.gasaver.activity;

import static com.gasaver.utils.Constants.REQUEST_LOCATION;
import static com.gasaver.utils.Constants.USER_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.gasaver.R;
import com.gasaver.Response.ProfileUserDataResponseGasaverT;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
SplashActivity extends AppCompatActivity {

    private final String TAG = SplashActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        setContentView(R.layout.activity_splash_screen1);

        Bundle bundle = getIntent().getExtras();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //() Sercilar bracket
        Log.e(TAG, getSystemDetail());




        if (bundle != null && bundle.get("data") != null) {


            //here can get notification message
            String datas = bundle.get("data").toString();
            Toast.makeText(this, datas, Toast.LENGTH_SHORT).show();

        }
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            launchApp();
        }


    }

    @SuppressLint("HardwareIds")
    private String getSystemDetail() {
        return "Brand: " + Build.BRAND + "\n" +
                "DeviceID: " +
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) + "\n" +
                "Model: " + Build.MODEL + "\n" +
                "ID: " + Build.ID + "\n" +
                "SDK: " + Build.VERSION.SDK_INT + "\n" +
                "Manufacture: " + Build.MANUFACTURER + "\n" +
                "Brand: " + Build.BRAND + "\n" +
                "User: " + Build.USER + "\n" +
                "Type: " + Build.TYPE + "\n" +
                "Base: " + Build.VERSION_CODES.BASE + "\n" +
                "Incremental: " + Build.VERSION.INCREMENTAL + "\n" +
                "Board: " + Build.BOARD + "\n" +
                "Host: " + Build.HOST + "\n" +
                "FingerPrint: " + Build.FINGERPRINT + "\n" +
                "Version Code: " + Build.VERSION.RELEASE;
    }


    ProfileUserDataResponseGasaverT profileUserDataResponseGasaverT;

    private void saveUserDatSettings(JsonObject jsonObject) {
        CommonUtils.showLoading(SplashActivity.this, "Please Wait", false);
        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<ProfileUserDataResponseGasaverT> call = apiInterface.setUserDatSettings(jsonObject);
        call.enqueue(new Callback<ProfileUserDataResponseGasaverT>() {
            @Override
            public void onResponse(Call<ProfileUserDataResponseGasaverT> call, Response<ProfileUserDataResponseGasaverT> response) {
                CommonUtils.hideLoading();

//                BaseResponseGasaverTProperty = (baseResponseGasaverTProperty) response.body();
                profileUserDataResponseGasaverT = (ProfileUserDataResponseGasaverT) response.body();

                if (profileUserDataResponseGasaverT != null && profileUserDataResponseGasaverT.getData() != null) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("user_id", SharedPrefs.getInstance(SplashActivity.this).getString(Constants.USER_ID));
                    jsonObject.addProperty("token", SharedPrefs.getInstance(SplashActivity.this).getString(Constants.TOKEN));

                    SharedPrefs.getInstance(SplashActivity.this).saveBoolean(Constants.allow_email, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_mail()));
                    SharedPrefs.getInstance(SplashActivity.this).saveBoolean(Constants.allow_sms, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_sms()));
                    SharedPrefs.getInstance(SplashActivity.this).saveBoolean(Constants.allow_push, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_push()));
                    pushNotificationEnabled = setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_push());
                    pushNotifacation();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        pushNotifacation();
                        Log.e(TAG, "onResponse: ..................." + pushNotificationEnabled);
                    }, 2000);
//                    binding.switch_push_noti.setText(profileUserDataResponseGasaverT.getData().getMobile());

                }
            }

            @Override
            public void onFailure(Call<ProfileUserDataResponseGasaverT> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });

    }

    public static boolean pushNotificationEnabled = true;

    public void pushNotifacation() {
        if (pushNotificationEnabled) {

            // Create a notification manager
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            // Create a notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")

                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("GaSaver")
                    .setContentText("Welcome To Gasaver \uD83C\uDF89")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);


            // Create a notification channel (required for Android Oreo and above)
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            // Show the notification
            notificationManager.notify(1, builder.build());
        }
    }


    //---------------------------------------------------------
    public boolean setCheckedSetting(String input) {
        if (input == null)
            return false;
        return input.equals("Yes");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //launch app
                launchApp();

            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_LOCATION_SETTINGS && resultCode == RESULT_OK) {
//            // The user enabled GPS, do something
//        }
//    }


    public void setSetting() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(SplashActivity.this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(SplashActivity.this).getString(Constants.TOKEN));
        saveUserDatSettings(jsonObject);
    }

    private void launchApp() {
//        setSetting();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPrefs.getInstance(SplashActivity.this).getString(USER_ID).isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, GettingStarted.class);
                    startActivity(intent);
                } else {

                    Log.e(TAG, "user_id: " + SharedPrefs.getInstance(SplashActivity.this).getString(USER_ID));
                    Log.e(TAG, "api_key: " + SharedPrefs.getInstance(SplashActivity.this).getString(Constants.API_KEY));
                    Intent intent = new Intent(SplashActivity.this, MainActivityGas.class);

                    startActivity(intent);
                }
                finish();
            }
        }, 1000);
    }

}