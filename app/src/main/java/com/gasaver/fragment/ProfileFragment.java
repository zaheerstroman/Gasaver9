package com.gasaver.fragment;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gasaver.R;
import com.gasaver.Response.BaseResponse;
import com.gasaver.Response.ProfileUserDataResponseGasaverT;
import com.gasaver.activity.CopunsActivity;
import com.gasaver.activity.PrivacyPolicyActivity;
import com.gasaver.activity.RateUsActivity;
import com.gasaver.activity.RewardActivity;
import com.gasaver.activity.SettingsActivity;
import com.gasaver.activity.ShareitActivity;
import com.gasaver.activity.SplashActivity;
import com.gasaver.activity.UploadActivity;
import com.gasaver.activity.WebViewActivity;
import com.gasaver.databinding.ActivityCopunsBinding;
import com.gasaver.databinding.ActivityProfileBinding;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//This is A Activity but it's seeing Fragment
public class ProfileFragment extends AppCompatActivity implements View.OnClickListener {

    ShapeableImageView iv_profile_img;
    ImageView iv_edit;

    private ActivityProfileBinding binding;
    private EditProfileFragment editProfileFragment;
    private ProfileUserDataResponseGasaverT responseProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityProfileBinding.inflate(LayoutInflater.from(getApplicationContext()));
        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
//        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        binding.layoutTerms.setOnClickListener(this);
        binding.layoutPrivacy.setOnClickListener(this);
        binding.layoutFeedbackSavedSearches.setOnClickListener(this);
        binding.layoutMyRewardsProperties.setOnClickListener(this);
        binding.layoutRateUsShortListed.setOnClickListener(this);
        binding.layoutSharitContaced.setOnClickListener(this);
        binding.layoutSettingsMyRequirements.setOnClickListener(this);
        binding.layoutMyUploadResponses.setOnClickListener(this);
        binding.ivEdit.setOnClickListener(this);
        binding.llLogout.setOnClickListener(this);

        binding.layoutMyCopunsProperties.setOnClickListener(this);


        fetchProfileDetails();

    }


    private void fetchProfileDetails() {

        CommonUtils.showLoading(this, "Please Wait", false);

        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id", SharedPrefs.getInstance(this).getString(Constants.USER_ID))
                .addFormDataPart("token", SharedPrefs.getInstance(this).getString(Constants.TOKEN)).build();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(this).getString(Constants.TOKEN));

        Call<ProfileUserDataResponseGasaverT> call = apiInterface.fetchProfileDetails(jsonObject);


        call.enqueue(new Callback<ProfileUserDataResponseGasaverT>() {
            @Override
            public void onResponse(Call<ProfileUserDataResponseGasaverT> call, Response<ProfileUserDataResponseGasaverT> response) {
                CommonUtils.hideLoading();
                try {
                    responseProfile = response.body();
                    Log.e("TAG", String.valueOf(responseProfile.getData()) );

                    // binding.tvRole.setText(responseProfile.getData().getUserCode());
                    // binding.tvRole.setText(String.valueOf(responseProfile.getData().getUserCode()));

                    if (responseProfile.getData().getName() != null)
                    {
                        binding.tvProfileName.setText(String.valueOf(responseProfile.getData().getName()));
                    }else binding.tvProfileName.setText("");


                    if (responseProfile.getData().getEmail() != null)
                    {
                        binding.tvEmail.setText(String.valueOf(responseProfile.getData().getEmail()));
                    }else binding.tvEmail.setText("");


                    if (responseProfile.getData().getMobile() != null)
                    {
                        binding.tvPhone.setText(String.valueOf(responseProfile.getData().getMobile()));
                    }else binding.tvPhone.setText("");


                    if (responseProfile.getData().getUserCode() != null)
                    {
                        binding.tvRole.setText(String.valueOf(responseProfile.getData().getUserCode()));
                    }else binding.tvRole.setText("");


                    if (responseProfile.getRewardPoints() != null)
                    {
                        binding.txtRewardPoints.setText(String.valueOf(responseProfile.getRewardPoints()));
                    }else binding.txtRewardPoints.setText("");





                    try {
                        SharedPrefs.getInstance(ProfileFragment.this).saveBoolean(Constants.allow_email, responseProfile.getData().getAllow_mail().equalsIgnoreCase("Yes"));
                        SharedPrefs.getInstance(ProfileFragment.this).saveBoolean(Constants.allow_sms, responseProfile.getData().getAllow_sms().equalsIgnoreCase("Yes"));
                        SharedPrefs.getInstance(ProfileFragment.this).saveBoolean(Constants.allow_push, responseProfile.getData().getAllow_push().equalsIgnoreCase("Yes"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Glide.with(ProfileFragment.this).load(response.body().getBase_path() + response.body().getData().getProfilePhoto())
                            .error(R.drawable.profile_img).error(R.drawable.profile_img).into(binding.ivProfileImg);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ProfileUserDataResponseGasaverT> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_terms:

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.TERMS_COND_URL));
                startActivity(i);
                break;
            case R.id.layout_privacy:

                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.PRIVACY_POLICY_URL));
                startActivity(i);
                break;
            case R.id.layout_myUploadResponses:
                Intent intent = new Intent(this, UploadActivity.class);
                intent.putExtra("SELECTED_POS", 0);
                startActivity(intent);
                break;

            case R.id.layout_myCopunsProperties:
                intent = new Intent(this, CopunsActivity.class);
                intent.putExtra("SELECTED_POS", 0);
                startActivity(intent);
                break;


            case R.id.layout_feedback_savedSearches:

                showFeedbackDilog();
                break;

            case R.id.layout_rateUs_shortListed:
                intent = new Intent(this, RateUsActivity.class);
                intent.putExtra("SELECTED_POS", 4);
                startActivity(intent);
                break;

            case R.id.layout_sharit_contaced:
                intent = new Intent(this, ShareitActivity.class);
                intent.putExtra("SELECTED_POS", 3);
                startActivity(intent);
                break;

            case R.id.layout_settings_myRequirements:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("SELECTED_POS", 2);
                startActivity(intent);
                break;

            case R.id.layout_myRewardsProperties:
                intent = new Intent(this, RewardActivity.class);
//                intent = new Intent(this, UploadActivity.class);
                intent.putExtra("SELECTED_POS", 1);
                startActivity(intent);
                break;
            case R.id.iv_edit:

                if (responseProfile != null) {
                    intent = new Intent(ProfileFragment.this, EditProfileFragment.class);
                    editProfileFragment = new EditProfileFragment();

                    intent.putExtra("reward_points", (responseProfile.getRewardPoints()));
                    intent.putExtra("bar_code", (responseProfile.getBarCode()));
                    intent.putExtra("basepath", (responseProfile.getBase_path()));
                    intent.putExtra("data", new Gson().toJson(responseProfile.getData()));

                    Bundle bundle = new Bundle();
//                    editProfileFragment.setArguments(bundle);
//                    editProfileFragment.setDismissListener(new EditProfileFragment.DismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            fetchProfileDetails();
//                        }
//                    });
//                    editProfileFragment.show(getParentFragmentManager(), "");



                    startActivityForResult(intent,102);
                }
                break;

            case R.id.ll_logout:
                SharedPrefs.getInstance(this).clearSharedPrefs();

                Intent intent1 = new Intent(this, SplashActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;
        }

    }

    private void showFeedbackDilog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback_dialog);
        dialog.setCancelable(false);

        Button btn_submit = dialog.findViewById(R.id.btn_submit);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        EditText et_feedback = dialog.findViewById(R.id.et_feedback);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_feedback.getText().toString().trim()))
                    postFeedback(dialog, et_feedback.getText().toString());
                else
                    Toast.makeText(ProfileFragment.this, "Please Enter message to submit feedback", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void postFeedback(Dialog dialog, String msg) {

        CommonUtils.showLoading(this, "Please Wait", false);
        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", SharedPrefs.getInstance(this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(this).getString(Constants.TOKEN));

        jsonObject.addProperty("description", msg);

        Call<BaseResponse> call = apiInterface.feedback(jsonObject);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse baseResponse = response.body();
                if (baseResponse != null && baseResponse.isStatus_code()) {

                    dialog.dismiss();
                    Toast.makeText(ProfileFragment.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();

                }
                CommonUtils.hideLoading();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(ProfileFragment.this, "Something went wrong. Please Try again", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                CommonUtils.hideLoading();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==102&&resultCode==RESULT_OK)
            fetchProfileDetails();
    }
}