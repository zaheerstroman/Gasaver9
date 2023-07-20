package com.gasaver.activity;

import static com.gasaver.activity.AdvancedBannerSlidSearchActivity.context;
//import static com.gasaver.fragment.HomeFragmentGasaver.getMyLocationNow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.gasaver.R;
import com.gasaver.Response.GraphReportsResponse;
import com.gasaver.databinding.ActivityMainGasBinding;
import com.gasaver.databinding.NavDrawerLayoutBinding;
import com.gasaver.databinding.ToolbarLayoutBinding;
import com.gasaver.fragment.HomeFragmentGasaver;
import com.gasaver.fragment.ProfileFragment;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import io.sentry.Sentry;

public class MainActivityGas extends AppCompatActivity {

    private GoogleMap map, googleMap, mGoogleMap;
//    Spinner spinner_caseText1, registertype_spinner, spinner_subcat;

    PowerSpinnerView spinner_caseText1, spinner_subcat;

    private final String[] budgetArrayList = {"", "E10", "Diesel", "AdBlue", "Unleaded", "PremDSL", "U95", "TruckDSL", "E85", "U98", "U91", "P95", "P98", "PDL", "B20", "EV", "DL"};



    public NavDrawerLayoutBinding navDrawerLayoutGasBinding;

    public ActivityMainGasBinding activityMainGasBinding;

    public ToolbarLayoutBinding toolbarLayoutGasBinding;


    public FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navDrawerLayoutGasBinding = NavDrawerLayoutBinding.inflate(getLayoutInflater());

        setContentView(navDrawerLayoutGasBinding.getRoot());

//        spinner_caseText1 = (Spinner) findViewById(R.id.spinner_caseText1);
        spinner_caseText1 = (PowerSpinnerView) findViewById(R.id.spinner_caseText1);
        spinner_subcat = (PowerSpinnerView) findViewById(R.id.spinner_subcat);

//        spinner_subcat.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_units, getResources().getStringArray(R.array.budgets)));

        activityMainGasBinding = navDrawerLayoutGasBinding.mainActivityGas;


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen



        //Ask enable gps onCreate/ Location Service Enable:-----------------
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, prompt the user to enable it
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Enabled");
            builder.setMessage("Please enable location services to use this feature.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }


        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.btnMenu);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

                if (fragment instanceof HomeFragmentGasaver)
                    ((HomeFragmentGasaver) fragment).showBottomSheet();

            }
        });


        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.btnHybrid);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowGraph();
                startActivity(new Intent(MainActivityGas.this, GraphActivityGeeks.class));

            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.btnReward);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityGas.this, RewardActivity.class));

            }
        });


        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.btnSetting);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityGas.this, SettingsActivity.class));


            }
        });


        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_geeks);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.home:

//                        startActivity(new Intent(getApplicationContext(), HomeFragmentGasaver.class));
//                                                overridePendingTransition(0,0);

                        Fragment navHostFragment1 = getSupportFragmentManager().getPrimaryNavigationFragment();
                        Fragment fragment1 = navHostFragment1.getChildFragmentManager().getFragments().get(0);

                        if (fragment1 instanceof HomeFragmentGasaver)
//                            ((HomeFragmentGasaver) fragment1).getMyLocationNow();
//                            ((HomeFragmentGasaver) fragment1).getCurrentLocation();
                            ((HomeFragmentGasaver) fragment1).getCurrentLocation();


                        ;

//                        bottomNavigationView.setSelectedItemId(R.id.home);

//                        getMyLocationNow();


//                        bottomNavigationView.setOnNavigationItemSelectedListener(item1 -> {
//                            if (item.getItemId() == R.id.home) {
//                                getMyLocationNow();
//                                return true;
//                            }
//                            return false;
//                        });

                        //Vinni
//                        R.id.bottomNavigationView.home.setOnClickListener(currentLocation -> getCurrentLocation());
                        //Ali
//        binding.currentLocation.setOnClickListener(v -> getMyLocationNow());
//                        R.id.home.setOnClickListener(v -> getMyLocationNow());
//                        if (fragment1 instanceof HomeFragmentGasaver)
//                            ((HomeFragmentGasaver) fragment1).showBottomSheet();

                        return true;

//                        Intent intent3 = new Intent(getApplicationContext(), HomeFragmentGasaver.class);
//                        startActivity(intent3);
//                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


//                        ProfileFragment addPhotoBottomDialogFragment =
//                                new ProfileFragment();
//                        addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "");
//                        return true;

//                        break;

                    case R.id.dashboard:

                        Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

                        if (fragment instanceof HomeFragmentGasaver)
                            ((HomeFragmentGasaver) fragment).showBottomSheet();

                        return true;


                    case R.id.nav_search:

                        Intent intent = new Intent(getApplicationContext(), AdvancedBannerSlidSearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;


                    case R.id.about:


                         intent = new Intent(getApplicationContext(), WishListActivity.class);

                        navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                        fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                        if (fragment instanceof HomeFragmentGasaver) {
                            intent.putExtra("data", new Gson().toJson(((HomeFragmentGasaver) fragment).stationDataList));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        overridePendingTransition(0, 0);
                        return true;


                    case R.id.profile:

                        Intent intent1 = new Intent(getApplicationContext(), ProfileFragment.class);
                        startActivity(intent1);

//                        ProfileFragment addPhotoBottomDialogFragment =
//                                new ProfileFragment();
//                        addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "");


                        return true;

                }
                return false;
            }
        });


//        -----------------------------------------------------------------------------------------------


        NavController navController = Navigation.findNavController(this, R.id.fragmentContainer);
        NavigationUI.setupWithNavController(

                navDrawerLayoutGasBinding.navigationView,
                navController
        );

    }

    private void ShowGraph() {
        CommonUtils.showLoading(getApplicationContext(), "Please Wait", false);

        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", "119");
        jsonObject.addProperty("token", "24631cdd0323cea063e6cb4e5b2b0f6606540a5ae48428ed41e412131efb3c5a");
        jsonObject.addProperty("city", "sydney");

        Call<GraphReportsResponse> call = apiInterface.fetchGraphReports(jsonObject);

        call.enqueue(new Callback<GraphReportsResponse>() {

            @Override
            public void onResponse(Call<GraphReportsResponse> call, Response<GraphReportsResponse> response) {

                GraphReportsResponse graphReportsResponse = response.body();

                if (graphReportsResponse != null && !graphReportsResponse.getGraphReport().isEmpty()) {

                    if (graphReportsResponse.getMessage() != null && !graphReportsResponse.getMessage().isEmpty()) {

                        // Extract the graph image URL from the response
                        String graphImageUrl = graphReportsResponse.getGraphReport();
                        final ImagePopup imagePopup = new ImagePopup(MainActivityGas.this);
                        imagePopup.setBackgroundColor(Color.TRANSPARENT);
                        imagePopup.setFullScreen(false);
                        imagePopup.setHideCloseIcon(true);
                        imagePopup.setImageOnClickClose(true);
                        imagePopup.setHideCloseIcon(true);
                        imagePopup.setMaxWidth(60);
                        imagePopup.setMaxHeight(200);
                        imagePopup.initiatePopupWithPicasso(graphImageUrl);
                        imagePopup.viewPopup();

//                        Uri uri = Uri.parse("https://de.wikipedia.org/wiki/Scalable_Vector_Graphics#/media/File:SVG_logo.svg");
                        Uri uri = Uri.parse("https://petrolspy.com.au/graph/sydney_U91E10.svg");

                        // initialize your imageview


//                        requestBuilder
//                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                // SVG cannot be serialized so it's not worth to cache it
//                                .load(uri)
////                                .into(mImageView);
//                                .into(ImageView);



//                        requestBuilder = Glide.with(mActivity)
//                                .using(Glide.buildStreamModelLoader(Uri.class, mActivity), InputStream.class)
//                                .from(Uri.class)
//                                .as(SVG.class)
//                                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
//                                .sourceEncoder(new StreamEncoder())
//                                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
//                                .decoder(new SvgDecoder())
//                                .placeholder(R.drawable.profile)
//                                .error(R.drawable.profile_img)
//                                .animate(android.R.anim.fade_in)
//                                .listener(new SvgSoftwareLayerSetter<Uri>());

                    }
                }
                CommonUtils.hideLoading();
            }

            @Override
            public void onFailure(Call<GraphReportsResponse> call, Throwable t) {

                t.printStackTrace();
                CommonUtils.hideLoading();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (navDrawerLayoutGasBinding.navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawerLayoutGasBinding.navDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


}