package firdausns.id.smarttataruangtegalkota;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firdausns.id.smarttataruangtegalkota.config.Config;
import firdausns.id.smarttataruangtegalkota.config.ItemKategori;
import firdausns.id.smarttataruangtegalkota.config.MyAdapterKategori;
import firdausns.id.smarttataruangtegalkota.config.UserAPIServices;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetaActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMapLoadedCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private LatLng lastKnownLatLng;
    private SupportMapFragment mapFragment;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private int offset_cari = 0,offset_polg_detail = 0, heighPeek = 0;
    private Double lat_posisi,log_posisi;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder locationSettingsRequest;
    private PendingResult<LocationSettingsResult> pendingResult;

    private BottomSheetBehavior bottomSheetBehaviorPeta, bottomSheetBehaviorTentang;
    private ProgressDialog pDialog;
    private ArrayList<String> DataIem = new ArrayList<>();
    private ArrayList<ItemKategori> itemKategori = new ArrayList<>();
    private MyAdapterKategori myAdapterKategori;
    private AppUpdateManager mAppUpdateManager;
    private final int UPDATE_REQUEST_CODE = 1612;

    CardView card_map_default, card_map_satelit, card_map_medan;
    TextView tv_map_default, tv_map_satelit, tv_map_medan;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.card_toolbar) CardView card_toolbar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ln_find_location) LinearLayout ln_find_location;
    @BindView(R.id.ln_bt_keterangan) LinearLayout ln_bt_keterangan;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.tv_alamat) TextView tv_alamat;
    @BindView(R.id.tv_pola_ruang) TextView tv_pola_ruang;
    @BindView(R.id.tv_kategori) TextView tv_kategori;
    @BindView(R.id.tv_keterangan) TextView tv_keterangan;
    @BindView(R.id.fab_layer) FloatingActionButton fab_layer;
    @BindView(R.id.toolbar_line_tentang) FrameLayout toolbar_line_tentang;
    @BindView(R.id.toolbar_ln) LinearLayout toolbar_ln;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Peta");

        initView();
    }

    private void initView() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        progressBar.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(this));
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        bottomSheetBehaviorPeta = BottomSheetBehavior.from(findViewById(R.id.bottomSheetPeta));
        bottomSheetBehaviorTentang = BottomSheetBehavior.from(findViewById(R.id.bottomSheetTentang));
        bottomSheetBehaviorTentang.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        toggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetBehaviorTentang.setHideable(true);
                                bottomSheetBehaviorTentang.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        });

                        getSupportActionBar().setTitle("Tentang");
                        getSupportActionBar().setSubtitle("");
                        card_toolbar.setCardElevation(0);
                        card_toolbar.setContentPadding(Config.dpToPx(1),Config.dpToPx(1),Config.dpToPx(1),Config.dpToPx(1));

                        fab_layer.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HIDDEN:
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.setDrawerIndicatorEnabled(true);
                        toggle.setToolbarNavigationClickListener(null);
                        card_toolbar.setCardElevation(Config.dpToPx(5));
                        card_toolbar.setContentPadding(0,0,0,0);

                        getSupportActionBar().setTitle("Peta");
                        getSupportActionBar().setSubtitle("");

                        fab_layer.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        initRealtimeMap();
        initViewToolbar();
        setNavDrawer();
        getDataKategori();
        checkUpdate();
    }

    private void initViewToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorBlackTransparent));
        }

        ViewTreeObserver observer= toolbar_ln.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int padTop = Config.getStatusBarHeight(PetaActivity.this)+toolbar_ln.getHeight();
                toolbar_line_tentang.setPadding(0,padTop,0,0);
            }
        });
    }

    private void setNavDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener( result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                try {
                    mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.d("CATATAN UPDT", "callInAppUpdate Error: "+e.getMessage());
                }
            }
        });
        mAppUpdateManager.registerListener(installStateUpdatedListener);
    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED){
                showCompleteUpdate();
            }
        }
    };

    private void showCompleteUpdate(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Aplikasi versi terbaru telah tersedia!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null) mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rb_tentang:
                bottomSheetBehaviorTentang.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.fab_find_location) void OnClick_fab_find_location() {
        findLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBar.setVisibility(View.VISIBLE);
        mMap = googleMap;

        double latitude = -6.872020764416118;
        double longitude = 109.11872149999999;
        LatLng point = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.setOnMapLoadedCallback(this);

        View location_button = mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton");
        location_button.setVisibility(View.GONE);
    }

    @Override
    public void onMapLoaded() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(id);
        if (id==0){
            mMap.clear();
        } else {
            mMap.clear();
            pDialog.show();
            if (bottomSheetBehaviorPeta.getState() == bottomSheetBehaviorPeta.STATE_EXPANDED){
                bottomSheetBehaviorPeta.setHideable(true);
                bottomSheetBehaviorPeta.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            getPolygonDetail(id);
        }
        return true;
    }

    private void getDataAll(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("offset", String.valueOf(offset_cari));
        builder.addFormDataPart("page", "300");
        builder.addFormDataPart("lt", String.valueOf(lat_posisi));
        builder.addFormDataPart("lg", String.valueOf(log_posisi));
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.cari_pola_ruang(requestBody);
        post.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int ttl_record = 0;
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray(Config.TAG_RESULT);
                    ttl_record = jsonObj.getInt("ttl_record");
                    offset_cari = jsonObj.getInt("offset");
                    Double lte = jsonObj.getDouble("lt");
                    Double lge = jsonObj.getDouble("lg");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lte,lge)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    for (int i=0;Config.jsonArray.length()>i;i++){
                        JSONObject c = Config.jsonArray.getJSONObject(i);

                        String status = c.getString("status");
                        String warna = c.getString("warna");
                        String hasil = c.getString("hasil");
                        String nama_kategori = c.getString("nama_kategori");
                        String nama_polygon = c.getString("nama_polygon");
                        String keterangan_polygon = c.getString("keterangan_polygon");

                        if (status.equals("true")){
                            PolygonOptions rectOptions = new PolygonOptions();
                            Config.jsonArray2 = new JSONArray(c.getString("polygon"));
                            for (int j=0;Config.jsonArray2.length() > j;j++){
                                JSONObject d = Config.jsonArray2.getJSONObject(j);
                                String lt = d.getString("latitude");
                                String lg = d.getString("longitude");
                                rectOptions.add(new LatLng(Double.parseDouble(lt),Double.parseDouble(lg)));
                            }
                            if (rectOptions!=null){
                                rectOptions.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.parseColor(warna));
                                mMap.addPolygon(rectOptions);
                            }
                            if (!hasil.equals("outside")){
                                String id_polygon = c.getString("id_polygon");
                                tv_kategori.setText(nama_kategori);
                                tv_pola_ruang.setText(nama_polygon);
                                tv_keterangan.setText(keterangan_polygon);

                                getSupportActionBar().setSubtitle(nama_kategori);
                                Log.d("catatan_hasil","hasil "+hasil+" -- "+id_polygon);
                            }
                        }
                    }

                    if (offset_cari >= ttl_record){
                        Log.d("catatan","Selesai size "+String.valueOf(offset_cari));
                        bottomSheetBehaviorPeta.setState(BottomSheetBehavior.STATE_EXPANDED);
                        recyclerView.setVisibility(View.GONE);
                        ln_bt_keterangan.setVisibility(View.VISIBLE);
                        pDialog.dismiss();
                        allways_on(false);
                    } else {
                        Log.d("catatan","Belum selesai size "+String.valueOf(offset_cari));
                        allways_on(true);
                        getDataAll();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!", Toast.LENGTH_LONG).show();
                    allways_on(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!!", Toast.LENGTH_LONG).show();
                    allways_on(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(PetaActivity.this, "Silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                Log.d("catatan",call.toString()+"\n"+t.toString());
                allways_on(false);
            }
        });
    }

    private void getDataKategori(){
        pDialog.show();
        DataIem.clear();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getKategoriPolygon();
        post.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    Log.d("error",json.toString());
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray(Config.TAG_RESULT);

                    Menu menu = navigationView.getMenu();
                    Menu submenu = menu.addSubMenu("Daftar Kategori");
                    for (int i=0;Config.jsonArray.length()>i;i++){
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        int id = c.getInt("id");
                        String nama = c.getString("nama");
                        DataIem.add(nama);
                        submenu.add(0,id,0,nama);
                    }
                    navigationView.invalidate();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(PetaActivity.this, "Silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRealtimeMap() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mEnableGps();
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //locationChangeMarker(lastKnownLatLng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void locationChangeMarker(LatLng latLng) {
        lat_posisi = latLng.latitude;
        log_posisi = latLng.longitude;
        offset_cari = 0;
        if (mMap != null){
            mMap.clear();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        String alamat = Config.getAddressSimple(latLng.latitude,latLng.longitude,this);
        tv_alamat.setText(alamat);

        pDialog.show();
        hideBottom();
//        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
//            bottomSheetBehavior.setHideable(true);
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        }
        getDataAll();
    }

    private void findLocation(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessage("Silahkan nyalakan GPS Anda!!!");
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getLocation();
        }
    }

    private void buildAlertMessage(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        mEnableGps();
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

    private void getLocation(){
        progressBar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
            if (location != null) {
                lastKnownLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                locationChangeMarker(lastKnownLatLng);
            } else  if (location1 != null) {
                lastKnownLatLng = new LatLng(location1.getLatitude(),location1.getLongitude());
                locationChangeMarker(lastKnownLatLng);
            } else  if (location2 != null) {
                lastKnownLatLng = new LatLng(location2.getLatitude(),location2.getLongitude());
                locationChangeMarker(lastKnownLatLng);
            }else{
                buildAlertMessage("Tidak Bisa Mengambil Lokasi Anda. Aktifkan GPS Anda");
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void mEnableGps() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mResult();
    }

    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(PetaActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "GPS Aktif", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "GPS Tidak Aktif", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == UPDATE_REQUEST_CODE){
            Toast.makeText(this, "Unduhan Dimulai", Toast.LENGTH_LONG).show();
            if (resultCode != RESULT_OK){
                Log.d("CATATAN UPDT", "onActivityResult : Update flow failed "+resultCode);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideBottom();
    }

    private void hideBottom(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            heighPeek = ln_find_location.getHeight();
            bottomSheetBehaviorPeta.setPeekHeight(heighPeek);
        }
    }

    private void getPolygonDetail(final int id) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_kategori", String.valueOf(id));
        builder.addFormDataPart("page", "300");
        builder.addFormDataPart("offset", String.valueOf(offset_polg_detail));
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getPolygonDetail(requestBody);
        post.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    itemKategori.clear();
                    int ttl_record = 0;
                    Double lt_p = 0.0,lg_p = 0.0;
                    boolean stt = false;
                    String warna = null;
                    ArrayList<Double> list_all_lt = new ArrayList<>();
                    ArrayList<Double> list_all_lg = new ArrayList<>();
                    ArrayList<String> list_all_id_poly = new ArrayList<>();

                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray(Config.TAG_RESULT);
                    ttl_record = jsonObj.getInt("ttl_record");
                    offset_polg_detail = jsonObj.getInt("offset");

                    for (int i=0;Config.jsonArray.length()>i;i++){
                        JSONObject c = Config.jsonArray.getJSONObject(i);

                        int id_polygon = c.getInt("id_polygon");
                        warna = c.getString("warna");
                        String nama_kategori = c.getString("nama_kategori");
                        String nama_polygon = c.getString("nama_polygon");
                        String keterangan = c.getString("keterangan");
                        getSupportActionBar().setSubtitle(nama_kategori);

                        ArrayList<String> list_id_poly = new ArrayList<>();
                        ArrayList<Double> list_lt = new ArrayList<>();
                        ArrayList<Double> list_lg = new ArrayList<>();

                        PolygonOptions rectOptions = new PolygonOptions();
                        Config.jsonArray2 = new JSONArray(c.getString("polygon"));
                        for (int j=0;Config.jsonArray2.length() > j;j++){
                            JSONObject d = Config.jsonArray2.getJSONObject(j);
                            String id_poly = d.getString("id_polygon");
                            String lt = d.getString("latitude");
                            String lg = d.getString("longitude");

                            if (ttl_record > 150){
                                lt_p = Double.valueOf(lt);
                                lg_p = Double.valueOf(lg);
                                rectOptions.add(new LatLng(Double.parseDouble(lt),Double.parseDouble(lg)));
                            } else {
                                list_lt.add(Double.valueOf(lt));
                                list_lg.add(Double.valueOf(lg));
                                list_id_poly.add(id_poly);
                                list_all_lt.add(Double.valueOf(lt));
                                list_all_lg.add(Double.valueOf(lg));
                                list_all_id_poly.add(id_poly);
                            }

                        }
                        if (ttl_record > 150){
                            if (rectOptions!=null){
                                rectOptions.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.parseColor(warna)).zIndex(id);
                                mMap.addPolygon(rectOptions);
                                stt = true;
                            }
                        } else {
                            itemKategori.add(new ItemKategori(id_polygon, warna, nama_kategori, nama_polygon, keterangan, list_id_poly, list_lt,  list_lg, false));
                        }
                    }

                    if (offset_polg_detail >= ttl_record){
                        pDialog.dismiss();
                        Log.d("catatan","Selesai size "+String.valueOf(offset_polg_detail));
                        offset_polg_detail = 0 ;

                        allways_on(false);
                        if (ttl_record > 150){
                            if (stt){
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lt_p,lg_p)).zoom(14).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            ln_bt_keterangan.setVisibility(View.GONE);
                            myAdapterKategori = new MyAdapterKategori(itemKategori,PetaActivity.this);
                            recyclerView.setAdapter(myAdapterKategori);

                            itemKategori.add(0,new ItemKategori(0, warna, "Semua", "Semua", "Semua Data Polygon", null, null,  null, false));
                            myAdapterKategori.notifyItemInserted(0);
                            myAdapterKategori.SetOnItemClickListener(new MyAdapterKategori.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    for (int pos = 0; pos < itemKategori.size(); pos++){
                                        itemKategori.get(pos).setPilih(false);
                                        if (position == pos){
                                            itemKategori.get(pos).setPilih(true);
                                        }
                                    }
                                    myAdapterKategori.notifyDataSetChanged();
                                    setKategoriPoly(position);
                                }
                            });

                            bottomSheetBehaviorPeta.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    } else {
                        Log.d("catatan","Belum selesai size "+String.valueOf(offset_polg_detail));
                        allways_on(true);
                        getPolygonDetail(id);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!", Toast.LENGTH_LONG).show();
                    offset_polg_detail = 0 ;
                    allways_on(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    allways_on(false);
                    Toast.makeText(PetaActivity.this, "Tidak bisa mengambil data!!", Toast.LENGTH_LONG).show();
                    offset_polg_detail = 0 ;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                allways_on(false);
                Toast.makeText(PetaActivity.this, "Silahkan cek koneksi internet anda", Toast.LENGTH_LONG).show();
                Log.d("catatan",call.toString()+"\n"+t.toString());
                offset_polg_detail = 0 ;
            }
        });
    }

    private void setKategoriPoly(int position){
        boolean stt = false;

        ArrayList<Double> list_item_lt = new ArrayList<>();
        ArrayList<Double> list_item_lg = new ArrayList<>();
        list_item_lt = itemKategori.get(position).getList_lt();
        list_item_lg = itemKategori.get(position).getList_lg();
        String warna = itemKategori.get(position).getWarna();
        Double lt_p = null, lg_p = null;
        int id = itemKategori.get(position).getId_polygon();

        if (position > 0){
            PolygonOptions rectOptions = new PolygonOptions();
            for (int i = 0; i<list_item_lt.size();i++){
                lt_p = list_item_lt.get(i);
                lg_p = list_item_lg.get(i);
                rectOptions.add(new LatLng(lt_p,lg_p));
            }
            if (rectOptions!=null){
                mMap.clear();
                rectOptions.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.parseColor(warna)).zIndex(id);
                mMap.addPolygon(rectOptions);
                stt = true;
            }
            if (stt){
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lt_p,lg_p)).zoom(14).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else {
            mMap.clear();
            for (int i=0;i<itemKategori.size();i++){
                ArrayList<Double> list_lt = new ArrayList<>();
                ArrayList<Double> list_lg = new ArrayList<>();
                list_lt = itemKategori.get(i).getList_lt();
                list_lg = itemKategori.get(i).getList_lg();
                int id_poly = itemKategori.get(i).getId_polygon();
                Log.d("catatan", "id-poly tampil => "+String.valueOf(id_poly));
                if (id_poly > 0){
                    PolygonOptions rectOptions = new PolygonOptions();
                    for (int j=0;j<list_lt.size();j++){
                        lt_p = list_lt.get(j);
                        lg_p = list_lg.get(j);
                        rectOptions.add(new LatLng(lt_p,lg_p));
                    }
                    if (rectOptions!=null){
                        rectOptions.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.parseColor(warna)).zIndex(id);
                        mMap.addPolygon(rectOptions);
                        stt = true;
                    }
                    if (stt){
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lt_p,lg_p)).zoom(14).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        stt = false;
                    }
                }
            }
        }
    }

    public void allways_on(boolean stt){
        if (stt){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void popup_layer(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layer, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        card_map_default = popupView.findViewById(R.id.card_map_default);
        card_map_satelit = popupView.findViewById(R.id.card_map_satelit);
        card_map_medan = popupView.findViewById(R.id.card_map_medan);
        tv_map_default = popupView.findViewById(R.id.tv_map_default);
        tv_map_satelit = popupView.findViewById(R.id.tv_map_satelit);
        tv_map_medan = popupView.findViewById(R.id.tv_map_medan);

        card_map_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayerMap("default");
            }
        });
        card_map_satelit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayerMap("satelit");
            }
        });
        card_map_medan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayerMap("medan");
            }
        });
    }

    private void setLayerMap(String type){
        card_map_default.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        card_map_satelit.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        card_map_medan.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        tv_map_default.setTextColor(getResources().getColor(R.color.colorGray50_600));
        tv_map_satelit.setTextColor(getResources().getColor(R.color.colorGray50_600));
        tv_map_medan.setTextColor(getResources().getColor(R.color.colorGray50_600));
        if (type.equals("default")){
            card_map_default.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv_map_default.setTextColor(getResources().getColor(R.color.colorPrimary));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (type.equals("satelit")){
            card_map_satelit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv_map_satelit.setTextColor(getResources().getColor(R.color.colorPrimary));
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (type.equals("medan")){
            card_map_medan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv_map_medan.setTextColor(getResources().getColor(R.color.colorPrimary));
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
    }
}
