package com.gasaver.activity;

import static com.google.firebase.messaging.Constants.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gasaver.R;
import com.gasaver.Response.BaseResponse;
import com.gasaver.Response.BaseResponseGasaverTProperty;
import com.gasaver.Response.ProfileUserDataResponseGasaverT;
import com.gasaver.Response.StationDataResponse;
import com.gasaver.databinding.ActivitySettingsBinding;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

//    private ProfileUserDataResponseGasaverT profileUserDataResponseGasaverT;
    public BaseResponseGasaverTProperty baseResponseGasaverTProperty;
    ProfileUserDataResponseGasaverT profileUserDataResponseGasaverT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());


        init();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().hide();
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });


    }

    private void init() {


        setContentView(binding.getRoot());
        setSetting();
        binding.switchPushNoti.setChecked(SharedPrefs.getInstance(this).getBoolean(Constants.allow_push, true));
        binding.switchSmsNot.setChecked(SharedPrefs.getInstance(this).getBoolean(Constants.allow_sms, true));
        binding.switchEmailNot.setChecked(SharedPrefs.getInstance(this).getBoolean(Constants.allow_email, true));
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("user_id", SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.USER_ID));
                jsonObject.addProperty("token", SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.TOKEN));
                jsonObject.addProperty("allow_email", binding.switchEmailNot.isChecked() ? "Yes" : "No");
                jsonObject.addProperty("allow_sms", binding.switchSmsNot.isChecked() ? "Yes" : "No");
                jsonObject.addProperty("allow_push", binding.switchPushNoti.isChecked() ? "Yes" : "No");

                saveUserSettings(jsonObject);



            }
        });
    }

    public void setSetting()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id",SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.USER_ID));
        jsonObject.addProperty("token",SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.TOKEN));
        saveUserDatSettings(jsonObject);
    }
    private void saveUserSettings(JsonObject jsonObject) {
        CommonUtils.showLoading(SettingsActivity.this, "Please Wait", false);
        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiInterface.settUserSettings(jsonObject);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                CommonUtils.hideLoading();
                if (response.body().isStatus_code()) {


//                    https://houseofspiritshyd.in/gasaver/admin/api/userSettings
//                    +
                    //https://houseofspiritshyd.in/gasaver/admin/api/getUserData

                    SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_email, binding.switchEmailNot.isChecked());
                    SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_sms, binding.switchSmsNot.isChecked());
                    SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_push, binding.switchPushNoti.isChecked());


                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });

    }

    //ProfileUserDataResponseGasaverT

    private void saveUserDatSettings(JsonObject jsonObject) {
        CommonUtils.showLoading(SettingsActivity.this, "Please Wait", false);
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
                        jsonObject.addProperty("user_id", SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.USER_ID));
                        jsonObject.addProperty("token", SharedPrefs.getInstance(SettingsActivity.this).getString(Constants.TOKEN));

                        SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_email, binding.switchEmailNot.isChecked());
                        SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_sms, binding.switchSmsNot.isChecked());
                        SharedPrefs.getInstance(SettingsActivity.this).saveBoolean(Constants.allow_push, binding.switchPushNoti.isChecked());

//                    binding.switch_push_noti.setText(profileUserDataResponseGasaverT.getData().getMobile());

                        binding.switchPushNoti.setChecked(setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_push()));
                        binding.switchSmsNot.setChecked(setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_sms()));
                        binding.switchEmailNot.setChecked(setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_mail()));

                }
            }

            @Override
            public void onFailure(Call<ProfileUserDataResponseGasaverT> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });

    }

    public boolean setCheckedSetting(String input)
    {
        if (input == null) {
            return false;
        }
        Log.e(TAG, input );
        //Ali
        return input.equals("Yes");
        //Zaheer
//        return input.equals("No");

    }

}