package com.gasaver.activity;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gasaver.R;
import com.gasaver.Response.BaseResponse;
import com.gasaver.Response.StationDataResponse;
import com.gasaver.Response.WishlistResponse;
import com.gasaver.databinding.ActivityUploadBinding;
import com.gasaver.databinding.ActivityWishlistBinding;
import com.gasaver.interfaces.DilogClickListener;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListActivity extends AppCompatActivity {

    String fuelid = "1";


    private final String searchCity = "Sydney";

    private final double searchCityLat = -33.8688197;
    private final double searchCityLang = 151.2092955;

    ActivityWishlistBinding binding;


    //Home location Distance is showing this 1 line of code
    public static Location cuLocation;

    RecyclerView rv_my_responses;
    LinearLayout ll_no_data;
    int SELECTED_POS;
    private ArrayList<StationDataResponse.StationDataModel> wishList = new ArrayList<>();

    private WishlistAdapter wishListAdapter;


    LinearLayout loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWishlistBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.recyclerviewList.setLayoutManager(new LinearLayoutManager(WishListActivity.this));
        getWishlist();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        loading = findViewById(R.id.ll_no_data);

        loading.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("My WishList");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });


    }

    private void getWishlist() {
        CommonUtils.showLoading(this, "Please Wait", false);
        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(this).getString(Constants.TOKEN));


        Call<StationDataResponse> call = apiInterface.getWishlist(jsonObject);

        call.enqueue(new Callback<StationDataResponse>() {

            @Override
            public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {

                try {
                    CommonUtils.hideLoading();
                    wishList = response.body().getData();
                    binding.recyclerviewList.setAdapter(wishListAdapter = new WishlistAdapter(WishListActivity.this, wishList));
                    loading.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<StationDataResponse> call, Throwable t) {

                Toast.makeText(WishListActivity.this, "error", Toast.LENGTH_SHORT).show();
                CommonUtils.hideLoading();
                t.printStackTrace();
            }
        });


    }


    private void removeWishlist(StationDataResponse.StationDataModel stationDataModel) {
//    private void removeWishlist(WishlistResponse.WishlistModel wishlistModel) {

        CommonUtils.showLoading(WishListActivity.this, "Please Wait", false);
        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(WishListActivity.this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(WishListActivity.this).getString(Constants.TOKEN));

        //        jsonObject.addProperty("vendor_id", wishlistModel.getId());
//            jsonObject.addProperty("vendor_id", stationDataModel.getId());
        jsonObject.addProperty("vendor_id", stationDataModel.getVendorId());
//            jsonObject.addProperty("vendor_id", stationid);

        jsonObject.addProperty("wishlist", "No");
//        jsonObject.addProperty("wishlist", iswishlist);

        Call<StationDataResponse> call = apiInterface.saveWishlist(jsonObject);
//        Call<WishlistResponse> call = apiInterface.saveWishlist(jsonObject);

        call.enqueue(new Callback<StationDataResponse>() {
//        call.enqueue(new Callback<WishlistResponse>() {

            @Override
            public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {
//            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {

                try {
                    CommonUtils.hideLoading();
                    if (response.body().isStatus_code()) {
                        wishList.remove(stationDataModel);
//                        wishList.remove(wishlistModel);
                        wishListAdapter.notifyDataSetChanged();

//                        Glide.with(WishListActivity.this).load(stationDataModel.getBrandIcon()).into(binding.llBrandReq.imgBrand);
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


    public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

        List<StationDataResponse.StationDataModel> stationDataList = new ArrayList<>();
//        List<WishlistResponse.WishlistModel> stationDataList = new ArrayList<>();

//                ArrayList<MyResResponse.MyResponses> myResponses;

        Context context;


        public WishlistAdapter(Context context, List<StationDataResponse.StationDataModel> stationDataList) {
//        public WishlistAdapter(Context context, List<WishlistResponse.WishlistModel> stationDataList) {
            this.stationDataList = stationDataList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fueldistanceemployeelistlayout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            try {
                StationDataResponse.StationDataModel stationDataModel = stationDataList.get(position);
//                WishlistResponse.WishlistModel wishlistModel = stationDataList.get(position);


//                Glide.with(getApplicationContext()).load(stationDataModel.getBrandIcon()).into(holder.ivProjImg);


                //Vinni
//                Glide.with(WishListActivity.this).load(stationDataList.get(position).getBrandIcon()).into(holder.iv_proj_img);
//                stationDataList
                //Zaheer
                Glide.with(WishListActivity.this).load(stationDataModel.getBrandIcon()).into(holder.iv_proj_img);
//                Glide.with(WishListActivity.this).load(wishlistModel.getBrandIcon()).into(holder.iv_proj_img);


                holder.tv_name.setText(stationDataModel.getName());
//                holder.tv_name.setText(wishlistModel.getName());


//                binding.stationLayout.txtPlaceName.setText(stationDataModel.getName());

//                Glide.with(WishListActivity.this).load(stationDataModel.getBrandIcon()).into(holder.img_brand);

                holder.tv_city.setText(stationDataModel.getCity());
//                holder.tv_city.setText(wishlistModel.getCity());

                holder.tv_addr.setText(stationDataModel.getAddress());
//                holder.tv_addr.setText(wishlistModel.getAddress());


                holder.tv_city.setText(stationDataModel.getBrand());
//                holder.tv_city.setText(wishlistModel.getBrand());

//                holder.tv_lastupdated.setText(stationDataModel.getLastupdated());
//                holder.tv_lastupdated.setText(StationDataResponse.priceModel.getLastupdated());

                //                holder.tv_req_type.setText(myResponses.get(position).getReqType());
//                holder.tv_req_type.setText(StationDataResponse.priceModel.get(position).getLastupdated());


//                String latesttDTE = null;
//                try {
//                    holder.tv_price.setText("");
//                    for (StationDataResponse.PriceModel priceModel :
////                    for (WishlistResponse.WishlistModel.PriceModel priceModel :
//
//                            stationDataModel.getPrices()) {
////                            wishlistModel.getPrices()) {
//
//                        holder.tv_price.append(priceModel.getType() + ": " + priceModel.getAmount() + " | ");
//                        if (latesttDTE == null)
//                            latesttDTE = priceModel.getLastupdated();
////                        else if (CommonUtils.getDate(latesttDTE).getTime() < (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
//                        else if (CommonUtils.getDate(latesttDTE).getTime() < (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
//
//                            latesttDTE = priceModel.getLastupdated();
//                        }
//                    }
//                    holder.tv_lastupdated.append(latesttDTE);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //--------
//                String latesttDTE;
                String latesttDTE = null;

                try {
//                    holder.tv_price.setText("");
//                    holder.txtPrices.setText("");
                    holder.txtPrices.setText("");
//                    holder.txtPrices.append(stationDataList.get(position).getPrices().get(position).getAmount());
//                    for (StationDataResponse.PriceModel priceModel :
//                            stationDataModel.getPrices()) {
//
//                        String priceString = priceModel.getType() + ": " + priceModel.getAmount() + " | ";
//
//                        // Generate a random color
//                        Random random = new Random();
//                        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
//
//                        // Create a SpannableString to set the color for the priceModel.getType() section
//                        SpannableString spannableString = new SpannableString(priceString);
//                        int start = priceString.indexOf(priceModel.getType());
////                        int end = start + priceModel.getType().length();
//                        int end = priceString.length();
//                        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        // Set the modified SpannableString on the TextView
//                        holder.txtPrices.append(spannableString);
//
//
//                        latesttDTE = priceModel.getLastupdated();
//                        if (CommonUtils.getDate(latesttDTE).getTime() >= (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
//                            latesttDTE = priceModel.getLastupdated();
//                        }
//
//                    }

//------

                    for (StationDataResponse.PriceModel priceModel : stationDataModel.getPrices()) {

//                        binding.stationLayout.txtPrices.append(priceModel.getType() + ": $ " + priceModel.getAmount() + "\n");

//                        String priceString = priceModel.getType() + ": $ " + priceModel.getAmount() + "\n";
                        String priceString = priceModel.getType() + ": " + priceModel.getAmount() + " | ";


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
                        holder.txtPrices.append(spannableString);

                        if (latesttDTE == null) latesttDTE = priceModel.getLastupdated();
                        else if (CommonUtils.getDate(latesttDTE).getTime() >= (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
                            latesttDTE = priceModel.getLastupdated();
                        }
                    }


                    //1 Update is Come:---
                    holder.tv_lastupdated.setText("Date: ");
//                    holder.tv_lastupdated.setText("Last Updated: ");
                    //So Many Update are Came:----
                    holder.tv_lastupdated.append(stationDataModel.getDate());

                    //15-06-2023
                    holder.tv_dis.setText("Distance : ");

                    //14-06-2023
                    try {
                        double distance = SphericalUtil.computeDistanceBetween(new LatLng(cuLocation.getLatitude(), cuLocation.getLongitude()), new LatLng(Double.parseDouble(stationDataModel.getLatitude()), Double.parseDouble(stationDataModel.getLongitude())));

                        if (distance > 1000) {
                            double kilometers = distance / 1000;
                            Log.e(TAG, String.valueOf(kilometers));
                            holder.tv_dis.append(CommonUtils.roundUpDecimal(kilometers, 2) + " KM");
                        }



                        else {
                            holder.tv_dis.setText("N/A");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.btn_navigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        {

                            String uri = "http://maps.google.com/maps?saddr=" + searchCity + "," + searchCity + "&daddr=" + searchCityLat + "," + searchCityLang;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);

//                            intent.putExtra("lat", marker.getPosition().latitude);
//                            intent.putExtra("lng", marker.getPosition().longitude);
//
//                            startActivity(intent);

                        }
                    }
                });


//                holder.btn_navigate.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        {
//
//                            Intent intent = new Intent(WishListActivity.this, DirectionActivity.class);
////                            Intent intent = new Intent(WishListActivity.this, NavigationActivity.class);
//
//
//                            intent.putExtra("lat", stationDataModel.getLatitude());
//                            intent.putExtra("lng", stationDataModel.getLongitude());
//
////                            intent.putExtra("lat", wishlistModel.getLatitude());
////                            intent.putExtra("lng", wishlistModel.getLongitude());
//
//                            startActivity(intent);
//
//                        }
//                    }
//                });
                holder.btn_submit_prices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(WishListActivity.this, MainPickmanActivity.class);

                        intent.putExtra("station_id", stationDataModel.getStationid());
//                        intent.putExtra("station_id", wishlistModel.getStationid());

//                        intent.putExtra("category", "3");
                        intent.putExtra("category", fuelid);

                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    }
                });
                holder.ll_delete_my_prop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(WishListActivity.this);
                        builder.setTitle("Remove Wishlist");
                        builder.setMessage("Are you sure you want to remove this from Wishlist?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                removeWishlist(stationDataModel);
//                                removeWishlist(wishlistModel);

                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                    }
                });

                holder.iv_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String sAux = "\nJust clicked & install this application:\n\n";
//
                            //https://houseofspiritshyd.in/gasaver/admin/

//                    sAux = sAux + "https://play.google.com/store/apps/details?id=org.halalscan.jss\n\n";
                            sAux = sAux + " https://play.google.com/store/apps/details?id=com.pineconesoft.petrolspy&pli=1\n\n";
                            i.putExtra(Intent.EXTRA_TEXT, sAux);
                            startActivity(Intent.createChooser(i, "Choose One"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


//                iv_wishlist1

                holder.iv_wishlist1.setImageResource(R.drawable.wishlist_added);
                holder.iv_wishlist1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(WishListActivity.this);
                        builder.setTitle("Remove Wishlist");
                        builder.setMessage("Are you sure you want to remove this from Wishlist?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                removeWishlist(stationDataModel);
//                                removeWishlist(wishlistModel);


//                                binding.stationLayout.ivWishlist.setImageResource(iswishlist.equalsIgnoreCase("yes") ? R.drawable.wishlist_added : R.drawable.like_icon);
//                                holder.iv_wishlist1.setImageResource("Yes".equalsIgnoreCase("No") ? R.drawable.wishlist_added : R.drawable.like_icon);

                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                    }
                });


                holder.iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        holder.cv_station.setVisibility(View.GONE);
                    }
                });

//                binding.cvStation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        binding.cvStation.setVisibility(View.GONE);
//                    }
//                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return stationDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_proj_img, img_brand, ivWishlist, iv_wishlist, iv_wishlist1, iv_close, iv_share;
            TextView tv_name, tv_addr, tv_city, tv_price, txtPrices, tv_lastupdated, tv_dis, tv_contact_my_res, txtLastUpdated;
            LinearLayout layoutid;
            AppCompatButton btn_submit_prices, btn_navigate;
            LinearLayout ll_delete_my_prop, cv_station;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ll_delete_my_prop = itemView.findViewById(R.id.ll_delete_my_prop);
                btn_submit_prices = itemView.findViewById(R.id.btn_submit_prices);
                btn_navigate = itemView.findViewById(R.id.btn_navigate);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_dis = itemView.findViewById(R.id.txtDis);

//                tv_lastupdated = itemView.findViewById(R.id.tv_lastupdated);
                tv_lastupdated = itemView.findViewById(R.id.txtLastUpdated);

                txtPrices = itemView.findViewById(R.id.txtPrices);

//                tv_price = itemView.findViewById(R.id.tv_price);
                tv_addr = itemView.findViewById(R.id.tv_addr);
                tv_city = itemView.findViewById(R.id.tvcity);
                layoutid = itemView.findViewById(R.id.layoutid);

                iv_proj_img = itemView.findViewById(R.id.iv_proj_img);
                iv_wishlist = itemView.findViewById(R.id.iv_wishlist);
                img_brand = itemView.findViewById(R.id.img_brand);

                iv_wishlist1 = itemView.findViewById(R.id.iv_wishlist1);
                iv_close = itemView.findViewById(R.id.iv_close);

                iv_share = itemView.findViewById(R.id.iv_share);

//                cv_station = itemView.findViewById(R.id.cv_station);

//                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//                RecyclerView mRecyclerView = itemView.findViewById(R.id.recyclerview_List);


            }
        }
    }

}