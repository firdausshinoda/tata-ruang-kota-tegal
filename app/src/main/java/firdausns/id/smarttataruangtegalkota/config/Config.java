package firdausns.id.smarttataruangtegalkota.config;

import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import firdausns.id.smarttataruangtegalkota.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {
    //    public static final String BASE_URL = "http://smartmobile.tegalkota.go.id";
//    public static final String BASE_URL = "http://10.0.2.2/tataruang/";
    public static final String BASE_URL = "http://tataruang.tegalkota.go.id";
    public static final String URL = BASE_URL+"/index.php/Api/";

    public static JSONArray jsonArray = null;
    public static JSONArray jsonArray2 = null;
    public static JSONArray jsonArray3 = null;
    public static JSONArray jsonArray4 = null;

    public static final String TAG_RESULT = "result";
    public static final String TAG_STATUS = "status";
    public static Retrofit getRetrofit(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS).build();

        return new Retrofit.Builder().baseUrl(url).client(client).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getAddressSimple(Double late, Double longe, Context context) {
        String addressStr = "";
        Geocoder geocoder;
        List<Address> yourAddresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            yourAddresses = geocoder.getFromLocation(late,longe, 1);
            if (yourAddresses.size() > 0) {
                addressStr += yourAddresses.get(0).getAddressLine(0);
                addressStr += yourAddresses.get(0).getAddressLine(1);
                addressStr += yourAddresses.get(0).getAddressLine(2);
            } else {
                addressStr = context.getString(R.string.no_address_returned);
            }
        } catch (IOException e) {
            e.printStackTrace();
            addressStr = context.getString(R.string.no_address_returned);
        }
        return addressStr;
    }
}
