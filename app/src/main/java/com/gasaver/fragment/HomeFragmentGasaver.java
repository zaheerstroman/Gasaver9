package com.gasaver.fragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.fragment.app.FragmentManager.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.gasaver.activity.SplashActivity.pushNotificationEnabled;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gasaver.ApiInterface;
import com.gasaver.GooglePlaceModel;
import com.gasaver.NearLocationInterface;
import com.gasaver.PlaceModel;
import com.gasaver.R;
import com.gasaver.Response.CatResponse;
import com.gasaver.Response.DefaultDataModel;
import com.gasaver.Response.ProfileUserDataResponseGasaverT;
import com.gasaver.Response.StationDataResponse;
import com.gasaver.Response.WishlistResponse;
import com.gasaver.SavedPlaceModel;
import com.gasaver.activity.MainPickmanActivity;
import com.gasaver.activity.WishListActivity;
//import com.gasaver.adapter.GooglePlaceAdapter;
import com.gasaver.adapter.InfoWindowAdapter;
import com.gasaver.constant.AllConstant;
import com.gasaver.databinding.FragmentHomeGasaverBinding;
import com.gasaver.model.CategoryModel;
import com.gasaver.model.MyItem;
import com.gasaver.model.googleplacemodel.GoogleResponseModel;
import com.gasaver.network.APIClient;
import com.gasaver.permissions.AppPermissions;
import com.gasaver.utility.LoadingDialog;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.DrawableClickListener;
import com.gasaver.utils.SharedPrefs;
import com.gasaver.view.FuelDistanceEmployeeListFragment;
import com.gasaver.webservices.RetrofitAPI;
import com.gasaver.webservices.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


//FindRoutesTechMirrors
public class HomeFragmentGasaver extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NearLocationInterface {

    //    private PostPropertyModel postPropertyModel;
    private CategoryModel categoryModel;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;


    //    Spinner spinner_budget;
    Spinner spinner_caseText1, spinner_subcat;

    //Category
    String fuelid = "1";

    //Sub Category
    String catid = "1";

    //    private String[] budgetArrayList = {"", "E10", "Diesel", "AdBlue", "Unleaded", "PremDSL", "U95", "TruckDSL", "E85", "U98", "U91", "P95", "P98", "PDL", "B20", "EV", "DL"};
    private final String[] budgetArrayList = {"", "E10", "DL", "AdBlue", "PremDSL", "U95", "TruckDSL", "E85", "U98", "U91", "P95", "P98", "PDL", "EV"};


    public static Context context;


    private FragmentHomeGasaverBinding binding;

    private GoogleMap homeMap;

    String URLString;


    //NearMe
//    private GoogleMap mGoogleMap;
    public static GoogleMap mGoogleMap;

    public static FuelDistanceEmployeeListFragment addPhotoBottomDialogFragment;

    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation, pumpLocation;
    private FirebaseAuth firebaseAuth;
    private Marker currentMarker;
    private LoadingDialog loadingDialog;
    private final int radius = 5000;
    private RetrofitAPI retrofitAPI;
    private List<GooglePlaceModel> googlePlaceModelList;
    private PlaceModel selectedPlaceModel;
//    private GooglePlaceAdapter googlePlaceAdapter;


    private GoogleMap.InfoWindowAdapter infoWindowAdapter;

    private ArrayList<String> userSavedLocationId;
    private DatabaseReference locationReference, userLocationReference;

    //Game App Studio Map Direction:----

    public static boolean asc;
    public static boolean price_asc;

    //   private GoogleMap map;
    private ApiInterface apiInterface;
    private List<LatLng> polylinelist;
    private PolylineOptions polylineOptions;
    //    private latlng origion, destination;
    private LatLng origion, dest;
    private GoogleMap map;


    //MapoDirectionCodeWorked:----
//    private GoogleMap mMap;
    //    private ActivityMapsBinding binding;
    private Context mContext;

    //FINDROUTESTECHMIRRORS:----
    //google map object
    private GoogleMap mMap;

    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    //polyline object
    private final List<Polyline> polylines = null;

    View view;

    private ArrayList<CategoryModel> catList = new ArrayList<>();
    private ArrayList<CategoryModel> subcatList = new ArrayList<>();
    public ArrayList<StationDataResponse.StationDataModel> stationDataList = new ArrayList<>();


    public ArrayList<WishlistResponse.WishlistModel> WishlistList = new ArrayList<>();

    public ArrayList<StationDataResponse> stationDataList1 = new ArrayList<>();

    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> clusterManager;

    public StationDataResponse stationDataResponse;

    //    private String distance = "distance";
//    private String distance = "100";
    private String distance = "30";

    private final String searchCity = "Sydney";

    private double searchCityLat = -33.8688197, searchCityLang = 151.2092955;

//    List<MainData> tempList = new ArrayList<>();

    public static HomeFragmentGasaver context1;

    private static final int NOTIFICATION_ID = 1;

    ProfileUserDataResponseGasaverT profileUserDataResponseGasaverT;
    private boolean isSearching;

    public boolean setCheckedSetting(String input) {
        if (input == null)
            return false;
        return input.equals("Yes");


    }


    public void setSetting() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(requireContext()).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(requireContext()).getString(Constants.TOKEN));
        saveUserDatSettings(jsonObject);

        getCurrentLocation();

    }

    private void saveUserDatSettings(JsonObject jsonObject) {
        CommonUtils.showLoading(requireContext(), "Please Wait", false);
        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);
        Call<ProfileUserDataResponseGasaverT> call = apiInterface.setUserDatSettings(jsonObject);
        call.enqueue(new Callback<ProfileUserDataResponseGasaverT>() {
            @Override
            public void onResponse(Call<ProfileUserDataResponseGasaverT> call, Response<ProfileUserDataResponseGasaverT> response) {
                CommonUtils.hideLoading();

//                BaseResponseGasaverTProperty = (baseResponseGasaverTProperty) response.body();
                profileUserDataResponseGasaverT = (ProfileUserDataResponseGasaverT) response.body();

                if (profileUserDataResponseGasaverT != null && profileUserDataResponseGasaverT.getData() != null) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("user_id", SharedPrefs.getInstance(requireContext()).getString(Constants.USER_ID));
                    jsonObject.addProperty("token", SharedPrefs.getInstance(requireContext()).getString(Constants.TOKEN));

                    SharedPrefs.getInstance(requireContext()).saveBoolean(Constants.allow_email, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_mail()));
                    SharedPrefs.getInstance(requireContext()).saveBoolean(Constants.allow_sms, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_sms()));
                    SharedPrefs.getInstance(requireContext()).saveBoolean(Constants.allow_push, setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_push()));
                    pushNotificationEnabled = setCheckedSetting(profileUserDataResponseGasaverT.getData().getAllow_push());
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        pushNotifacation();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Fragment
        binding = FragmentHomeGasaverBinding.inflate(inflater, container, false);


        appPermissions = new AppPermissions();
        firebaseAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(requireActivity());
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        googlePlaceModelList = new ArrayList<>();
        userSavedLocationId = new ArrayList<>();
        locationReference = FirebaseDatabase.getInstance().getReference("Places");

        context1 = HomeFragmentGasaver.this;

        //-----------------------------

//        getMyLocationNow(requireContext());

        //---------------------------------------------------------

//        binding.spinnerCaseText1.setVisibility(View.GONE);
//        binding.spinnerSubcat.setVisibility(View.GONE);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.homeMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl("https://maps.googleapis.com/").build();
        apiInterface = retrofit.create(ApiInterface.class);
        //request location permission.
        requestPermision();
        binding.btnMapType.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case R.id.btnMenu:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                        mGoogleMap.setOnInfoWindowCloseListener(Color.RED);

                        break;

                    case R.id.btnNormal:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;

                    case R.id.btnSatellite:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;

                    case R.id.btnTerrain:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;

                    case R.id.btnHybrid:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

//               case R.id.btnGraph:
//                  mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                  break;

                    case R.id.btnSearchPlus:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

                    case R.id.btnReward:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

                    case R.id.btnSetting:
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

//                    case R.id.btnGalary:
//                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        break;
//
//                    case R.id.btnPlus:
//                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        break;
//
//                    case R.id.btnMaines:
//                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        break;

                }
                return true;
            });

            popupMenu.show();
        });


        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });


        binding.enableTraffic.setOnClickListener(view -> {

            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }

        });

        //Vinni
        binding.currentLocation.setOnClickListener(currentLocation -> getCurrentLocation());
        //Ali
//        binding.currentLocation.setOnClickListener(v -> getMyLocationNow());
        binding.currentLocation.setOnClickListener(v -> getMyLocationNow());


        binding.btnSearchPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });


        // Get a reference to the Google Map object

        binding.edtPlaceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getPlaces(s.toString());
            }
        });
        try {

            if (!Places.isInitialized()) {
                Places.initialize(getActivity(), getResources().getString(R.string.API_KEY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) binding.getRoot().findViewById(R.id.edit_search);
        new FetchCityNamesTask(requireContext(), autoCompleteTextView).execute();

        // Define an array of city names to zzzzzuse as suggestions
//        String[] cityNames = new String[]{"New York", "Los Angeles", "Chicago", "Houston", "Philadelphia", "Phoenix", "San Antonio", "San Diego", "Dallas", "San Jose"};
        String[] cityNames = new String[]{"Sydney", "Melbourne", "Canberra", "Brisbane", "Adelaide", "Perth", "Darwin", "Hobart", "Newcastle", "Cairns",
                "Townsville", "Geelong", "Wollongong", "Gold Coast", "Ballarat", "Bundaberg", "Bendigo", "Bathurst", "Albany", "Bunbury", "Mackay", "Dubbo", "Orange"};

// Create an ArrayAdapter to provide the suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cityNames);

// Get the AutoCompleteTextView and set the adapter

        autoCompleteTextView.setAdapter(adapter);

// Set the minimum number of characters required to show suggestions
        autoCompleteTextView.setThreshold(1);

// Set a listener to handle the user's selection of a suggestion

        //One click all dismiss:----------
        autoCompleteTextView.setOnClickListener(v -> {

            binding.spinnerCaseText1.dismiss();
            binding.spinnerSubcat.dismiss();
            binding.stationLayout.cvStation.setVisibility(View.GONE);

        });

        //Click listener for drawableleft in EditText in Android? [duplicate]:-
        autoCompleteTextView.setOnTouchListener(new DrawableClickListener(2) {

            @Override
            public boolean onDrawableClick() {
                Log.e(TAG, "Please click on Image Icon");
                performSearch();
                return false;
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = (String) parent.getItemAtPosition(position);
                // Handle the selected city here
                EditText editText = binding.getRoot().findViewById(R.id.edit_search);

                editText.setText(selectedCity);
                performSearch();


            }
        });

        AutoCompleteTextView editText = (AutoCompleteTextView) binding.getRoot().findViewById(R.id.edit_search);
        // Clear the text when hitting the API
        editText.setText("");
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Handle search action here
                    performSearch();
                    return true;

                }
                return false;
            }
        });


        binding.placesGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (binding.placesGroup.getCheckedChipId() == 99) {
                    binding.spinnerSubcat.selectItemByIndex(0);
                    catid = "1";
                    getStationsData();
                } else {
                    Log.e(TAG, "onCheckedChanged: " + binding.placesGroup.getCheckedChipId());
                }

                //Search search Chipper hide:---
                binding.spinnerSubcat.dismiss();
                binding.spinnerCaseText1.dismiss();
                binding.stationLayout.cvStation.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                getStationsData();

            }
        });

        setSetting();

        return binding.getRoot();
    }


    public boolean isPushNotificationEnabled() {
        SharedPreferences preferences = requireContext().getSharedPreferences("app_preferences", MODE_PRIVATE);
//        SharedPrefs preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
//        return preferences.getBoolean("push_notification_enabled", true);
        return preferences.getBoolean("allow_push", false);
    }


    private void performSearch() {
        isSearching = true;
        AutoCompleteTextView searchEditText = (AutoCompleteTextView) binding.getRoot().findViewById(R.id.edit_search);
        String searchString = searchEditText.getText().toString();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocationName(searchString, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                LatLng latLng = new LatLng(lat, lng);

                searchCityLat = lat;
                searchCityLang = lng;
                getStationsData();

//                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                binding.spinnerSubcat.selectItemByIndex(0);

//                getStationsList();
            } else {

                // No results found

//                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                getStationsData();
//                getStationsList();
                setUpClusterer();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setUpClusterer() {
        // Position the map.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
        //Sydney
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.8688197, 151.2092955), 10));


        clusterManager = new ClusterManager<MyItem>(context, map);

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

// Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;
        double lat = -33.8688197;
        double lng = 151.2092955;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }
    }
//    });


    private void getCategories() {

        CommonUtils.showLoading(getActivity(), "Please Wait", false);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("table_name", "category");
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));

        Call<CatResponse> call = apiInterface.getDefaultData(jsonObject);
        call.enqueue(new Callback<CatResponse>() {
            @Override
            public void onResponse(Call<CatResponse> call, Response<CatResponse> response) {
                try {

                    CommonUtils.hideLoading();
                    catList = response.body().getData();

                    ArrayList<String> spinnerItems = new ArrayList<>();

                    for (CategoryModel c : response.body().getData()) {
                        spinnerItems.add(c.getName());

                    }

//                    Spinner:----
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    binding.spinnerCaseText1.setAdapter(adapter);

                    //PowerSpinner:-----


                    binding.spinnerCaseText1.setItems(spinnerItems);

                    binding.spinnerCaseText1.selectItemByIndex(0);

                    //12-06-2023


                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("table_name", "subcategory");
                    jsonObject.addProperty("reference_id", "category_id");

                    jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
                    jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));

                    getsubCategories(jsonObject);

                    binding.spinnerCaseText1.getSelectedIndex();

                    if (!binding.spinnerCaseText1.getText().equals("Fuel")) {
                        fuelid = response.body().getData().get(binding.spinnerCaseText1.getSelectedIndex() - 1).getId();

                        getStationsData();

                    }


//                    binding.spinnerCaseText1.setVisibility(View.VISIBLE);

//                    if (binding.spinnerCaseText1.getVisibility()==View.VISIBLE&&spinner_caseText1.getSelectedItem()==null){
//                        Toast.makeText(requireContext(), "Select Category", Toast.LENGTH_SHORT).show();
//                        return false;
//                    }

                    //category show sub category dismise
                    binding.spinnerCaseText1.setOnClickListener(v -> {
                        binding.spinnerSubcat.dismiss();
                        binding.spinnerCaseText1.showOrDismiss();
                        binding.stationLayout.cvStation.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        View focusedView = requireActivity().getCurrentFocus();
                        if (focusedView != null) {
                            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                        }
                    });

                    binding.spinnerCaseText1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                        @Override
                        public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {

                            JsonObject jsonObject = new JsonObject();

                            jsonObject.addProperty("table_name", "subcategory");
                            jsonObject.addProperty("reference_id", "category_id");

                            jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
                            jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));

//                            12-06-2023 Category Default
                            //getsubCategories(jsonObject);

//                            binding.spinnerCaseText1.getSelectedIndex();
                            //06-06-2023
                            binding.spinnerSubcat.dismiss();
                            if (!newItem.equals("Fuel")) {
                                fuelid = response.body().getData().get(binding.spinnerCaseText1.getSelectedIndex() - 1).getId();

                                getStationsData();
                            }

                        }


                    });

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<CatResponse> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });
    }


    private void getsubCategories(JsonObject jsonObject) {
        CommonUtils.showLoading(getActivity(), "Please Wait", false);

        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);

        Call<CatResponse> call = apiInterface.getDefaultData(jsonObject);
        call.enqueue(new Callback<CatResponse>() {
            @Override
            public void onResponse(Call<CatResponse> call, Response<CatResponse> response) {
                try {
                    com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);

                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                    CommonUtils.hideLoading();
                    subcatList = response.body().getData();
                    ArrayList<String> spinnerItems = new ArrayList<>();

//                    spinnerItems.add("Select");
                    spinnerItems.add("All Fuels");

                    Chip chip1 = new Chip(requireContext());
                    chip1.setText("All Fuels");
                    chip1.setGravity(Gravity.CENTER_HORIZONTAL);
                    chip1.setTextColor(getResources().getColor(R.color.chip_textclr));

                    chip1.setChipStrokeColorResource(R.color.chip_textclr);


                    chip1.setChipBackgroundColorResource(R.color.chip_bg);

                    chip1.setId(99);

                    chip1.setCheckable(true);
                    chip1.setCheckedIconVisible(false);
                    binding.placesGroup.addView(chip1);

                    for (CategoryModel c : response.body().getData()) {
                        spinnerItems.add(c.getName());
                        Chip chip = new Chip(requireContext());
                        chip.setText(c.getName());
                        chip.setGravity(Gravity.CENTER_HORIZONTAL);
                        chip.setId(response.body().getData().indexOf(c));
//                        chip.setPadding(8, 8, 8, 8);
//                        chip.setTextColor(getResources().getColor(white));
                        chip.setTextColor(getResources().getColor(R.color.chip_textclr));
                        chip.setChipStrokeColorResource(R.color.chip_textclr);
                        chip.setChipBackgroundColorResource(R.color.chip_bg);
                        chip.setCheckable(true);
                        chip.setCheckedIconVisible(false);

//                        builder.addFormDataPart("fcm_token", SharedPrefs.getInstance(this).getString(Constants.FCM_TOKEN))
                        builder.addFormDataPart("token", SharedPrefs.getInstance(getApplicationContext()).getString(Constants.TOKEN))
//                                .addFormDataPart("city", binding.spinnerCity.getSelectedItem().toString())
                                .addFormDataPart("id", SharedPrefs.getInstance(getApplicationContext()).getString(Constants.ID)).addFormDataPart("user_id", SharedPrefs.getInstance(getApplicationContext()).getString(Constants.USER_ID))
//                                .addFormDataPart("city", binding.spinnerSubcat.getSelectedItem().toString())
//                                .addFormDataPart("subcategory", binding.spinnerSubcat.getSelectedItem().toString())
//                                .addFormDataPart("subcategory", binding.placesGroup.getSelectedItem().toString())

//                        jsonObject.addProperty("reference_id", "category_id");
                                .addFormDataPart("reference_id", SharedPrefs.getInstance(getApplicationContext()).getString(Constants.REFERENCE_ID)).build();


                        binding.placesGroup.addView(chip);
//                        binding.spinnerSubcat.addView(chip);

                    }

                    //Ali
                    binding.spinnerSubcat.setItems(spinnerItems);

                    //By Default Selected not E10 every time Previous O/p
                    binding.spinnerSubcat.selectItemByIndex(0);


                    //Category click Subcategory dismise
                    binding.spinnerSubcat.setOnClickListener(v -> {
                        binding.spinnerCaseText1.dismiss();
                        binding.spinnerSubcat.showOrDismiss();
                        binding.stationLayout.cvStation.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        View focusedView = requireActivity().getCurrentFocus();
                        if (focusedView != null) {
                            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                        }
                    });

                    binding.spinnerSubcat.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                        @Override
                        public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
//
                            binding.spinnerSubcat.getSelectedIndex();
                            binding.spinnerCaseText1.dismiss();

                            if (!newItem.equals("All Fuels")) {

                                catid = response.body().getData().get(binding.spinnerSubcat.getSelectedIndex() - 1).getId();
                                getStationsData();

                                binding.placesGroup.check(binding.spinnerSubcat.getSelectedIndex() - 1);

                            } else {

                                binding.placesGroup.check(99);

                                getStationsData();
                            }
                        }
                    });

                    binding.placesGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                        @Override
                        public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                            if (binding.placesGroup.getCheckedChipId() == 99) {
                                binding.spinnerSubcat.selectItemByIndex(0);
                                catid = "1";
                                getStationsData();
                                return;
                            }
                            binding.spinnerSubcat.selectItemByIndex(binding.placesGroup.getCheckedChipId() + 1);

                            if (!binding.spinnerSubcat.getText().toString().equals("All Fuels")) {
                                catid = response.body().getData().get(binding.spinnerSubcat.getSelectedIndex() - 1).getId();
                                getStationsData();

                                //03-05-2023 Changes
//                                if (stationDataList == null || stationDataList.isEmpty())
//                                    Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();


                            } else {
                                getStationsData();
                            }
                            getStationsData();
                        }
                    });


                    //05-04-2023
//                    binding.placesGroup.check(0);
//                    binding.spinnerSubcat.check(0);
//                    binding.spinnerSubcat.setItems(spinnerItems);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CatResponse> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });
    }


    boolean taost = true;

    private void getStationsData() {


        if (taost) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("latitude", searchCityLat);
            jsonObject.addProperty("longitude", searchCityLang);

            //23-03-2023
            jsonObject.addProperty("distance", "30");

            jsonObject.addProperty("sort", "distance");

            jsonObject.addProperty("order", "asc");

            try {
                jsonObject.addProperty("category", fuelid);
            } catch (Exception e) {
                jsonObject.addProperty("category", "1");

            }

            if (!binding.spinnerSubcat.getText().equals("All Fuels"))
                jsonObject.addProperty("subcategory", catid);

            jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
            jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));

            Log.e(TAG, jsonObject.toString());

            com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);

            Call<StationDataResponse> call = apiInterface.getStationsData(jsonObject);

            call.enqueue(new Callback<StationDataResponse>() {

                @Override
                public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {
                    try {
                        CommonUtils.hideLoading();

                        if (response.isSuccessful() && response.body() != null) {
                            stationDataList = response.body().getData();

                            if (!stationDataList.isEmpty()) {
                                StationDataResponse.StationDataModel dataResponse = stationDataList.get(0); // Access the first element or use a valid index based on your requirements

                                ArrayList<DefaultDataModel> defaultDataModels = new ArrayList<>();

                                for (DefaultDataModel defaultData : dataResponse.getDefault_data()) {
                                    DefaultDataModel defaultDataModel = new DefaultDataModel();
                                    defaultDataModel.setType(defaultData.getType());
                                    defaultDataModel.setAmount(defaultData.getAmount());
                                    defaultDataModels.add(defaultDataModel);
                                }

                                mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter(requireContext(), defaultDataModels));

                                binding.spinnerSubcat.dismiss();
                                binding.spinnerCaseText1.dismiss();
                                binding.stationLayout.cvStation.setVisibility(View.GONE);

                                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                View focusedView = requireActivity().getCurrentFocus();
                                if (focusedView != null) {
                                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                                }

                                for (StationDataResponse.StationDataModel stationDataModel : stationDataList) {
                                    addMarker(stationDataModel);
                                }

                                isSearching = false;
                            } else {

                                mGoogleMap.clear();
                                Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            // Handle unsuccessful response
                            Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle other exceptions
                    }
                }

                @Override
                public void onFailure(Call<StationDataResponse> call, Throwable t) {

                    CommonUtils.hideLoading();

                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                    Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();


                }
            });
        }

        taost = false;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                taost = true;

                isSearching = false;
            }
        }, 1000);


    }

    public void getStationsList(String sortby, String order) {

//        addPhotoBottomDialogFragment.dismissAllowingStateLoss();

        CommonUtils.showLoading(getActivity(), "Please Wait", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("latitude", searchCityLat);
        jsonObject.addProperty("longitude", searchCityLang);

        jsonObject.addProperty("distance", distance);


        jsonObject.addProperty("sort", sortby);

        jsonObject.addProperty("order", order);

        jsonObject.addProperty("category", fuelid);


        if (!binding.spinnerSubcat.getText().equals("All Fuels"))
            jsonObject.addProperty("subcategory", catid);

        jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));


        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);


        Call<StationDataResponse> call = apiInterface.getStationsData(jsonObject);

        call.enqueue(new Callback<StationDataResponse>() {

            @Override
            public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {
                try {
                    CommonUtils.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        stationDataList = response.body().getData();

                        if (!stationDataList.isEmpty()) {
                            StationDataResponse.StationDataModel dataResponse = stationDataList.get(0); // Access the first element or use a valid index based on your requirements

                            ArrayList<DefaultDataModel> defaultDataModels = new ArrayList<>();

                            for (DefaultDataModel defaultData : dataResponse.getDefault_data()) {
                                DefaultDataModel defaultDataModel = new DefaultDataModel();
                                defaultDataModel.setType(defaultData.getType());
                                defaultDataModel.setAmount(defaultData.getAmount());
                                defaultDataModels.add(defaultDataModel);
                            }

                            mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter(requireContext(), defaultDataModels));

                            binding.spinnerSubcat.dismiss();
                            binding.spinnerCaseText1.dismiss();
                            binding.stationLayout.cvStation.setVisibility(View.GONE);

                            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            View focusedView = requireActivity().getCurrentFocus();
                            if (focusedView != null) {
                                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                            }

                            for (StationDataResponse.StationDataModel stationDataModel : stationDataList) {
                                addMarker(stationDataModel);
                            }
                            showBottomSheet();
                            isSearching = false;
                        } else {

                            mGoogleMap.clear();
                            Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle other exceptions
                }
            }

            @Override
            public void onFailure(Call<StationDataResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    //-----------------


    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        //homeMap
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationChangeListener(location -> {

            myLocation = location;

        });


    }

    private void getMyLocationNow() {
//    public static void getMyLocationNow() {

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // Zoom the camera to the current location
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                            // 15 is the zoom level
                        } else {
                            Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {

            ActivityCompat.requestPermissions(HomeFragmentGasaver.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }


//    public static void getMyLocationNow() {
//        // Check if location permission is granted
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            // Get the last known location
//            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(location -> {
//                        if (location != null) {
//                            // Zoom the camera to the current location
//                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
//                            // 15 is the zoom level
//                        } else {
//                            Toast.makeText(context, "Unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            // Request location permission
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        }
//    }


//    ------------------------------------------


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setUpRecyclerView();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getApplicationContext());

        getCategories();
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        mGoogleMap.setOnMapClickListener(v -> {
            binding.spinnerSubcat.dismiss();
            binding.spinnerCaseText1.dismiss();

            binding.stationLayout.cvStation.setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = requireActivity().getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }

        });

//        -----------------------

        //FindRoutesTechMirrors

        //homeMap
        mMap = googleMap;

        if (locationPermission) {
            getMyLocation();
        }


        if (appPermissions.isLocationOk(requireContext())) {
            isLocationPermissionOk = true;

            setUpGoogleMap();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext()).setTitle("Location Permission").setMessage("Near me required location permission to show you near by places").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestLocation();
                }
            }).create().show();
        } else {
            requestLocation();
        }

    }


//    -----------------------------------------------------------------------------------------------------


    private void requestLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstant.LOCATION_REQUEST_CODE);
    }


    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this::onMarkerClick);

        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Code to be executed after 5 seconds
                mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    //Zoom in Api not call, No Loading logos not hide,
                    //Zoom out Api call Refresh, Re loading
                    float oldZoom = mGoogleMap.getCameraPosition().zoom;
                    private float currentZoom = -1;
                    private final float ZOOM_THRESHOLD = 0.5f; // Set your desired threshold value here

                    @Override
                    public void onCameraChange(CameraPosition pos) {
                        if (pos.zoom != currentZoom) {
                            currentZoom = pos.zoom;

                            if (oldZoom > currentZoom && !isSearching) {
                                // The user has zoomed out
                                if (oldZoom - currentZoom >= ZOOM_THRESHOLD)
                                    oldZoom = currentZoom;
                                // Rest of your code here
                                LatLng cameraPosition = pos.target;

                                // Create a geocoder instance
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                                try {
                                    // Get the address from the camera position
                                    List<Address> addresses = geocoder.getFromLocation(cameraPosition.latitude, cameraPosition.longitude, 1);

                                    if (addresses.size() > 0) {


                                        // Get the radius in kilometers from the current zoom level
                                        double radiusInKm = 15554.03392 * Math.cos(cameraPosition.latitude * Math.PI / 180) / Math.pow(2, currentZoom);


                                        searchCityLat = cameraPosition.latitude;
                                        searchCityLang = cameraPosition.longitude;
                                        int radiusInKmInt = (int) radiusInKm;
                                        distance = String.valueOf(radiusInKmInt);
                                        getStationsData();


                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (oldZoom < currentZoom) {
                                // The user has zoomed in too much
                                if (currentZoom - oldZoom >= ZOOM_THRESHOLD) {
                                    oldZoom = currentZoom;
                                }
                            }
                        }
                    }
                });
            }
        }, 5000); // 5000 milliseconds = 5 seconds


    }


    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

//                getCurrentLocation();
//                getMyLocation();
//                getMyLocationNow();
//                setUpClusterer();
//                stopLocationUpdate();
//                setUpLocationUpdate();


                if (task.isSuccessful()) {
//                            Toast.makeText(requireContext(), "Location updated started", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

//    private void getCurrentLocation() {

//    public static void getCurrentLocation(Context context, GoogleMap googleMap) {

                public void getCurrentLocation() {

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.maps_key));
//        placesClient = Places.createClient(getApplicationContext());

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                Log.e(TAG, "onSuccess: " + location);
                try {
                    searchCityLat = location.getLatitude();
                    searchCityLang = location.getLongitude();
                    LatLng latLng = new LatLng(searchCityLat, searchCityLang);

                    //    //Home location Distance is showing this 1 line of code
                    WishListActivity.cuLocation = location;

                    //10f Hyderabad ORR shows in google map
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f));

                    //15f Amirpet shows currect location in Hyderabad
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                    getStationsData();
                } catch (Exception e) {
                    Log.e(TAG, "onSuccess: " + e);
                }

            }
        });
    }


    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: Location Update stop");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null) stopLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.spinnerSubcat.dismiss();

        // Assuming you have an EditText or SearchView for your search input
//        EditText searchEditText = requireView().findViewById(R.id.edit_search);
        AutoCompleteTextView searchEditText = requireView().findViewById(R.id.edit_search);

        // Clear the text when the fragment is resumed
        // Clear the text when hitting the API
        searchEditText.setText("");

        if (fusedLocationProviderClient != null) {

            startLocationUpdates();

            //Refresh when i'm Comming back frome MainPickmanActivity to HomeFragmentGasaver
            getStationsData();


        }

    }


    //    private void initMarker(ArrayList<StationDataResponse.StationDataModel> stationDataList) {
    private void addMarker(StationDataResponse.StationDataModel stationDataModel) {

        double smalVal = Double.parseDouble(stationDataModel.getPrices().get(0).getAmount());
        for (StationDataResponse.PriceModel priceModel : stationDataModel.getPrices()) {
            Double am = Double.valueOf(priceModel.getAmount());
            if (smalVal > am)
                smalVal = am;
        }
        LatLng latLng = new LatLng(Double.parseDouble(stationDataModel.getLatitude()), Double.parseDouble(stationDataModel.getLongitude()));

        //Red /Blue
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(stationDataModel.getBrand() + " Price:" + smalVal).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        Marker mapmarker = mGoogleMap.addMarker(markerOptions);
        mapmarker.setSnippet(new Gson().toJson(stationDataModel)); // 10 15 5
        mapmarker.setTag(stationDataModel.getStationid()); // 10 15 5
        loadMarkerIcon(mapmarker, stationDataModel.getBrandIcon());

        //Direct Go with out Select
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        mapmarker.showInfoWindow();

    }

    private void loadMarkerIcon(final Marker marker, String BrandIcon) {
        try {


            Glide.with(requireContext()).load(BrandIcon).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    int height = 80;
                    int width = 80;
                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                    Bitmap customSizeMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                    try {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(customSizeMarker));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {

        }
    }

    //Water Mark image


    //GoogleMapMyApplication1

    //Search Scroll RecyclerView
    private void setUpRecyclerView() {

        binding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.placesRecyclerView.setHasFixedSize(false);
//        googlePlaceAdapter = new GooglePlaceAdapter(this);
//        binding.placesRecyclerView.setAdapter(googlePlaceAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(binding.placesRecyclerView);

        binding.placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position > -1) {
                    GooglePlaceModel googlePlaceModel = googlePlaceModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(), googlePlaceModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        String markerTag = (String) marker.getTag();
        Log.d("TAG", "onMarkerClick: " + markerTag);

        for (StationDataResponse.StationDataModel stationDataModel : stationDataList) {
//        for (WishlistResponse.WishlistModel wishlistModel : WishlistList) {

            if (stationDataModel.getStationid().equals(markerTag)) {
//            if (wishlistModel.getStationid().equals(markerTag)) {

                binding.stationLayout.cvStation.setVisibility(View.VISIBLE);


                binding.spinnerSubcat.dismiss();
                binding.spinnerCaseText1.dismiss();
//                binding.stationLayout.cvStation.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                binding.stationLayout.ivWishlist.setImageResource(stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("yes") ? R.drawable.wishlist_added : R.drawable.like_icon);

                binding.stationLayout.ivShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String sAux = "\nJust clicked & install this application:\n\n";
//
//                    sAux = sAux + "https://play.google.com/store/apps/details?id=org.halalscan.jss\n\n";
                            sAux = sAux + " https://play.google.com/store/apps/details?id=com.pineconesoft.petrolspy&pli=1\n\n";
                            i.putExtra(Intent.EXTRA_TEXT, sAux);
                            startActivity(Intent.createChooser(i, "Choose One"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                binding.stationLayout.txtPlaceName.setText(stationDataModel.getName());
                binding.stationLayout.txtPlaceName.setSelected(true);


                Glide.with(getActivity()).load(stationDataModel.getBrandIcon()).into(binding.stationLayout.ivProjImg);

//                Glide.with(getActivity()).load(stationDataModel.getBrandIcon()).into(binding.stationLayout.imgBrand);

                binding.stationLayout.txtPlaceAddress.setText(stationDataModel.getAddress());

                //Marque code
                binding.stationLayout.txtPlaceAddress.setSelected(true);


                String latesttDTE = null;

                try {
                    binding.stationLayout.txtPrices.setText("");

                    for (StationDataResponse.PriceModel priceModel : stationDataModel.getPrices()) {

//                        binding.stationLayout.txtPrices.append(priceModel.getType() + ": $ " + priceModel.getAmount() + "\n");

                        String priceString = priceModel.getType() + ": $ " + priceModel.getAmount() + "\n";

                        // Generate a random color
                        Random random = new Random();
                        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

                        // Create a SpannableString to set the color for the priceModel.getType() section
                        SpannableString spannableString = new SpannableString(priceString);
                        int start = priceString.indexOf(priceModel.getType());
//                        int end = start + priceModel.getType().length();
                        int end = priceString.length();
                        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        // Set the modified SpannableString on the TextView
//                        holder.txtPrices.append(spannableString);
                        binding.stationLayout.txtPrices.append(spannableString);

                        if (latesttDTE == null)
                            latesttDTE = priceModel.getLastupdated();
                        else if (CommonUtils.getDate(latesttDTE).getTime() >= (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
                            latesttDTE = priceModel.getLastupdated();
                        }
                    }
                    binding.stationLayout.txtLastUpdated.setText("Last Updated: ");
                    binding.stationLayout.txtLastUpdated.append(latesttDTE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Log.e(TAG, "onSuccess: " + location );


                try {
                    double distance = SphericalUtil.computeDistanceBetween(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker.getPosition());

                    if (distance > 1000) {
                        double kilometers = distance / 1000;
                        binding.stationLayout.txtDis.setText(CommonUtils.roundUpDecimal(kilometers, 2) + " KM");

                        Log.e(TAG, "kilometers: " + kilometers);

                    } else {
                        binding.stationLayout.txtDis.setText(CommonUtils.roundUpDecimal(distance, 2) + " Meters");

                    }

                    float speed = currentLocation.getSpeed();

                    if (speed > 0) {
                        double time = distance / speed;
                        binding.stationLayout.txtTime.setText(CommonUtils.roundUpDecimal(time, 2) + " sec");

                        Log.e(TAG, "time: " + time);
                    } else {
                        binding.stationLayout.txtTime.setText("N/A");

                    }
//                    binding.stationLayout.txtDis.setText(String.valueOf(stationDataModel.getDistance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.stationLayout.btnNavigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        {

                            String uri = "http://maps.google.com/maps?saddr=" + searchCity + "," + searchCity + "&daddr=" + searchCityLat + "," + searchCityLang;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);

                            intent.putExtra("lat", marker.getPosition().latitude);
                            intent.putExtra("lng", marker.getPosition().longitude);

                            startActivity(intent);

                        }
                    }
                });


                //Priceing for all:---

//                binding.stationLayout.btnSubmitPrices.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent(getApplicationContext(), MainPickmanActivity.class);
//                        intent.putExtra("station_id", stationDataModel.getStationid());
////                        intent.putExtra("category", catList.get(binding.spinnerCaseText1.getSelectedItemPosition()).getId());
//                        intent.putExtra("category", fuelid);
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(0, 0);
//
//                    }
//                });

////(Austrailia) Out 100 Meter Toast Show Here only , if he fail to show MainPickman also show Camera Toast with in 100 Meter


                binding.stationLayout.btnSubmitPrices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                Making a Location object in Android with latitude and longitude values

                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(marker.getPosition().latitude);//your coords of course
                        targetLocation.setLongitude(marker.getPosition().longitude);
                        float distanceInMeters = currentLocation.distanceTo(targetLocation);
                        if (distanceInMeters < 100) {
                            Intent intent = new Intent(getApplicationContext(), MainPickmanActivity.class);
                            intent.putExtra("station_id", stationDataModel.getStationid());
//                        intent.putExtra("station_id", wishlistModel.getStationid());
//                        intent.putExtra("category", catList.get(binding.spinnerCaseText1.getSelectedItemPosition()).getId());
                            intent.putExtra("category", fuelid);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                        } else {
                            Toast.makeText(getApplicationContext(), "User must be in 100 meters radius from fuel station to submit prices", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                //This is Priceing for only One:---

//                binding.stationLayout.btnSubmitPrices.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (!binding.spinnerSubcat.getText().equals("All Fuels"))
//                        {
//                            Intent intent = new Intent(getApplicationContext(), MainPickmanActivity.class);
//                            intent.putExtra("station_id", stationDataModel.getStationid());
////                        intent.putExtra("station_id", wishlistModel.getStationid());
//
//                            intent.putExtra("Selected", binding.spinnerSubcat.getText().toString());
//                            intent.putExtra("id", binding.spinnerSubcat.getSelectedIndex()+1);
//
////                        intent.putExtra("category", catList.get(binding.spinnerCaseText1.getSelectedItemPosition()).getId());
//                            intent.putExtra("category", fuelid);
//
//                            Log.e(TAG, "Prices not updated" );
//
//                            startActivity(intent);
//                            getActivity().overridePendingTransition(0, 0);
//                        }
//                        else {
//
//                            Intent intent = new Intent(getApplicationContext(), MainPickmanActivity.class);
//                            intent.putExtra("station_id", stationDataModel.getStationid());
////                        intent.putExtra("station_id", wishlistModel.getStationid());
////                        intent.putExtra("category", catList.get(binding.spinnerCaseText1.getSelectedItemPosition()).getId());
//                            intent.putExtra("category", fuelid);
//
//                            startActivity(intent);
//                            getActivity().overridePendingTransition(0, 0);
//                        }
//
//
//                    }
//                });

                binding.stationLayout.ivWishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveWishlist(stationDataModel, stationDataModel.getId(), stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes");

//                        saveWishlist(wishlistModel, wishlistModel.getId(), stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes");
//                        saveWishlist(wishlistModel, wishlistModel.getId(), wishlistModel.getWishlist() != null && wishlistModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes");


                    }
                });
                binding.stationLayout.ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.stationLayout.cvStation.setVisibility(View.GONE);
                    }
                });


//                Glide.with(getApplicationContext()).load(Constants.LOGO_IMG_URL + stationDataList.get(position).getBrandIcon()).into(holder.iv_proj_img);

//                Glide.with(getApplicationContext()).load(Constants.LOGO_IMG_URL + stationLayout.get(position).getBrandIcon()).into(holder.iv_proj_img);


            }
        }

//        binding.placesRecyclerView.scrollToPosition(markerTag);
        return false;
    }

    private void saveWishlist(StationDataResponse.StationDataModel stationDataModel, Integer stationid, String iswishlist) {
//private void saveWishlist(WishlistResponse.WishlistModel wishlistModel, Integer stationid, String iswishlist) {

//        CommonUtils.showLoading(getActivity(), "Please Wait", false);
        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));
        jsonObject.addProperty("vendor_id", stationid);
        jsonObject.addProperty("wishlist", iswishlist);

        Call<StationDataResponse> call = apiInterface.saveWishlist(jsonObject);
//    Call<WishlistResponse> call = apiInterface.saveWishlist(jsonObject);

        call.enqueue(new Callback<StationDataResponse>() {
//    call.enqueue(new Callback<WishlistResponse>() {

            @Override
            public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {
//        public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {

                try {
                    CommonUtils.hideLoading();
                    if (response.body().isStatus_code()) {

                        stationDataModel.setWishlist(iswishlist);
//                        wishlistModel.setWishlist(iswishlist);

                        binding.stationLayout.ivWishlist.setImageResource(iswishlist.equalsIgnoreCase("yes") ? R.drawable.wishlist_added : R.drawable.like_icon);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<StationDataResponse> call, Throwable t) {
//            public void onFailure(Call<WishlistResponse> call, Throwable t) {

                CommonUtils.hideLoading();
            }
        });


    }

    @Override
    public void onSaveClick(GooglePlaceModel googlePlaceModel) {

        if (userSavedLocationId.contains(googlePlaceModel.getPlaceId())) {
            new AlertDialog.Builder(requireContext()).setTitle("Remove Place").setMessage("Are you sure to remove this place?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removePlace(googlePlaceModel);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else {
            loadingDialog.startLoading();

            locationReference.child(googlePlaceModel.getPlaceId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {

                        SavedPlaceModel savedPlaceModel = new SavedPlaceModel(googlePlaceModel.getName(), googlePlaceModel.getVicinity(), googlePlaceModel.getPlaceId(), googlePlaceModel.getRating(), googlePlaceModel.getUserRatingsTotal(), googlePlaceModel.getGeometry().getLocation().getLat(), googlePlaceModel.getGeometry().getLocation().getLng());

                        saveLocation(savedPlaceModel);
                    }

                    saveUserLocation(googlePlaceModel.getPlaceId());

                    int index = googlePlaceModelList.indexOf(googlePlaceModel);
                    googlePlaceModelList.get(index).setSaved(true);
//                    googlePlaceAdapter.notifyDataSetChanged();
                    loadingDialog.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void removePlace(GooglePlaceModel googlePlaceModel) {

        userSavedLocationId.remove(googlePlaceModel.getPlaceId());
        int index = googlePlaceModelList.indexOf(googlePlaceModel);
        googlePlaceModelList.get(index).setSaved(false);
//        googlePlaceAdapter.notifyDataSetChanged();

        Snackbar.make(binding.getRoot(), "Place removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSavedLocationId.add(googlePlaceModel.getPlaceId());
                googlePlaceModelList.get(index).setSaved(true);
//                googlePlaceAdapter.notifyDataSetChanged();

            }
        }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                userLocationReference.setValue(userSavedLocationId);
            }
        }).show();

    }

    private void saveUserLocation(String placeId) {

        userSavedLocationId.add(placeId);
        userLocationReference.setValue(userSavedLocationId);
        Snackbar.make(binding.getRoot(), "Place Saved", Snackbar.LENGTH_LONG).show();
    }

    private void saveLocation(SavedPlaceModel savedPlaceModel) {
        locationReference.child(savedPlaceModel.getPlaceId()).setValue(savedPlaceModel);
    }

    @Override
    public void onDirectionClick(GooglePlaceModel googlePlaceModel) {

        String placeId = googlePlaceModel.getPlaceId();
        Double lat = googlePlaceModel.getGeometry().getLocation().getLat();
        Double lng = googlePlaceModel.getGeometry().getLocation().getLng();

//        Intent intent = new Intent(requireContext(), DirectionActivity.class);
//        intent.putExtra("placeId", placeId);
//        intent.putExtra("lat", lat);
//        intent.putExtra("lng", lng);
//
//        startActivity(intent);

    }

    //---------------------------------

//    private void refreshBottomSheetLayout() {
//        binding.llForm.postDelayed(() -> {
//            binding.llForm.smoothScrollTo(0, 0);
//        }, 200); // Adjust the delay time as needed
//    }


//    private void refreshBottomSheetLayout() {
//        binding.placesRecyclerView.postDelayed(() -> {
//            binding.placesRecyclerView.smoothScrollTo(0, 0);
//        }, 200); // Adjust the delay time as needed
//    }

    public void showBottomSheet() {

        addPhotoBottomDialogFragment = new FuelDistanceEmployeeListFragment(stationDataList, currentLocation, this.requireContext());

        addPhotoBottomDialogFragment.show(getParentFragmentManager(), "");

        binding.spinnerSubcat.dismiss();
        binding.spinnerCaseText1.dismiss();
        binding.stationLayout.cvStation.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = requireActivity().getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

//        getMyLocationNow();
//        getMyLocationNow(requireContext());

    }

    private void getPlaces(String placeName) {

        if (isLocationPermissionOk) {

            loadingDialog.startLoading();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius=" + radius + "&type=" + placeName + "&key=" + getResources().getString(R.string.API_KEY);

            if (currentLocation != null) {


                retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GoogleResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("TAG", "onResponse: " + res);
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {

                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();
                                    ArrayList<String> placenames = new ArrayList<>();
                                    for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {

//                                        if (userSavedLocationId.contains(response.body().getGooglePlaceModelList().get(i).getPlaceId())) {
//                                            response.body().getGooglePlaceModelList().get(i).setSaved(true);
//                                        }
                                        GooglePlaceModel place = response.body().getGooglePlaceModelList().get(i);
                                        googlePlaceModelList.add(place);
                                        placenames.add(place.getName());

                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, placenames);
                                    binding.edtPlaceName.setAdapter(adapter);
//                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);

                                }
                            }

                        } else {
                            Log.d("TAG", "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), "Error : " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }


                        loadingDialog.stopLoading();

                    }

                    @Override
                    public void onFailure(Call<GoogleResponseModel> call, Throwable t) {

                        Log.d("TAG", "onFailure: " + t);
                        loadingDialog.stopLoading();

                    }
                });
            }
        }

    }


    private class MarkerCallBack implements com.squareup.picasso.Callback {

        Marker marker = null;

        public MarkerCallBack(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onSuccess() {

            if (marker != null && marker.isInfoWindowShown()) {

                marker.hideInfoWindow();
                marker.showInfoWindow();

            }
        }

        @Override
        public void onError(Exception e) {

            Log.e(getClass().getSimpleName(), "Error Loading Thumbnail");
        }
    }


    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
