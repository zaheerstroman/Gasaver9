package com.gasaver.view;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.gasaver.fragment.HomeFragmentGasaver.asc;
import static com.gasaver.fragment.HomeFragmentGasaver.price_asc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gasaver.R;
import com.gasaver.Response.StationDataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.gasaver.activity.MainPickmanActivity;
import com.gasaver.databinding.ActivityFuelDistanceEmployeeListActivityityBinding;
import com.gasaver.fragment.HomeFragmentGasaver;
import com.gasaver.network.APIClient;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.google.maps.android.SphericalUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FuelDistanceEmployeeListFragment extends BottomSheetDialogFragment {

    String fuelid = "1";

    public Window window;

    private final String searchCity = "Sydney";

    private final double searchCityLat = -33.8688197;
    private final double searchCityLang = 151.2092955;

    ActivityFuelDistanceEmployeeListActivityityBinding binding;
    ArrayList<StationDataResponse.StationDataModel> stationDataList;


    Location cuLocation;

    Context context;

    public FuelDistanceEmployeeListFragment(ArrayList<StationDataResponse.StationDataModel> stationDataList, Location currentLocation, Context context) {
        this.stationDataList = stationDataList;
        this.cuLocation = currentLocation;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ActivityFuelDistanceEmployeeListActivityityBinding.inflate(getLayoutInflater());

        if (stationDataList == null || stationDataList.isEmpty()) {
            Toast.makeText(getActivity(), "no stations found", Toast.LENGTH_SHORT).show();
        } else
            binding.recyEmployeelist.setAdapter(new FuelDistanceEmployeeListAdapter(context, stationDataList));
        return binding.getRoot();
    }


    public class FuelDistanceEmployeeListAdapter extends RecyclerView.Adapter<FuelDistanceEmployeeListAdapter.ViewHolder> {

        List<StationDataResponse.StationDataModel> stationDataList = new ArrayList<>();

        Context context;


        public FuelDistanceEmployeeListAdapter(Context context, List<StationDataResponse.StationDataModel> stationDataList) {
            this.stationDataList = stationDataList;
            this.context = context;
        }

        @NonNull
        @Override
        public FuelDistanceEmployeeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fueldistanceemployeelistlayout, parent, false);
            return new FuelDistanceEmployeeListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FuelDistanceEmployeeListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            try {
                StationDataResponse.StationDataModel stationDataModel = stationDataList.get(position);
                holder.iv_wishlist1.setImageResource(stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("yes") ? R.drawable.wishlist_added : R.drawable.like_icon);
                holder.iv_wishlist1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        saveWishlist(stationDataModel, stationDataModel.getId(), stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes", holder.iv_wishlist1);
//                        saveWishlist(stationDataModel, stationDataModel.getId(), stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes", holder.iv_wishlist1, holder.ivShare);
                        saveWishlist(stationDataModel, stationDataModel.getId(), stationDataModel.getWishlist() != null && stationDataModel.getWishlist().equalsIgnoreCase("Yes") ? "No" : "Yes", holder.iv_wishlist1, holder.iv_share);


                    }
                });

                holder.ivShare.setOnClickListener(new View.OnClickListener() {
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


                List<StationDataResponse.PriceModel> prices = stationDataModel.getPrices();
                Glide.with(getActivity()).load(stationDataList.get(position).getBrandIcon()).into(holder.iv_proj_img);

                holder.tv_name.setText(stationDataModel.getName());

                holder.tv_city.setText(stationDataModel.getCity());

                holder.tv_addr.setText(stationDataModel.getAddress().toLowerCase(Locale.ROOT));

                holder.tv_addr.setText(stationDataModel.getAddress());

                holder.tv_city.setText(stationDataModel.getBrand());

                String latesttDTE;
                try {
                    holder.txtPrices.setText("");

                    for (StationDataResponse.PriceModel priceModel :
                            stationDataModel.getPrices()) {

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
                        holder.txtPrices.append(spannableString);


                        latesttDTE = priceModel.getLastupdated();
                        if (CommonUtils.getDate(latesttDTE).getTime() >= (CommonUtils.getDate(priceModel.getLastupdated()).getTime())) {
                            latesttDTE = priceModel.getLastupdated();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                //Single Last Updated
                holder.txtLastUpdated.setText("Last Updated : ");

                //Double trible values(1st Postition)
                holder.txtLastUpdated.append(stationDataList.get(position).getPrices().get(0).getLastupdated());


                holder.txtDis.setText("Distance : ");
                try {
                    double distance = SphericalUtil.computeDistanceBetween(new LatLng(cuLocation.getLatitude(), cuLocation.getLongitude()), new LatLng(Double.parseDouble(stationDataModel.getLatitude()), Double.parseDouble(stationDataModel.getLongitude())));

                    if (distance > 1000) {
                        double kilometers = distance / 1000;
                        holder.txtDis.append(CommonUtils.roundUpDecimal(kilometers, 2) + " KM");

                    }

                    else {
                        holder.txtDis.setText("N/A");

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



                            Log.e("hjnmk", stationDataModel.getLatitude() + stationDataModel.getLongitude());

                            intent.putExtra("lat", stationDataModel.getLatitude());
                            intent.putExtra("lng", stationDataModel.getLongitude());

                            startActivity(intent);

                        }
                    }
                });
                holder.btn_submit_prices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(), MainPickmanActivity.class);
                        intent.putExtra("station_id", stationDataModel.getStationid());
//                        intent.putExtra("category", "3");
                        intent.putExtra("category", fuelid);

                        startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }


            //----------------------


//            binding.ivDistanceUpdownAscDesc.setRotation(asc ? 0 : 180);


            //Ascending & Descending
            if (asc) {
                binding.ivDistanceUpdownAscDesc.setRotation(0);


            } else if (!asc) {
                binding.ivDistanceUpdownAscDesc.setRotation(180);
//                binding.ivDistanceUpdownAscDesc.setRotation(360);

            }

            binding.tvDistance.setOnClickListener(view -> {
                HomeFragmentGasaver activity = (HomeFragmentGasaver) HomeFragmentGasaver.context1;


                if (asc) {
                    binding.ivDistanceUpdownAscDesc.setRotation(0);
                    activity.getStationsList("distance", "asc");
                    asc = false;
//                    asc = true;
                    HomeFragmentGasaver.addPhotoBottomDialogFragment.dismissAllowingStateLoss();



//                    WindowCompat.setDecorFitsSystemWindows(window, false);



                } else if (!asc) {
                    binding.ivDistanceUpdownAscDesc.setRotation(180);
//                    binding.ivDistanceUpdownAscDesc.setRotation(360);
                    activity.getStationsList("distance", "desc");
                    asc = true;
//                    asc = false;
                    HomeFragmentGasaver.addPhotoBottomDialogFragment.dismissAllowingStateLoss();
//                    WindowCompat.setDecorFitsSystemWindows(window, false);

                }

//                asc = !asc;

            });

//            binding.ivPriceUpdownAscDesc.setRotation(price_asc ? 0 : 180);

            if (price_asc) {
                binding.ivPriceUpdownAscDesc.setRotation(0);


            } else if (!price_asc) {
                binding.ivPriceUpdownAscDesc.setRotation(180);
//                binding.ivPriceUpdownAscDesc.setRotation(360);

            }

            binding.tvPriceL.setOnClickListener(view -> {
                HomeFragmentGasaver activity = (HomeFragmentGasaver) HomeFragmentGasaver.context1;
                if (price_asc) {
                    binding.ivPriceUpdownAscDesc.setRotation(0);
//                activity.getStationsList("price","asc");
                    activity.getStationsList("price", "asc");
                    price_asc = false;
//                    price_asc = true;
                    HomeFragmentGasaver.addPhotoBottomDialogFragment.dismissAllowingStateLoss();

                }else if (!price_asc) {
                    binding.ivPriceUpdownAscDesc.setRotation(180);

                    activity.getStationsList("price", "desc");
                    HomeFragmentGasaver.addPhotoBottomDialogFragment.dismissAllowingStateLoss();
                    price_asc = true;
                }



            });
//
//
//

            //----------------------






        }


        @Override
        public int getItemCount() {
            return stationDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout layoutid, ll_form, layout_distance, layout_price, layout_km_dollar, recyclerview_markers_list, ll_delete_my_prop;

            ImageView iv_wishlist1, iv_proj_img, ivShare, iv_share, iv_distance_updown_asc_desc, iv_price_updown_asc_desc;

            //TextView tv_addr, tv_name, tv_city, tv_price, tv_dis, tv_lastupdated;
            TextView tv_dis, tv_price, tv_name, tv_addr, tv_city, txtPrices, txtLastUpdated, txtDis;

            AppCompatButton btn_submit_prices, btn_navigate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ll_form = itemView.findViewById(R.id.ll_form);
                layout_distance = itemView.findViewById(R.id.layout_distance);
                layout_price = itemView.findViewById(R.id.layout_price);
//                layout_km_dollar = itemView.findViewById(R.id.layout_km_dollar);
                ll_delete_my_prop = itemView.findViewById(R.id.ll_delete_my_prop);
                ll_delete_my_prop.setVisibility(View.GONE);

                recyclerview_markers_list = itemView.findViewById(R.id.recyclerview_markers_list);


                tv_dis = itemView.findViewById(R.id.tv_distance);
                tv_price = itemView.findViewById(R.id.tv_price_L);

                iv_distance_updown_asc_desc = itemView.findViewById(R.id.iv_distance_updown_asc_desc);
                iv_price_updown_asc_desc = itemView.findViewById(R.id.iv_price_updown_asc_desc);


                layoutid = itemView.findViewById(R.id.layoutid);

                iv_wishlist1 = itemView.findViewById(R.id.iv_wishlist1);
                ivShare = itemView.findViewById(R.id.iv_share);
                iv_proj_img = itemView.findViewById(R.id.iv_proj_img);
                tv_name = itemView.findViewById(R.id.tv_name);


                tv_addr = itemView.findViewById(R.id.tv_addr);
                tv_city = itemView.findViewById(R.id.tvcity);

                txtPrices = itemView.findViewById(R.id.txtPrices);

//                tv_lastupdated = itemView.findViewById(R.id.tv_lastupdated);
                txtLastUpdated = itemView.findViewById(R.id.txtLastUpdated);

                btn_submit_prices = itemView.findViewById(R.id.btn_submit_prices);
                btn_navigate = itemView.findViewById(R.id.btn_navigate);

                txtDis = itemView.findViewById(R.id.txtDis);


            }
        }
    }

    private void saveWishlist(StationDataResponse.StationDataModel stationDataModel, Integer stationid, String iswishlist, ImageView ivWishlist, ImageView ivShare) {

        CommonUtils.showLoading(getActivity(), "Please Wait", false);
        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(getActivity()).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(getActivity()).getString(Constants.TOKEN));
        jsonObject.addProperty("vendor_id", stationid);
        jsonObject.addProperty("wishlist", iswishlist);

        Call<StationDataResponse> call = apiInterface.saveWishlist(jsonObject);

        call.enqueue(new Callback<StationDataResponse>() {

            @Override
            public void onResponse(Call<StationDataResponse> call, Response<StationDataResponse> response) {

                try {
                    CommonUtils.hideLoading();
                    if (response.body().isStatus_code()) {

                        stationDataModel.setWishlist(iswishlist);

                        ivWishlist.setImageResource(iswishlist.equalsIgnoreCase("yes") ? R.drawable.wishlist_added : R.drawable.like_icon);

//                        Glide.with(getActivity()).load(stationDataModel.getBrandIcon()).into(binding.ll_brand_req.imgBrand);

                        ivShare.setOnClickListener(new View.OnClickListener() {
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


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<StationDataResponse> call, Throwable t) {

                CommonUtils.hideLoading();
            }
        });


    }
}