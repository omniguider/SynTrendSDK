package com.omni.syntrendsdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.THLight.USBeacon.App.Lib.BatteryPowerData;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
//import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.omni.syntrendsdk.manager.BeaconDataManager;
import com.omni.syntrendsdk.manager.DataCacheManager;
import com.omni.syntrendsdk.module.BeaconInfo;
import com.omni.syntrendsdk.module.BeaconPushContent;
import com.omni.syntrendsdk.module.BuildingFloor;
import com.omni.syntrendsdk.module.NavigationRoutePOI;
import com.omni.syntrendsdk.module.NavigationType;
import com.omni.syntrendsdk.module.OmniClusterItem;
import com.omni.syntrendsdk.module.OmniEvent;
import com.omni.syntrendsdk.module.OmniFloor;
import com.omni.syntrendsdk.module.POI;
import com.omni.syntrendsdk.module.SetBeaconBatteryResponse;
import com.omni.syntrendsdk.network.LocationApi;
import com.omni.syntrendsdk.network.NetworkManager;
import com.omni.syntrendsdk.network.SynTrendApi;
import com.omni.syntrendsdk.service.OGService;
import com.omni.syntrendsdk.tool.DialogTools;
import com.omni.syntrendsdk.tool.PreferencesTools;
import com.omni.syntrendsdk.tool.SynTrendText;
import com.omni.syntrendsdk.tool.Tools;
import com.omni.syntrendsdk.view.CircleNetworkImageView;
import com.omni.syntrendsdk.view.OmniClusterRender;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omni.syntrendsdk.SynTrendSDKActivity.POI_CATE.FACILITY_CATE;
import static com.omni.syntrendsdk.SynTrendSDKActivity.POI_CATE.RESTAURANT_CATE;
import static com.omni.syntrendsdk.SynTrendSDKActivity.POI_CATE.STORE_CATE;
import static com.omni.syntrendsdk.SynTrendSDKActivity.POI_CATE.THEATER_CATE;

public class SynTrendSDKActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowCloseListener,
        BeaconConsumer,
        BluetoothAdapter.LeScanCallback {

    public static final LatLng START_LOCATION = new LatLng(25.045130666062, 121.53120953099);

    private static final String ARG_KEY_GUIDE_CATEGORY = "arg_key_guide_category";
    private static final String ARG_KEY_FACILITY_TYPE = "arg_key_facility_type";
    private static final String ARG_KEY_FACILITY_TITLE = "arg_key_facility_title";
    private static final String ARG_KEY_STORE_ROUTE_A = "arg_key_store_route_a";
    private static final String ARG_KEY_STORE_ROUTE_B = "arg_key_store_route_b";
    private static final String ARG_KEY_AUTO_HEADING = "arg_key_auto_heading";
    private static final String ARG_KEY_NAVIGATE_DIRECT = "arg_key_navigate_direct";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int floorNum = 9;

    //    private String type;
    private String guide_category;
    private String facility_type;
    private String facility_title;
    private String route_custom;
    private String route_recommend;
    private boolean autoHeading;
    private boolean naviDirect;

    private BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    private BeaconManager mBeaconManager;
    private HandlerThread mBBHandlerThread;
    private Handler mBBHandler;
    private String mLastSendBatteryMac;
    final int MSG_LE_START_SCAN = 1000;
    final int MSG_LE_STOP_SCAN = 1001;
    final int MSG_GET_DATA = 1002;
    final int MSG_STOP_SCAN = 1003;
    private int mBeaconCooldownTime = 1;
    private long mNotificationSendTime = -1;
    private boolean isGettingAllBeaconInfo = false;
    final List<String> SynTrend_BEACON_MAJOR_LIST = new ArrayList<String>() {{
        add("5101");
        add("5102");
        add("5103");
        add("5104");
        add("5105");
        add("5111");
        add("5112");
        add("5201");
        add("5202");
        add("5203");
        add("5204");
        add("5211");
        add("5301");
        add("5302");
        add("5303");
        add("5304");
        add("5311");
    }};

    public class POI_CATE {
        public static final int STORE_CATE = 0;
        public static final int FACILITY_CATE = 1;
        public static final int RESTAURANT_CATE = 2;
        public static final int THEATER_CATE = 3;
    }

    private int current_cate = STORE_CATE;

    public class NaviMode {
        public static final int USER_IN_NAVIGATION = 0;
        public static final int WATCH_OTHER_PLACE = 1;
        public static final int NOT_NAVIGATION = 2;
    }

    private Handler mTimeHandler;
    private final Runnable mTimeRunner = new Runnable() {
        @Override
        public void run() {
            mTimeHandler.removeCallbacks(this);

            checkMapInit();
        }
    };

    private GoogleMap mMap;
    private ClusterManager<OmniClusterItem> mClusterManager;
    private Location mLastLocation;
    private IATask<IAFloorPlan> mPendingAsyncResult;
    //    private IAResourceManager mIAResourceManager;
    private TextView mFloorLevelTV;
    private Marker mUserMarker;
    private Marker mNaviStartMarker;
    private Marker mNaviEndMarker;
    private Map<String, TileOverlay> mTileOverlayMap;
    private List<NavigationRoutePOI> mCurrentRouteList;
    private int mNavigationMode = NaviMode.NOT_NAVIGATION;
    private LinearLayout mFloorsLayout;
    private LinearLayout mFloorsLayoutNew;
    private FrameLayout mMaskLayout;
    private Circle mUserAccuracyCircle;
    private List<OmniClusterItem> itemList;
    private List<String> menuList;
    private boolean mIsIndoor = false;
    private POI mCurrentSelectedPOI;
    private POI mEndPOI;
    private RelativeLayout mNaviInfoRL;
    private CircleNetworkImageView mNaviInfoIconCNIV;
    private TextView mNaviInfoTitleTV;
    private FrameLayout mPOIInfoLayout;
    private NetworkImageView mPOIInfoPicNIV;
    private TextView mPOIInfoContentTV;
    private int cameraMoveTimes = 0;
    private boolean startRecordFlag = false;
    private List<String[]> recordData = new ArrayList<String[]>();
    private boolean mIsMapInited = false;
    private SupportMapFragment mMapFragment;
    private EventBus mEventBus;
    private OGService mOGService;
    private OmniClusterRender mOmniClusterRender;
    private RelativeLayout mPOIInfoHeaderLayout;
    private ImageView mPOIInfoHeaderArrowIV;
    private BottomSheetBehavior mBottomSheetBehavior;
    private CircleNetworkImageView mPOIInfoIconCNIV;
    private TextView mPOIInfoTitleTV;
    private Marker mOriginalPOIMarker;
    private NavigationType mNavigationType;
    private BuildingFloor groundFloor;
    private Button mSroreBtn;
    private Button mFacilityBtn;
    private Button mRestaurantBtn;
    private Button mTheaterBtn;
    private int REQUEST_CODE = 1;
    private int REQUEST_CODE_BLUETOOTH = 2;
    private LinearLayout categoryLayout;
    private ImageView searchIcon;
    private ImageView menuIcon;
    private FrameLayout menuIconLayout;
    private String EndPOIId = "";
    private List<NavigationRoutePOI> routePOIList;
    private List<NavigationRoutePOI> NavigationRoutePOIListAll;
    private List<Marker> MarkerList;
    private int NavigationRoutePOIListAllPosition = 0;
    private int MarkerListPosition = 0;
    private List<NavigationRoutePOI> NavigationRoutePOIListA;
    private List<NavigationRoutePOI> NavigationRoutePOIListB;
    private boolean initRoute = true;
    private Marker mNavigationMarker;
    private boolean mapReady = false;
    private boolean mapReadyNavi = false;
    private boolean titleSetting = false;
    private TextView headerNaviTV;
    private ImageView headerNaviIV;
    public static boolean withAccelerometer = false;
    public static boolean withGyroscope = false;
    protected PowerManager.WakeLock mWakeLock;

    private LatLng userPosition = null;
    private LatLng previousPoint = null;
    private LatLng closestPoint = null;
    private double closestDistance = -1;
    private double heading = -1;
    private double distanceLine;
    private double distanceSegment;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_USER_OUTDOOR_LOCATION:
                mLastLocation = (Location) event.getObj();
                mIsIndoor = false;

                showUserPosition();
                break;

            case OmniEvent.TYPE_USER_INDOOR_LOCATION:
                Log.e("OKOK", "TYPE_USER_INDOOR_LOCATION");
                mLastLocation = (Location) event.getObj();
                mIsIndoor = true;

                showUserPosition();
                break;

            case OmniEvent.TYPE_FLOOR_PLAN_CHANGED:
                String floorPlanId = event.getContent();
                if (DataCacheManager.getInstance().containsFloor(this, floorPlanId)) {
                    Log.e("@W@", "TYPE_FLOOR_PLAN_CHANGED id : " + floorPlanId);
                    if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A) && mNavigationMode != NaviMode.USER_IN_NAVIGATION) {
                    } else {
                        fetchFloorPlan(floorPlanId, true, DataCacheManager.getInstance().getFloorNumberByPlanId(this, floorPlanId));
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : allSensors) {
            switch (s.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    withAccelerometer = true;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    withGyroscope = true;
                    break;
            }
        }

        if (!withAccelerometer || !withGyroscope) {
            DialogTools.getInstance().showErrorMessage(this, getString(R.string.error_dialog_title_text_normal), getResources().getString(R.string.without_sensor_hint));
        }

        setContentView(R.layout.stsdk_activity_main);
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);

        if (mTimeHandler == null) {
            mTimeHandler = new Handler();
            mTimeHandler.postDelayed(mTimeRunner, 1000);
        }

        guide_category = getIntent().getStringExtra(ARG_KEY_GUIDE_CATEGORY);
        facility_type = getIntent().getStringExtra(ARG_KEY_FACILITY_TYPE);
        facility_title = getIntent().getStringExtra(ARG_KEY_FACILITY_TITLE);
        route_custom = getIntent().getStringExtra(ARG_KEY_STORE_ROUTE_A);
        route_recommend = getIntent().getStringExtra(ARG_KEY_STORE_ROUTE_B);
        autoHeading = getIntent().getBooleanExtra(ARG_KEY_AUTO_HEADING, true);
        naviDirect = getIntent().getBooleanExtra(ARG_KEY_NAVIGATE_DIRECT, false);

        DataCacheManager.getInstance().initAllBuildingsData(this);
        checkLocationService();
        checkBluetoothOn();
        startScanBeacon();
        initView();
    }

    private void checkBluetoothOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_CODE_BLUETOOTH);
            }
        }
    }

    public void startScanBeacon() {
        mBBHandlerThread = new HandlerThread("HandlerThread");
        mBBHandlerThread.start();
        mBBHandler = new Handler(mBBHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_LE_START_SCAN:
                        if (mBTAdapter.isEnabled()) {
                            mBTAdapter.startLeScan(SynTrendSDKActivity.this);
                        }
                        break;

                    case MSG_LE_STOP_SCAN:
                        if (mBTAdapter.isEnabled()) {
                            mBTAdapter.stopLeScan(SynTrendSDKActivity.this);
                        }
                        break;

                    case MSG_STOP_SCAN:
                        mBBHandler.removeMessages(MSG_LE_START_SCAN);
                        mBBHandler.removeMessages(MSG_LE_STOP_SCAN);
                        if (mBTAdapter.isEnabled()) {
                            mBTAdapter.stopLeScan(SynTrendSDKActivity.this);
                        }
                        break;
                    case MSG_GET_DATA:
                        break;

                }
                super.handleMessage(msg);
            }
        };
        mBBHandler.sendEmptyMessage(MSG_LE_START_SCAN);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapFragment != null) {
            mMapFragment.onLowMemory();
        }
    }

    @Override
    protected void onResume() {
        if (mMapFragment != null) {
            mMapFragment.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapFragment != null) {
            mMapFragment.onPause();
        }
//        if (mOGService != null) {
//            mOGService.stopService();
//        }
//        DialogTools.getInstance().dismissProgress(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapFragment != null) {
            mMapFragment.onDestroy();
        }

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        if (mTimeHandler != null && mTimeRunner != null) {
            mTimeHandler.removeCallbacks(mTimeRunner);
        }

        if (mOGService != null) {
            mOGService.stopService();
            mOGService.destroy();
            Log.e("OKOK", "mOGService.destroy()");
        }

        if (mLastLocation != null) {
            mLastLocation = null;
        }
    }

    private void initView() {
        findViewById(R.id.map_content_view_fl_action_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_content_view_map);
        mMapFragment.getMapAsync(this);
        mFloorLevelTV = findViewById(R.id.map_content_view_tv_floor_level);
//        mFloorsLayout = findViewById(R.id.map_content_view_ll_floors);
        mFloorsLayoutNew = findViewById(R.id.map_content_view_floor_ll);
        mMaskLayout = findViewById(R.id.map_content_view_mask);
        mFloorLevelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloorsLayoutNew.setVisibility(mFloorsLayoutNew.isShown() ? View.GONE : View.VISIBLE);
                mMaskLayout.setVisibility(mMaskLayout.isShown() ? View.GONE : View.VISIBLE);
//                collapseBottomSheet();
            }
        });
        mMaskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloorsLayoutNew.setVisibility(mFloorsLayoutNew.isShown() ? View.GONE : View.VISIBLE);
                mMaskLayout.setVisibility(mMaskLayout.isShown() ? View.GONE : View.VISIBLE);
            }
        });

//        findViewById(R.id.map_content_view_tv_floor_level).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("OKOK", "mFloorsLayout onClick");
//                mFloorsLayout.setVisibility(mFloorsLayout.isShown() ? View.GONE : View.VISIBLE);
//            }
//        });
        findViewById(R.id.map_content_view_fab_current_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null && mLastLocation != null) {
                    LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("OKOK", "mLastLocation.getLatitude()" + mLastLocation.getLatitude());
                    Log.e("OKOK", "mLastLocation.getLongitude()" + mLastLocation.getLongitude());
                    addUserMarker(current, mLastLocation);
                    String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
                    if (mIsIndoor && !userCurrentFloorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9")) {
                        fetchFloorPlan(userCurrentFloorPlanId, false, DataCacheManager.getInstance().getFloorNumberByPlanId(SynTrendSDKActivity.this, userCurrentFloorPlanId));
                    } else {
                        fetchFloorPlan(groundFloor.getFloorPlanId(), false, "1");
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, SynTrendText.MAP_ZOOM_LEVEL));
                }
            }
        });
        mPOIInfoLayout = findViewById(R.id.ntsdk_activity_main_poi_info);
        mPOIInfoPicNIV = mPOIInfoLayout.findViewById(R.id.poi_info_view_niv);
        mPOIInfoContentTV = mPOIInfoLayout.findViewById(R.id.poi_info_view_desc);
        mPOIInfoHeaderLayout = mPOIInfoLayout.findViewById(R.id.poi_info_view_header);
        mPOIInfoHeaderArrowIV = mPOIInfoLayout.findViewById(R.id.item_poi_header_iv_arrow);
//        if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
//            mPOIInfoHeaderArrowIV.setVisibility(View.GONE);
//        }
//        mPOIInfoIconCNIV = mPOIInfoHeaderLayout.findViewById(R.id.poi_info_header_view_cniv);
        mPOIInfoIconCNIV = findViewById(R.id.item_poi_header_iv_icon);
        mPOIInfoTitleTV = mPOIInfoLayout.findViewById(R.id.poi_info_header_view_tv_title);
        headerNaviTV = mPOIInfoHeaderLayout.findViewById(R.id.poi_info_header_view_tv_navi);
        headerNaviTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavigationData();
            }
        });
        headerNaviIV = mPOIInfoHeaderLayout.findViewById(R.id.poi_info_header_view_iv_navi);
        headerNaviIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavigationData();
            }
        });

        FrameLayout fl = (FrameLayout) mPOIInfoLayout.getParent();
        mBottomSheetBehavior = BottomSheetBehavior.from(fl);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mPOIInfoLayout.requestLayout();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mPOIInfoLayout.requestLayout();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        categoryLayout = findViewById(R.id.map_content_view_category_btn);
        mSroreBtn = findViewById(R.id.map_content_view_store_btn);
        mFacilityBtn = findViewById(R.id.map_content_view_facility_btn);
        mRestaurantBtn = findViewById(R.id.map_content_view_restaurant_btn);
        mTheaterBtn = findViewById(R.id.map_content_view_theater_btn);

        mSroreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStore();
            }
        });
        mFacilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFacility();
            }
        });
        mRestaurantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRestaurant();
            }
        });
        mTheaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTheater();
            }
        });

        mNaviInfoRL = findViewById(R.id.ntsdk_activity_main_navi_view);
        mNaviInfoIconCNIV = mNaviInfoRL.findViewById(R.id.navigation_info_view_cniv);
        mNaviInfoTitleTV = mNaviInfoRL.findViewById(R.id.navigation_info_view_tv_title);
        mNaviInfoRL.findViewById(R.id.navigation_info_view_tv_leave_navi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveNavigation();
            }
        });
        mNaviInfoRL.findViewById(R.id.navigation_info_view_iv_leave_navi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveNavigation();
            }
        });

        menuIconLayout = findViewById(R.id.map_content_view_fl_action_bar_menu);
//        menuIcon = findViewById(R.id.map_content_view_iv_action_bar_menu);
        menuIcon = findViewById(R.id.poi_info_header_view_iv_list);
        menuIcon.setVisibility(View.GONE);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.activity_menu_list, null);

//                final PopupWindow popupWindow = new PopupWindow(popupView,
//                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//                popupWindow.showAsDropDown(view);
//                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                    }
//                });
                ListView menuListView = popupView.findViewById(R.id.menu_list);
                menuListView.setDividerHeight(0);
                if (menuList != null) {
                    menuListView.setAdapter(new menuAdapter(SynTrendSDKActivity.this, menuList));
                }
                menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
                            selectPOI(itemList.get(position));
                        }
//                        popupWindow.dismiss();
                    }
                });

                Dialog dialog = new Dialog(SynTrendSDKActivity.this);
                dialog.setContentView(popupView);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });

        searchIcon = findViewById(R.id.map_content_view_iv_action_bar_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent();
                searchIntent.setClass(SynTrendSDKActivity.this, PoiSearchActivity.class);
                startActivityForResult(searchIntent, REQUEST_CODE);
                collapseBottomSheet();
            }
        });

    }

    private void selectStore() {
        current_cate = STORE_CATE;
        String id = DataCacheManager.getInstance().getCurrentShowFloor().getFloorPlanId();
        String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(SynTrendSDKActivity.this, id);
        addPOIMarkers(buildingId, id, STORE_CATE);
        mSroreBtn.setTextColor(getResources().getColor(android.R.color.white));
        mSroreBtn.setBackgroundColor(getResources().getColor(R.color.blue_25));
        mFacilityBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mFacilityBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mRestaurantBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mRestaurantBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mTheaterBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mTheaterBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
    }

    private void selectFacility() {
        current_cate = FACILITY_CATE;
        String id = DataCacheManager.getInstance().getCurrentShowFloor().getFloorPlanId();
        String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(SynTrendSDKActivity.this, id);
        addPOIMarkers(buildingId, id, FACILITY_CATE);
        mSroreBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mSroreBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mFacilityBtn.setTextColor(getResources().getColor(android.R.color.white));
        mFacilityBtn.setBackgroundColor(getResources().getColor(R.color.blue_25));
        mRestaurantBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mRestaurantBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mTheaterBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mTheaterBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
    }

    private void selectRestaurant() {
        current_cate = RESTAURANT_CATE;
        String id = DataCacheManager.getInstance().getCurrentShowFloor().getFloorPlanId();
        String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(SynTrendSDKActivity.this, id);
        addPOIMarkers(buildingId, id, RESTAURANT_CATE);
        mSroreBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mSroreBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mFacilityBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mFacilityBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mRestaurantBtn.setTextColor(getResources().getColor(android.R.color.white));
        mRestaurantBtn.setBackgroundColor(getResources().getColor(R.color.blue_25));
        mTheaterBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mTheaterBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
    }

    private void selectTheater() {
        current_cate = THEATER_CATE;
        String id = DataCacheManager.getInstance().getCurrentShowFloor().getFloorPlanId();
        String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(SynTrendSDKActivity.this, id);
        addPOIMarkers(buildingId, id, THEATER_CATE);
        mSroreBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mSroreBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mFacilityBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mFacilityBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mRestaurantBtn.setTextColor(getResources().getColor(R.color.gray_a7));
        mRestaurantBtn.setBackgroundColor(getResources().getColor(R.color.black_3d));
        mTheaterBtn.setTextColor(getResources().getColor(android.R.color.white));
        mTheaterBtn.setBackgroundColor(getResources().getColor(R.color.blue_25));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateSelectedPOI((POI) data.getExtras().get(SynTrendText.INTENT_EXTRAS_SELECTED_POI));
            }
        }
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void updateSelectedPOI(final POI selectedPoi) {
        Log.e("OKOK", "updateSelectedPOI");
        if (selectedPoi.getType().equalsIgnoreCase("store")) {
            selectStore();
        } else if (selectedPoi.getType().equals("Clapper Theater")
                || selectedPoi.getType().equals("Clapper studio")
                || selectedPoi.getType().equals("Event space")) {
            selectTheater();
        } else if (selectedPoi.getType().equals("Restaurant")
                || selectedPoi.getType().equals("restaurant")
                || selectedPoi.getType().equals("Food court")
                || selectedPoi.getType().equals("cafe")) {
            selectRestaurant();
        } else {
            selectFacility();
        }
        BuildingFloor floor = DataCacheManager.getInstance().getSearchFloorPlanId(SynTrendSDKActivity.this, selectedPoi.getId());
        fetchFloorPlan(floor.getFloorPlanId(), false, floor.getNumber());
        final String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(this, floor.getFloorPlanId());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                itemList = DataCacheManager.getInstance().getClusterListByBuildingId(buildingId);
                Log.e("OKOK", "itemList.size()" + itemList.size());
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getPOI().getId() == (selectedPoi.getId())) {
                        selectPOI(itemList.get(i));
                        break;
                    }
//                selectPOI(itemList.get(i));
                }
            }
        }, 1000);
    }

    class menuAdapter extends BaseAdapter {
        Context context;
        List<String> data;
        private LayoutInflater inflater;

        menuAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null)
                view = inflater.inflate(R.layout.item_menu_list, null);
            TextView title = view.findViewById(R.id.item_menu_list_title);
            title.setText(data.get(position));
            return view;
        }
    }

    private void selectPOI(OmniClusterItem item) {
        Log.e("OKOK", "selectPOI" + item.getTitle());
        if (mOmniClusterRender != null) {
//            DialogTools.getInstance().dismissProgress(this);
            Marker marker = mOmniClusterRender.getMarker(item);
            POI poi = item.getPOI();
            if (marker != null) {
                if (marker.getTag() == null) {
                    marker.setTag(poi);
                }
                marker.showInfoWindow();
                marker.setIcon(BitmapDescriptorFactory.fromResource(poi.getPOIIconRes(true)));
                showPOIInfo(marker);

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing((float) 15.5)
                        .target(new LatLng(poi.getLatitude(), poi.getLongitude()))
                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                        .build()));
            }
        }
    }

    private void showStoreNaviPOIInfo(final Marker marker) {
        Log.e("OKOK", "showStoreNaviPOIInfo");
        if (!TextUtils.isEmpty(marker.getTitle())) {
            LinearLayout.LayoutParams naviTVParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            naviTVParams.setMargins(0, 0, 0, Tools.getInstance().dpToIntPx(getApplicationContext(), 16));
            naviTVParams.gravity = Gravity.CENTER;
            int iconHeight = findViewById(R.id.item_poi_header_iv_icon_fl).getHeight() / 2;
            mBottomSheetBehavior.setPeekHeight(mPOIInfoHeaderLayout.getHeight() + iconHeight);
            mPOIInfoHeaderLayout.requestLayout();
            NavigationRoutePOI poi = (NavigationRoutePOI) marker.getTag();
            if (poi.getPoisType().equalsIgnoreCase("store")) {
                NetworkManager.getInstance().setNetworkImage(this, mPOIInfoIconCNIV, "", R.mipmap.syn_poi_store, R.mipmap.syn_poi_information);
            }
            if (poi.getPoisType().equals("Clapper Theater")
                    || poi.getPoisType().equals("Clapper studio")
                    || poi.getPoisType().equals("Event space")) {
                NetworkManager.getInstance().setNetworkImage(this, mPOIInfoIconCNIV, "", R.mipmap.syn_poi_clapper_theater, R.mipmap.syn_poi_information);
            }
            if (poi.getPoisType().equals("Restaurant")
                    || poi.getPoisType().equals("restaurant")
                    || poi.getPoisType().equals("Food court")
                    || poi.getPoisType().equals("cafe")) {
                NetworkManager.getInstance().setNetworkImage(this, mPOIInfoIconCNIV, "", R.mipmap.syn_poi_restaurant, R.mipmap.syn_poi_information);
            }
            mPOIInfoTitleTV.setText(marker.getTitle());
        }
    }

    private void showPOIInfo(final Marker marker) {
        Log.e("@W@", "marker getTitle : " + marker.getTitle());
        if (!TextUtils.isEmpty(marker.getTitle())) {
            final POI poi = (POI) marker.getTag();
            mCurrentSelectedPOI = poi;
            LinearLayout.LayoutParams naviTVParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            naviTVParams.setMargins(0, 0, 0, Tools.getInstance().dpToIntPx(getApplicationContext(), 16));
            naviTVParams.gravity = Gravity.CENTER;
            int iconHeight = findViewById(R.id.item_poi_header_iv_icon_fl).getHeight() / 2;
            mBottomSheetBehavior.setPeekHeight(mPOIInfoHeaderLayout.getHeight() + iconHeight);
            mPOIInfoHeaderLayout.requestLayout();
            NetworkManager.getInstance().setNetworkImage(this, mPOIInfoIconCNIV, poi.getUrlToPoisImage(), poi.getPOIIconRes(false), poi.getPOIIconRes(false));
            mPOIInfoTitleTV.setText(poi.getName());
            mPOIInfoContentTV.setText(poi.getDesc());
            NetworkManager.getInstance().setNetworkImage(this, mPOIInfoPicNIV, poi.getLogo());
        }
    }

    private void collapseBottomSheet() {
        Log.e("OKOK", "collapseBottomSheet" + mNavigationMode);
        if (mNavigationMode == NaviMode.USER_IN_NAVIGATION) {
            return;
        }
        Log.e("OKOK", "collapseBottomSheet111");
        mBottomSheetBehavior.setPeekHeight(0);
        if (mOriginalPOIMarker != null) {
            mOriginalPOIMarker = null;
        }
    }

    private void getNavigationData() {
        if (mNavigationMode == NaviMode.USER_IN_NAVIGATION && mEndPOI != null) {
            leaveNavigation();
        }

        setNavigationMode(NaviMode.USER_IN_NAVIGATION);

        if (mEndPOI == null) {
            mEndPOI = mCurrentSelectedPOI;
        }

        if (mOriginalPOIMarker != null) {
            Log.e("@W@", "mIsIndoor : " + mIsIndoor);
            if (mIsIndoor) {
                if (SynTrendText.isTestMode) {
                    getUserIndoorLocationToOutdoorPRoute(START_LOCATION.latitude, START_LOCATION.longitude,
                            mOriginalPOIMarker.getPosition().latitude, mOriginalPOIMarker.getPosition().longitude);
                } else {

                    getUserIndoorLocationToOutdoorPRoute(mUserMarker.getPosition().latitude, mUserMarker.getPosition().longitude,
                            mOriginalPOIMarker.getPosition().latitude, mOriginalPOIMarker.getPosition().longitude);
                }
            } else {

            }
        } else {
            if (SynTrendText.isTestMode || mLastLocation == null) {
                Log.e("@W@", "#1 TODOHere");
                getLocationToPRoute(START_LOCATION.latitude, START_LOCATION.longitude);
            } else {
                if (DataCacheManager.getInstance().isInBuilding(this)) {
//                    getLocationToPRoute(mUserMarker.getPosition().latitude, mUserMarker.getPosition().longitude);
                    getLocationToPRoute(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {
                    BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
                    if (floor != null && DataCacheManager.getInstance().getEntrancePOI(floor) != null) {
                        POI entrancePOI = DataCacheManager.getInstance().getEntrancePOI(floor);
                        getLocationToPRoute(entrancePOI.getLatitude(), entrancePOI.getLongitude());
                    } else {
                        getLocationToPRoute(START_LOCATION.latitude, START_LOCATION.longitude);
                    }
                }
            }
        }
        String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
        Log.e("OKOK", "userCurrentFloorPlanId : " + userCurrentFloorPlanId);
        if (mIsIndoor && !userCurrentFloorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9")) {
            fetchFloorPlan(userCurrentFloorPlanId, false, DataCacheManager.getInstance().getFloorNumberByPlanId(this, userCurrentFloorPlanId));
        } else {
            if (groundFloor != null) {
                fetchFloorPlan(groundFloor.getFloorPlanId(), false, "1");
            }
        }
    }

    private void leaveNavigation() {
        Log.e("@W@", "leaveNavigation");
        if (mNavigationMode == NaviMode.USER_IN_NAVIGATION) {

            setNavigationMode(NaviMode.NOT_NAVIGATION);

            if (mWakeLock != null)
                mWakeLock.release();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });

            mapReadyNavi = false;

            mEndPOI = null;
            /** marked this line because if start navi when in navi mode already, leaveNavigation() will lose poi info. */
            if (!getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                collapseBottomSheet();
            }


            mCurrentRouteList = null;

            if (mNaviStartMarker != null) {
                mNaviStartMarker.remove();
                mNaviStartMarker = null;
            }
            if (mNaviEndMarker != null) {
                mNaviEndMarker.remove();
                mNaviEndMarker = null;
            }

            if (mNavigationMarker != null) {
                mNavigationMarker.remove();
                mNavigationMarker = null;
            }

            DataCacheManager.getInstance().clearAllPolyline();
            DataCacheManager.getInstance().clearAllArrowMarkers();

            Map<String, List<LatLng>> routePointsMap = DataCacheManager.getInstance().getFloorRoutePointsMap();
            if (routePointsMap != null) {
                routePointsMap.clear();
            }

            Log.e("@W@", "mUserMarker == null ? " + (mUserMarker == null));

            String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
            CameraPosition cameraPosition;

            if (TextUtils.isEmpty(userCurrentFloorPlanId) ||
                    (!TextUtils.isEmpty(userCurrentFloorPlanId) && !DataCacheManager.getInstance().isInBuilding(this)) ||
                    mLastLocation == null) {

                BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
                POI entrancePOI = DataCacheManager.getInstance().getEntrancePOI(floor);

//                if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)
//                        && NavigationRoutePOIListAll != null
//                        && NavigationRoutePOIListAll.size() != 0
//                        && NavigationRoutePOIListAll.size() > NavigationRoutePOIListAllPosition) {
//                    BuildingFloor storeFloor = DataCacheManager.getInstance().getSearchFloorPlanId(
//                            SynTrendSDKActivity.this, Integer.parseInt(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getID()));
//                    fetchFloorPlan(storeFloor.getFloorPlanId(), false, NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getFloorNumber());
//                    cameraPosition = new CameraPosition.Builder(mMap.getCameraPosition())
//                            .bearing((float) 15.5)
//                            .target(new LatLng(Double.parseDouble(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLatitude()),
//                                    Double.parseDouble(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLongitude())))
//                            .zoom(SynTrendText.MAP_ZOOM_LEVEL)
//                            .build();
//                } else {
                fetchFloorPlan(floor.getFloorPlanId(), false, floor.getNumber());
                cameraPosition = new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing((float) 15.5)
                        .target(new LatLng(entrancePOI.getLatitude(), entrancePOI.getLongitude()))
                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                        .build();
//                }

            } else {
                fetchFloorPlan(userCurrentFloorPlanId, true, DataCacheManager.getInstance().getFloorNumberByPlanId(this, userCurrentFloorPlanId));
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
//                        .bearing(mUserMarker.getRotation())
                        .bearing((float) 15.5)
                        .tilt(0)
                        .build();
            }

            if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)
                    && NavigationRoutePOIListAll != null
                    && NavigationRoutePOIListAll.size() != 0
                    && NavigationRoutePOIListAll.size() > NavigationRoutePOIListAllPosition) {
                BuildingFloor storeFloor = DataCacheManager.getInstance().getSearchFloorPlanId(
                        SynTrendSDKActivity.this, Integer.parseInt(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getID()));
                fetchFloorPlan(storeFloor.getFloorPlanId(), false, NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getFloorNumber());
                cameraPosition = new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing((float) 15.5)
                        .target(new LatLng(Double.parseDouble(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLatitude()),
                                Double.parseDouble(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLongitude())))
                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                        .build();
            }

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void getUserIndoorLocationToOutdoorPRoute(double userLat, double userLng,
                                                      double poiLat, double poiLng) {
        LocationApi.getInstance().getUserIndoorLocationToOutdoorPRoute(this,
                DataCacheManager.getInstance().getUserCurrentBuildingId(this),
                DataCacheManager.getInstance().getUserCurrentFloorLevel(this),
                userLat, userLng, poiLat, poiLng,
                mNavigationType == NavigationType.ACCESSIBLE ? "elevator" : "normal",
                new NetworkManager.NetworkManagerListener<NavigationRoutePOI[]>() {
                    @Override
                    public void onSucceed(NavigationRoutePOI[] routePOIs) {
                        if (routePOIs.length != 0) {
                            startNavigation(Arrays.asList(routePOIs));
                        } else {
                            DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this, R.string.error_dialog_title_text_normal, R.string.dialog_message_route_empty);
                        }
                    }

                    @Override
                    public void onFail(String errorMsg, boolean shouldRetry) {
                        DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
                                R.string.error_dialog_title_text_normal,
                                R.string.error_dialog_title_text_json_parse_error);
                    }
                }
        );
    }

    private void getLocationToPRoute(double startLat, double startLng) {
        if (mEndPOI != null) {
            EndPOIId = String.valueOf(mEndPOI.getId());
        }
        Log.e("OKOK", "EndPOIId" + EndPOIId);
        LocationApi.getInstance().getUserLocationToIndoorPRoute(this,
                EndPOIId,
                startLat,
                startLng,
                DataCacheManager.getInstance().getUserCurrentFloorLevel(this),
                mNavigationType == NavigationType.ACCESSIBLE ? "elevator" : "normal",
                new NetworkManager.NetworkManagerListener<NavigationRoutePOI[]>() {
                    @Override
                    public void onSucceed(NavigationRoutePOI[] routePOIs) {
                        if (routePOIs.length != 0) {
                            startNavigation(Arrays.asList(routePOIs));
                        } else {
                            DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this, R.string.error_dialog_title_text_normal, R.string.dialog_message_route_empty);
                        }
                    }

                    @Override
                    public void onFail(final String errorMsg, boolean shouldRetry) {
                        Log.e("@W@", "#3");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
//                                        R.string.error_dialog_title_text_normal,
//                                        errorMsg);
                            }
                        });
                    }
                });
    }

    private void showUserPosition() {
        Log.e("OKOK", "showUserPosition");
        if (!mIsMapInited) {
            mIsMapInited = true;
            mMapFragment.getMapAsync(this);
        }
        if (mLastLocation != null) {
            LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            addUserMarker(current, mLastLocation);
        }
    }

    private void checkMapInit() {
        if (!mIsMapInited) {
            mEventBus.post(new OmniEvent(OmniEvent.TYPE_REQUEST_LAST_LOCATION, ""));
        }
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Log.e("OKOK", "onInfoWindowClose");
        if (marker.getTag() == null) {
            return;
        }
        if (!getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
//            POI poi = (POI) marker.getTag();
            marker.setIcon(BitmapDescriptorFactory.fromResource(((POI) marker.getTag()).getPOIIconRes(false)));
            collapseBottomSheet();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("OKOK", "onMapReady");
//
//        if (mapReady) {
//            return;
//        } else {
//            mapReady = true;
//        }

        mMap = googleMap;
        mMap.setOnInfoWindowCloseListener(this);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mClusterManager != null) {
                    mClusterManager.cluster();
                }
                refreshMapTextMarkers((int) mMap.getCameraPosition().zoom);
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json));

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                .bearing((float) 15.5)
                .target(START_LOCATION)
                .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                .build()));
        groundFloor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
        if (groundFloor != null) {
            fetchFloorPlan(groundFloor.getFloorPlanId(), false, "1");
        }

        String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
        Log.e("OKOK", "userCurrentFloorPlanId" + userCurrentFloorPlanId);
        setupClusterManager();

        if (getIntent().getExtras().containsKey(ARG_KEY_FACILITY_TYPE)) {

            if (mLastLocation != null) {
                fetchFloorPlan(userCurrentFloorPlanId, true, DataCacheManager.getInstance().getFloorNumberByPlanId(this, userCurrentFloorPlanId));
                LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                addUserMarker(current, mLastLocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, SynTrendText.MAP_ZOOM_LEVEL));
            }

            searchIcon.setVisibility(View.GONE);
            categoryLayout.setVisibility(View.GONE);
            mNaviInfoIconCNIV.setVisibility(View.GONE);
            mNaviInfoTitleTV.setText(facility_title);

            DialogTools.getInstance().showLocationProgress(this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getEmergencyRoute(facility_type);
                }
            }, 7000);

        } else if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {

            if (mLastLocation != null) {
                fetchFloorPlan(userCurrentFloorPlanId, true, DataCacheManager.getInstance().getFloorNumberByPlanId(this, userCurrentFloorPlanId));
                LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                addUserMarker(current, mLastLocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, SynTrendText.MAP_ZOOM_LEVEL));
            } else {
                mEventBus.post(new OmniEvent(OmniEvent.TYPE_REQUEST_LAST_LOCATION, ""));
            }

//            menuIconLayout.setVisibility(View.VISIBLE);
            menuIcon.setVisibility(View.VISIBLE);
            findViewById(R.id.poi_info_header_view_tv_list).setVisibility(View.VISIBLE);
            searchIcon.setVisibility(View.GONE);
            categoryLayout.setVisibility(View.GONE);
            mNaviInfoIconCNIV.setVisibility(View.GONE);
            mNaviInfoTitleTV.setText(facility_title);

            DialogTools.getInstance().showLocationProgress(this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getRecommendRoute(route_custom, route_recommend);
                }
            }, 7000);

            if (naviDirect) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNavigationData();
                    }
                }, 9000);
            }

        } else {

            if (TextUtils.isEmpty(userCurrentFloorPlanId) ||
                    (!TextUtils.isEmpty(userCurrentFloorPlanId) && !DataCacheManager.getInstance().isInBuilding(this))) {

                BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
                if (floor != null) {
                    POI entrancePOI = DataCacheManager.getInstance().getEntrancePOI(floor);
                    fetchFloorPlan(floor.getFloorPlanId(), false, floor.getNumber());

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                            .bearing((float) 15.5)
                            .target(new LatLng(floor.getLatitude(), floor.getLongitude()))
                            .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                            .build()));
                }
            } else {
                if (mLastLocation != null) {
                    fetchFloorPlan(userCurrentFloorPlanId, true, DataCacheManager.getInstance().getFloorNumberByPlanId(this, userCurrentFloorPlanId));
                    LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    addUserMarker(current, mLastLocation);
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, SynTrendText.MAP_ZOOM_LEVEL));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                            .bearing((float) 15.5)
                            .target(current)
                            .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                            .build()));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                            .bearing((float) 15.5)
                            .target(START_LOCATION)
                            .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                            .build()));
                }
            }
        }

        groundFloor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);

        if (guide_category != null) {
            switch (guide_category) {
                case "category_all":
                case "category_store":
                    selectStore();
                    break;
                case "category_restaurant":
                    selectRestaurant();
                    break;
                case "category_theater":
                    selectTheater();
                    break;
                case "category_facility":
                    selectFacility();
                    break;
            }
        }
    }

    private void setupClusterManager() {
        Log.e("OKOK", " setupClusterManager()");
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<OmniClusterItem>(this, mMap);
        }
        if (mOmniClusterRender == null) {
            mOmniClusterRender = new OmniClusterRender(this, mMap, mClusterManager);
        }
        mClusterManager.setRenderer(mOmniClusterRender);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<OmniClusterItem>() {
            @Override
            public boolean onClusterItemClick(OmniClusterItem omniClusterItem) {
                if (mNavigationMode == NaviMode.USER_IN_NAVIGATION) {
                    return true;
                }

                Marker marker = mOmniClusterRender.getMarker(omniClusterItem);
                if (marker.getTag() == null) {
                    marker.setTag(omniClusterItem.getPOI());
                }

                if (TextUtils.isEmpty(marker.getTitle()) || omniClusterItem.getPOI().getType().contains("Z")) {
                    return true;
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(omniClusterItem.getPOI().getPOIIconRes(true)));
                    showPOIInfo(marker);
                    return false;
                }
            }
        });
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<OmniClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<OmniClusterItem> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(),
                        (float) Math.floor(mMap.getCameraPosition().zoom + 1)),
                        300,
                        null);
                return true;
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    private void fetchFloorPlan(final String id, final boolean isEnterRegion, final String floorLevel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchFloorPlan(id, isEnterRegion, false, floorLevel);
            }
        });
    }


    private void fetchFloorPlan(final String id, final boolean isEnterRegion,
                                final boolean hasNavi, final String floorLevel) {
        Log.e("OKOK", "fetchFloorPlan" + id);
        if (!NetworkManager.getInstance().isNetworkAvailable(this)) {
//            DialogTools.getInstance().dismissProgress(this);
            Log.e("@W@", "--- fetchFloorPlan show no network");
            DialogTools.getInstance().showNoNetworkMessage(this);
            return;
        }
        if (TextUtils.isEmpty(id)) {
            DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this, "Loading building map error", "There's no floor plan id !");
            return;
        }
//        cancelPendingNetworkCalls();

//        if (mIAResourceManager == null) {
//            mIAResourceManager = IAResourceManager.create(this);
//        }
//        mPendingAsyncResult = mIAResourceManager.fetchFloorPlanWithId(id);
//        if (mPendingAsyncResult != null) {
//            Log.e("OKOK", "mPendingAsyncResult");
//            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
//                @Override
//                public void onResult(IAResult<IAFloorPlan> iaResult) {
//                    Log.e("OKOK", "onResult" + iaResult.getResult());
//                    final IAFloorPlan floorPlan = iaResult.getResult();
//                    if (iaResult.isSuccess() && floorPlan != null) {

        DataCacheManager.getInstance().setCurrentShowFloor(new OmniFloor(floorLevel, id));
        Log.e("@W@", "fetchFloorPlan floorLevel : " + DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel() +
                ", floorPlanId : " + DataCacheManager.getInstance().getCurrentShowFloor().getFloorPlanId());
        Log.e("@W@", "getUserCurrentFloorLevel floorLevel : " + DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFloorLevelTV.setText((floorLevel.contains("-") ? floorLevel.replace("-", "B") : floorLevel + "F"));
                mFloorLevelTV.setVisibility(View.VISIBLE);
            }
        });

        if (isEnterRegion) {
            DataCacheManager.getInstance().setUserCurrentFloorLevel(String.valueOf(floorLevel));
            DataCacheManager.getInstance().setUserCurrentFloorPlanId(id);
        }

        if (mMap != null) {
            NavigationRoutePOI naviStartPOI = null;
            NavigationRoutePOI naviEndPOI = null;
            Log.e("@W@", "mNaviStartMarker == null : " + (mNaviStartMarker == null) +
                    ", mNaviEndMarker == null : " + (mNaviEndMarker == null));
            if (mNaviStartMarker != null) {
                naviStartPOI = (NavigationRoutePOI) mNaviStartMarker.getTag();
            }
            if (mNaviEndMarker != null) {
                naviEndPOI = (NavigationRoutePOI) mNaviEndMarker.getTag();
            }
            mMap.clear();
            if (mUserMarker != null) {
                mUserMarker.remove();
                mUserMarker = null;
            }
            if (mLastLocation != null) {
                if (DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
                        .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this))) {
                    addUserMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                            mLastLocation);
                }
            }
            if (naviStartPOI != null && mNaviStartMarker != null) {
                mNaviStartMarker.setTag(naviStartPOI);
            }
            if (naviEndPOI != null && mNaviEndMarker != null) {
                mNaviEndMarker.setTag(naviEndPOI);
                mNaviEndMarker.setVisible(naviStartPOI.getFloorNumber().equals(naviEndPOI.getFloorNumber()));
            }

            TileProvider tileProvider = new UrlTileProvider(SynTrendText.TILE_WIDTH, SynTrendText.TILE_HEIGHT) {
                @Override
                public URL getTileUrl(int x, int y, int zoom) {
                    String s = String.format(NetworkManager.DOMAIN_NAME + "map/tile/%s/%d/%d/%d.png",
                            id, zoom, x, y);

                    if (!checkTileExists(x, y, zoom)) {
                        return null;
                    }

                    try {
                        return new URL(s);
                    } catch (MalformedURLException e) {
                        Log.e("@W@", "getTileUrl get exception message === " + e.getMessage() +
                                "\ncause === " + e.getCause() + "\nlocalizedMessage === " + e.getLocalizedMessage());
                        throw new AssertionError(e);
                    }
                }
            };

            String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(SynTrendSDKActivity.this, id);

            if (mTileOverlayMap != null) {
                // when floor changed and in the same building, remove tile overlay
                TileOverlay previousTile = mTileOverlayMap.get(buildingId);
                if (previousTile != null) {
                    previousTile.remove();
                    previousTile.clearTileCache();
                }
            } else {
                mTileOverlayMap = new HashMap<>();
            }

            // add current floor tile overlay
            TileOverlay tile = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
            mTileOverlayMap.put(buildingId, tile);

            if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A) && routePOIList != null) {
                drawStoreNavigation(routePOIList);
            }

            Log.e("@W@", "hasNavi : " + hasNavi + ", mCurrentRouteList = null : " + (mCurrentRouteList == null));
            if (hasNavi || mCurrentRouteList != null) {
//                                if (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE)) {
//
//                                    BookNavigationRoute route = (BookNavigationRoute) getArguments().getSerializable(ARG_KEY_BOOK_NAVIGATION_ROUTE);
//                                    if (route.getRoutePOIs().length != 0) {
//                                        startNavigation(Arrays.asList(route.getRoutePOIs()));
//
//                                    } else {
//                                        DialogTools.getInstance().dismissProgress(SynTrendSDKActivity.this);
//                                        DialogTools.getInstance().showErrorMessage(this, getString(R.string.error_dialog_title_text_normal), getString(R.string.dialog_message_bookshelf_route_not_found));
//                                    }
//                                } else if (getArguments().containsKey(ARG_KEY_CAR_ROUTES)) {
//
//                                    NavigationRoutePOI[] routePOIS = (NavigationRoutePOI[]) getArguments().getSerializable(ARG_KEY_CAR_ROUTES);
//
//                                    if (routePOIS.length != 0) {
//                                        startNavigation(Arrays.asList(routePOIS));
//
//                                    } else {
//                                        DialogTools.getInstance().dismissProgress(getApplication());
//                                        DialogTools.getInstance().showErrorMessage(this, getString(R.string.error_dialog_title_text_normal), getString(R.string.dialog_message_bookshelf_route_not_found));
//                                    }
//
//                                } else {
                if (!DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
                        .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this))) {
                    mapReadyNavi = false;
                }
                startNavigation(mCurrentRouteList);
//                                }
            }
            addPOIMarkers(buildingId, id, current_cate);

            // show the floor's route poly line
            if (mNavigationMode != NaviMode.NOT_NAVIGATION) {
                showPolylineByFloorNumber(String.valueOf(floorLevel));
            }

            String userCurrentFloorLevel = DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this);
            if (mUserMarker != null) {
                mUserMarker.setVisible(userCurrentFloorLevel.equals(String.valueOf(floorLevel)));
            }
            if (mUserAccuracyCircle != null) {
                mUserAccuracyCircle.setVisible(userCurrentFloorLevel.equals(String.valueOf(floorLevel)));
            }

//            }
//                    }
//                }
//            }, Looper.getMainLooper());
        }
    }

    private void initRoutePOIList(List<NavigationRoutePOI> routes) {
        NavigationRoutePOIListA = new ArrayList<>();
        NavigationRoutePOIListB = new ArrayList<>();
        NavigationRoutePOIListAll = new ArrayList<>();

        for (final NavigationRoutePOI routePOI : routes) {
            if ((routePOI.getPoisType().equalsIgnoreCase("store")
                    || routePOI.getPoisType().equalsIgnoreCase("Information")
                    || routePOI.getPoisType().equalsIgnoreCase("Clapper Theater")
                    || routePOI.getPoisType().equalsIgnoreCase("Clapper studio")
                    || routePOI.getPoisType().equalsIgnoreCase("Event space")
                    || routePOI.getPoisType().equalsIgnoreCase("Restaurant")
                    || routePOI.getPoisType().equalsIgnoreCase("restaurant")
                    || routePOI.getPoisType().equalsIgnoreCase("Food court")
                    || routePOI.getPoisType().equalsIgnoreCase("cafe"))
                    && routePOI.getStoreID().length() != 0) {
                if (routePOI.getSelected().equals("a")) {
                    NavigationRoutePOIListA.add(routePOI);
                } else if (routePOI.getSelected().equals("b")) {
                    NavigationRoutePOIListB.add(routePOI);
                }
                NavigationRoutePOIListAll.add(routePOI);
            }
        }

        route_custom = "";
        for (NavigationRoutePOI poi : NavigationRoutePOIListA) {
            route_custom = route_custom + poi.getStoreID() + ",";
        }
        Log.e("OKOK", "route_custom" + route_custom);
        PreferencesTools.getInstance().saveProperty(this, PreferencesTools.KEY_STORE_ROUTE_A, route_custom);

        route_recommend = "";
        for (NavigationRoutePOI poi : NavigationRoutePOIListB) {
            route_recommend = route_recommend + poi.getStoreID() + ",";
        }
        Log.e("OKOK", "route_recommend" + route_recommend);
        PreferencesTools.getInstance().saveProperty(this, PreferencesTools.KEY_STORE_ROUTE_B, route_recommend);
    }

    private void drawStoreNavigation(List<NavigationRoutePOI> routes) {
        Log.e("OKOK", "drawStoreNavigation");
        NavigationRoutePOI previousPOI = null;
        List<LatLng> pointList = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {

            NavigationRoutePOI poi = routes.get(i);
            LatLng point = new LatLng(Double.valueOf(poi.getLatitude()), Double.valueOf(poi.getLongitude()));

            if (previousPOI == null || poi.getFloorNumber().equals(previousPOI.getFloorNumber())) {
                pointList.add(point);
                if (i == routes.size() - 1) {
                    drawPolyline(poi.getFloorNumber(), pointList, getResources().getColor(R.color.blue_41), SynTrendText.POLYLINE_WIDTH);
                }
            } else {
                drawPolyline(previousPOI.getFloorNumber(), pointList, getResources().getColor(R.color.blue_41), SynTrendText.POLYLINE_WIDTH);
                pointList.clear();
                pointList.add(point);
            }
            previousPOI = poi;
        }

//        NavigationRoutePOIListA = new ArrayList<>();
//        NavigationRoutePOIListB = new ArrayList<>();
//        NavigationRoutePOIListAll = new ArrayList<>();
        MarkerList = new ArrayList<>();
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }

        for (final NavigationRoutePOI routePOI : routes) {
            if ((routePOI.getPoisType().equalsIgnoreCase("store")
                    || routePOI.getPoisType().equalsIgnoreCase("Information")
                    || routePOI.getPoisType().equalsIgnoreCase("Clapper Theater")
                    || routePOI.getPoisType().equalsIgnoreCase("Clapper studio")
                    || routePOI.getPoisType().equalsIgnoreCase("Event space")
                    || routePOI.getPoisType().equalsIgnoreCase("Restaurant")
                    || routePOI.getPoisType().equalsIgnoreCase("restaurant")
                    || routePOI.getPoisType().equalsIgnoreCase("Food court")
                    || routePOI.getPoisType().equalsIgnoreCase("cafe"))
                    && routePOI.getStoreID().length() != 0) {
                if (routePOI.getSelected().equals("a")) {
//                    NavigationRoutePOIListA.add(routePOI);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Marker storeMarker;
                            storeMarker = mMap.addMarker(new MarkerOptions()
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_terminal_point))
                                    .position(routePOI.getLocation())
                                    .zIndex(SynTrendText.MARKER_Z_INDEX));
                            storeMarker.setTag(routePOI);
                            storeMarker.setTitle(routePOI.getName());
                            storeMarker.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel().equals(routePOI.getFloorNumber()));
                            MarkerList.add(storeMarker);
                        }
                    });
                } else if (routePOI.getSelected().equals("b")) {
//                    NavigationRoutePOIListB.add(routePOI);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Marker storeMarker;
                            storeMarker = mMap.addMarker(new MarkerOptions()
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_terminal_point_yellow))
                                    .position(routePOI.getLocation())
                                    .zIndex(SynTrendText.MARKER_Z_INDEX));
                            storeMarker.setTag(routePOI);
                            storeMarker.setTitle(routePOI.getName());
                            storeMarker.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel().equals(routePOI.getFloorNumber()));
                            MarkerList.add(storeMarker);
                        }
                    });
                }
//                NavigationRoutePOIListAll.add(routePOI);
                menuList.add(routePOI.getName());
            }
        }

//        if (initRoute) {
//            initRoute = false;
//            route_custom = "";
//            for (NavigationRoutePOI poi : NavigationRoutePOIListA) {
//                route_custom = route_custom + poi.getStoreID() + ",";
//            }
//            PreferencesTools.getInstance().saveProperty(this, PreferencesTools.KEY_STORE_ROUTE_A, route_custom);
//
//            route_recommend = "";
//            for (NavigationRoutePOI poi : NavigationRoutePOIListB) {
//                route_recommend = route_recommend + poi.getStoreID() + ",";
//            }
//            PreferencesTools.getInstance().saveProperty(this, PreferencesTools.KEY_STORE_ROUTE_B, route_recommend);
//        }
        Log.e("OKOK", "NavigationRoutePOIListAllPosition" + NavigationRoutePOIListAllPosition);
        Log.e("OKOK", "NavigationRoutePOIListAll.size()" + NavigationRoutePOIListAll.size());
        if (NavigationRoutePOIListAll.size() != 0 && NavigationRoutePOIListAllPosition < NavigationRoutePOIListAll.size()) {
            Log.e("OKOK", "NavigationRoutePOIListAllPosition111111");
            EndPOIId = NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getID();
            mNaviInfoIconCNIV.setVisibility(View.VISIBLE);
            NetworkManager.getInstance().setNetworkImage(this, mNaviInfoIconCNIV, "", R.mipmap.syn_poi_store, R.mipmap.syn_poi_information);
            mNaviInfoTitleTV.setText(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getName());
            mPOIInfoContentTV.setText(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getDesc());
            Log.e("OKOK", "NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getDesc()" +
                    NavigationRoutePOIListAll.get(0).getDesc());
            NetworkManager.getInstance().setNetworkImage(this, mPOIInfoPicNIV,
                    NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLogo());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MarkerList.get(MarkerListPosition).showInfoWindow();
//                MarkerList.get(0).setIcon(BitmapDescriptorFactory.fromResource(poi.getPOIIconRes(true)));
                    showStoreNaviPOIInfo(MarkerList.get(MarkerListPosition));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                            .bearing((float) 15.5)
                            .target(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getLocation())
                            .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                            .build()));
                }
            });
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void startNavigation(final List<NavigationRoutePOI> routes) {
        Log.e("@W@", "startNavigation");

        setNavigationMode(NaviMode.USER_IN_NAVIGATION);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeLock");
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        mCurrentRouteList = routes;

        if (!mapReadyNavi) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
                                    .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this))) {
                                mapReadyNavi = true;
                            }
                        }
                    }, 2000);
                }
            });
        }

//        if (!getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataCacheManager.getInstance().clearAllPolyline();
                DataCacheManager.getInstance().clearAllArrowMarkers();
            }
        });
//        }

        List<LatLng> pointList = new ArrayList<>();
//        List<LatLng> pointList = DataCacheManager.getInstance().getCurrentRoutePointList();
//        if (!pointList.isEmpty()) {
//            pointList.clear();
//        }
        NavigationRoutePOI previousPOI = null;
        int colorIndex = 0;

        for (int i = 0; i < routes.size(); i++) {

            NavigationRoutePOI poi = routes.get(i);
            LatLng point = new LatLng(Double.valueOf(poi.getLatitude()), Double.valueOf(poi.getLongitude()));

            if (previousPOI == null || poi.getFloorNumber().equals(previousPOI.getFloorNumber())) {
                pointList.add(point);
                if (i == routes.size() - 1) {
//                    drawPolyline(poi.getFloorNumber(), pointList, this.getResources().getColor(R.color.red), SynTrendText.POLYLINE_WIDTH);
                    drawPolyline(poi.getFloorNumber(), pointList, this.getResources().getColor(R.color.blue_25), SynTrendText.POLYLINE_WIDTH);
                }
            } else {
//                drawPolyline(previousPOI.getFloorNumber(), pointList, this.getResources().getColor(R.color.red), SynTrendText.POLYLINE_WIDTH);
                drawPolyline(previousPOI.getFloorNumber(), pointList, this.getResources().getColor(R.color.blue_25), SynTrendText.POLYLINE_WIDTH);
                pointList.clear();
                pointList.add(point);
//                colorIndex++;
            }
            previousPOI = poi;
        }

//                mRouteInfoLL.setVisibility(View.VISIBLE);
//                mSearchTV.setVisibility(View.GONE);
//        DataCacheManager.getInstance().setCurrentRoutePointList(pointList);

        final NavigationRoutePOI firstRoutePOI = routes.get(0);
        final NavigationRoutePOI lastRoutePOI = routes.get(routes.size() - 1);
        POI p = DataCacheManager.getInstance().getMainLibraryEntrancePOI(this);
        int cameraBearing = 0;
        if (firstRoutePOI.getLatitude().contains(String.valueOf(p.getLatitude())) &&
                firstRoutePOI.getLongitude().contains(String.valueOf(p.getLongitude()))) {
            cameraBearing = 0;
        }

        final int finalCameraBearing = cameraBearing;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing((float) 15.5)
                        .target(firstRoutePOI.getLocation())
                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                        .build()));

                mNaviStartMarker = mMap.addMarker(new MarkerOptions()
                        .flat(false)
//                .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_start_point))
                        .position(firstRoutePOI.getLocation())
                        .zIndex(SynTrendText.MARKER_Z_INDEX));
                Log.e("@W@", "mNaviStartMarker set tag firstRoutePOI == null : " + (firstRoutePOI == null));
                mNaviStartMarker.setTag(firstRoutePOI);
//        addNaviStartOrEndMarker(mNaviStartMarker, firstRoutePOI);

                if (!getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
                    mNaviEndMarker = mMap.addMarker(new MarkerOptions()
                            .flat(false)
//                .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_terminal_point_red))
                            .position(lastRoutePOI.getLocation())
                            .zIndex(SynTrendText.MARKER_Z_INDEX));

                    mNaviEndMarker.setTag(lastRoutePOI);
//        addNaviStartOrEndMarker(mNaviEndMarker, lastRoutePOI);
//        if (mNaviEndMarker != null) {
                    mNaviEndMarker.setVisible(firstRoutePOI.getFloorNumber().equals(lastRoutePOI.getFloorNumber()));
                }
//        }

                List<LatLng> userFloorPointList = DataCacheManager.getInstance().getUserCurrentFloorRoutePointList(SynTrendSDKActivity.this);
                if (userFloorPointList != null) {
//            if (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE)) {
//                BookNavigationRoute navigationRoute = (BookNavigationRoute) getArguments().getSerializable(ARG_KEY_BOOK_NAVIGATION_ROUTE);
//                if (!DataCacheManager.getInstance().isInSameBuilding(this, navigationRoute.getBuildingId() + "")) {
//                    return;
//                }
//            }
                    Log.e("@W@", "mIsIndoor" + mIsIndoor);
                    String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
                    if (mIsIndoor && !userCurrentFloorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9")) {
//                        moveCameraByUserLocation(userFloorPointList);
                    } else {
//                        Log.e("OKOK", "mNavigationMarker");
//                        mNavigationMarker = mMap.addMarker(new MarkerOptions()
//                                .position(firstRoutePOI.getLocation())
//                                .rotation((float) SphericalUtil.computeHeading(routes.get(1).getLocation(), routes.get(2).getLocation()))
//                                .flat(true)
//                                .zIndex(SynTrendText.MARKER_Z_INDEX)
//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_navigation_start)));
//                        mNavigationMarker.setVisible(true);
                    }
                }
            }
        });
    }

    private void setNavigationMode(int navigationMode) {
        mNavigationMode = navigationMode;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mNavigationMode == NaviMode.USER_IN_NAVIGATION) {
//                    mPOIInfoLayout.setVisibility(View.GONE);
                    if (getIntent().getExtras().containsKey(ARG_KEY_FACILITY_TYPE)) {
                        mNaviInfoRL.setVisibility(View.VISIBLE);
                    }
                    headerNaviTV.setText(R.string.map_page_stop_navi);
//                    headerNaviTV.setTextColor(getResources().getColor(R.color.red_91));
//                    headerNaviTV.setBackgroundResource(R.mipmap.rectangle_copy_7);
                    headerNaviTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveNavigation();
                        }
                    });
                    headerNaviIV.setImageResource(R.mipmap.stop_nav);
                    headerNaviIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveNavigation();
                        }
                    });
                    if (mEndPOI != null) {
                        NetworkManager.getInstance().setNetworkImage(SynTrendSDKActivity.this,
                                mNaviInfoIconCNIV,
                                mEndPOI.getUrlToPoisImage(),
                                mEndPOI.getPOIIconRes(false),
                                mEndPOI.getPOIIconRes(false));
                        mNaviInfoTitleTV.setText(mEndPOI.getName());
                    }
//            mEventBus.post(new OmniEvent(OmniEvent.TYPE_NAVIGATION_MODE_CHANGED, OmniEvent.EVENT_CONTENT_USER_IN_NAVIGATION));
                } else {
//                    mPOIInfoLayout.setVisibility(View.VISIBLE);
                    if (getIntent().getExtras().containsKey(ARG_KEY_FACILITY_TYPE)) {
                        mNaviInfoRL.setVisibility(View.GONE);
                    }
                    headerNaviTV.setText(R.string.map_page_start_navi);
//                    headerNaviTV.setTextColor(getResources().getColor(android.R.color.white));
//                    headerNaviTV.setBackgroundResource(R.drawable.solid_round_rectangle_navi_blue);
                    headerNaviTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getNavigationData();
                        }
                    });
                    headerNaviIV.setImageResource(R.mipmap.start_nav);
                    headerNaviIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getNavigationData();
                        }
                    });
//            mEventBus.post(new OmniEvent(OmniEvent.TYPE_NAVIGATION_MODE_CHANGED, OmniEvent.EVENT_CONTENT_NOT_NAVIGATION));
                }
            }
        });
    }

    private void addUserMarker(LatLng position, Location location) {
        if (mMap == null) {
            return;
        }

        if (!DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
                .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this))) {
            return;
        }

        if (mUserMarker == null) {
            Log.e("@W@", "addUserMarker mUserMarker");
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .rotation(location.getBearing())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .zIndex(SynTrendText.MARKER_Z_INDEX));

            mUserAccuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(location.getAccuracy() / 2)
                    .strokeColor(ContextCompat.getColor(this, R.color.map_circle_stroke_color))
                    .fillColor(ContextCompat.getColor(this, R.color.map_circle_fill_color))
                    .strokeWidth(5)
                    .zIndex(SynTrendText.MARKER_Z_INDEX));
        } else {
            mUserMarker.setPosition(position);
            mUserMarker.setRotation(location.getBearing());

            mUserAccuracyCircle.setCenter(position);
            mUserAccuracyCircle.setRadius(location.getAccuracy() / 2);
        }

        if (mNavigationMode == NaviMode.USER_IN_NAVIGATION) {
//            if (!DataCacheManager.getInstance().isInBuilding(this) ||
//                    (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE) && !getArguments().getBoolean(ARG_KEY_NAVIGATION_IS_USER_IN_BUILDING))) {
//                return;
//            }
            final List<LatLng> pointList = DataCacheManager.getInstance().getUserCurrentFloorRoutePointList(this);
            Log.e("@W@", "pointList == null ? " + (pointList == null));
            if (pointList != null) {
//                if (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE)) {
//                    BookNavigationRoute navigationRoute = (BookNavigationRoute) getArguments().getSerializable(ARG_KEY_BOOK_NAVIGATION_ROUTE);
//                    Log.e("@W@", "isInSameBuilding : " + DataCacheManager.getInstance().isInSameBuilding(this, navigationRoute.getBuildingId() + ""));
//                    if (!DataCacheManager.getInstance().isInSameBuilding(this, navigationRoute.getBuildingId() + "")) {
//                        return;
//                    }
//                }
                String userCurrentFloorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
                Log.e("OKOK", "mapReadyNavi" + mapReadyNavi);
                if (mIsIndoor && !userCurrentFloorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9") && mapReadyNavi) {
                    moveCameraByUserLocation(pointList);
                }
            }
        }
    }

    private void addPOIMarkers(final String buildingId, String floorPlanId, int type) {
        Log.e("OKOK", "addPOIMarkers" + buildingId + floorPlanId);
        BuildingFloor floor = DataCacheManager.getInstance().getBuildingFloor(this, buildingId, floorPlanId);
        if (floor != null) {

            if (getIntent().getExtras().containsKey(ARG_KEY_GUIDE_CATEGORY)) {
                removePreviousMarkers(buildingId);

                OmniClusterItem item;
                itemList = DataCacheManager.getInstance().getClusterListByBuildingId(buildingId);
                if (itemList == null) {
                    itemList = new ArrayList<>();
                }
                if (menuList == null) {
                    menuList = new ArrayList<>();
                } else {
                    menuList.clear();
                }

                // add floor markers
                for (POI poi : floor.getPois()) {
                    if (!poi.getType().equals("map_text")) {
                        item = new OmniClusterItem(poi);
                        if (type == STORE_CATE) {
                            if (poi.getType().equalsIgnoreCase("store")) {
                                itemList.add(item);
                                menuList.add(item.getTitle());
                                DataCacheManager.getInstance().setPOIClusterItemMap(poi.getId() + "", item);
                            }
                        } else if (type == THEATER_CATE) {
                            if (poi.getType().equals("Clapper Theater")
                                    || poi.getType().equals("Clapper studio")
                                    || poi.getType().equals("Event space")) {
                                itemList.add(item);
                                menuList.add(item.getTitle());
                                DataCacheManager.getInstance().setPOIClusterItemMap(poi.getId() + "", item);
                            }
                        } else if (type == RESTAURANT_CATE) {
                            if (poi.getType().equals("Restaurant")
                                    || poi.getType().equals("restaurant")
                                    || poi.getType().equals("Food court")
                                    || poi.getType().equals("cafe")) {
                                itemList.add(item);
                                menuList.add(item.getTitle());
                                DataCacheManager.getInstance().setPOIClusterItemMap(poi.getId() + "", item);
                            }
                        } else if (type == FACILITY_CATE) {
                            if (!poi.getType().equalsIgnoreCase("store")
                                    && !poi.getType().equals("Clapper Theater")
                                    && !poi.getType().equals("Clapper studio")
                                    && !poi.getType().equals("Event space")
                                    && !poi.getType().equals("Restaurant")
                                    && !poi.getType().equals("restaurant")
                                    && !poi.getType().equals("Food court")
                                    && !poi.getType().equals("cafe")) {
                                itemList.add(item);
                                menuList.add(item.getTitle());
                                DataCacheManager.getInstance().setPOIClusterItemMap(poi.getId() + "", item);
                            }
                        }
                    }
                }
                DataCacheManager.getInstance().setBuildingClusterItems(buildingId, itemList);

                if (mClusterManager != null) {
                    mClusterManager.addItems(itemList);
                    mClusterManager.cluster();
                }
            }

            final BuildingFloor[] floors = DataCacheManager.getInstance().getFloorsByBuildingId(this, buildingId);
//            mFloorsLayout.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Tools.getInstance().dpToIntPx(this, 40),
                    Tools.getInstance().dpToIntPx(this, 40));
//            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(Tools.getInstance().dpToIntPx(this, 40),
//                    Tools.getInstance().dpToIntPx(this, 1));

            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = floors.length - 1; i >= 0; i--) {
                final BuildingFloor f = floors[i];

                String floorName = f.getName();
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.item_view_floor_selector_floor, null, false);
//                textView.setLines(2);
//                textView.setGravity(Gravity.CENTER);
//                textView.setText(floorName);
                ((TextView) linearLayout.findViewById(R.id.item_view_floor_selector_floor_name)).setText(floorName);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(f.getFloorPlanId())) {
//                            if (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE)) {
//                                fetchFloorPlan(f.getFloorPlanId(), false, true, f.getNumber());
//                            } else {
                            fetchFloorPlan(f.getFloorPlanId(), false, f.getNumber());
//                            }

//                            for (int i = 0; i < mFloorsLayout.getChildCount(); i++) {
//                                mFloorsLayout.getChildAt(i).setBackgroundColor(Tools.getInstance().getColor(getApplication(), android.R.color.white));
//                            }
//
//                            v.setBackgroundColor(Tools.getInstance().getColor(SynTrendSDKActivity.this, R.color.olive_green));
//                            mFloorsLayout.setVisibility(View.GONE);
                        } else {
                            DialogTools.getInstance().showErrorMessage(getApplication(), R.string.error_dialog_title_text_normal,
                                    "There's no building " + buildingId + "'s floor " + f.getName() + " map.");
                        }
                    }
                });

//                if (i != floors.length - 1) {
//                    View dividerView = new View(this);
//                    dividerView.setBackgroundColor(Tools.getInstance().getColor(this, R.color.gray_e4));
//                    mFloorsLayout.addView(dividerView, dividerParams);
//                }

//                mFloorsLayout.addView(linearLayout, params);
            }

            FloorAdapter adapter = new FloorAdapter(this);
            GridView grid = findViewById(R.id.map_content_view_floor_gv);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (floors.length > position + 1) {
                        final BuildingFloor f = floors[position + 1];
                        fetchFloorPlan(f.getFloorPlanId(), false, f.getNumber());
                    }
                    mFloorsLayoutNew.setVisibility(mFloorsLayoutNew.isShown() ? View.GONE : View.VISIBLE);
                    mMaskLayout.setVisibility(mMaskLayout.isShown() ? View.GONE : View.VISIBLE);
                }
            });

            findViewById(R.id.map_content_view_floor_b2_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BuildingFloor f = floors[0];
                    fetchFloorPlan(f.getFloorPlanId(), false, f.getNumber());
                    mFloorsLayoutNew.setVisibility(mFloorsLayoutNew.isShown() ? View.GONE : View.VISIBLE);
                    mMaskLayout.setVisibility(mMaskLayout.isShown() ? View.GONE : View.VISIBLE);
                }
            });

            addZPOIMarkers(buildingId, floorPlanId);
        }
//        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_POIS_ADDED, ""));
    }

    private void showPolylineByFloorNumber(String floorNumber) {
        Log.e("@W@", "showPolylineByFloorNumber");
        Map<String, Polyline> map = DataCacheManager.getInstance().getFloorPolylineMap();
        for (String key : map.keySet()) {
            map.get(key).setVisible(key.equals(floorNumber));
            map.get(key).setVisible(key.equals(floorNumber));
        }

        showArrowMarkersByFloorNumber(floorNumber);

        if (mNaviStartMarker != null) {
            NavigationRoutePOI poi = (NavigationRoutePOI) mNaviStartMarker.getTag();
            mNaviStartMarker.setVisible(floorNumber.equals(poi.getFloorNumber()));
        }
        if (mNaviEndMarker != null) {
            NavigationRoutePOI poi = (NavigationRoutePOI) mNaviEndMarker.getTag();
            mNaviEndMarker.setVisible(floorNumber.equals(poi.getFloorNumber()));
        }
    }

    private void showArrowMarkersByFloorNumber(String floorNumber) {
        Map<String, List<Marker>> map = DataCacheManager.getInstance().getFloorArrowMarkersMap();
        for (String key : map.keySet()) {
            List<Marker> list = map.get(key);
            for (Marker marker : list) {
                marker.setVisible(key.equals(floorNumber));
            }
        }
        showNavigationMarkerByFloorNumber(floorNumber);
    }

    private void showNavigationMarkerByFloorNumber(String floorNumber) {
//        if (mNavigationMarker != null) {
//            mNavigationMarker.setVisible(false);
//        }
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void addZPOIMarkers(final String buildingId, String floorPlanId) {

        BuildingFloor floor = DataCacheManager.getInstance().getBuildingFloor(this, buildingId, floorPlanId);
        if (floor != null) {
//            RequestQueue queue = NetworkManager.getInstance().getRequestQueue(this);

            for (final POI zPoi : floor.getZPois()) {

                Marker zMarker = mMap.addMarker(new MarkerOptions()
                        .flat(false)
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(zPoi.getLatitude(), zPoi.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(zPoi.getName(), 30, Color.BLACK)))
                        .zIndex(SynTrendText.MARKER_Z_INDEX));
                zMarker.setTag(zPoi);

                DataCacheManager.getInstance().setZMarkerByBuildingId(buildingId, zMarker);

//                ImageRequest request = new ImageRequest(zPoi.getMapTextImageUrl(),
//                        new Response.Listener<Bitmap>() {
//                            @Override
//                            public void onResponse(Bitmap response) {
//                                Marker zMarker = mMap.addMarker(new MarkerOptions()
//                                        .flat(false)
//                                        .anchor(0.5f, 0.5f)
//                                        .position(new LatLng(zPoi.getLatitude(), zPoi.getLongitude()))
//                                        .icon(BitmapDescriptorFactory.fromBitmap(response))
//                                        .zIndex(SynTrendText.MARKER_Z_INDEX));
//                                zMarker.setTag(zPoi);
//
//                                DataCacheManager.getInstance().setZMarkerByBuildingId(buildingId, zMarker);
//                            }
//                        },
//                        0,
//                        0,
//                        ImageView.ScaleType.CENTER,
//                        Bitmap.Config.RGB_565,
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("@W@", "onErrorResponse url : " + zPoi.getMapTextImageUrl());
//                            }
//                        });
//
//                queue.add(request);
            }
        }
    }

    private void removePreviousMarkers(String buildingId) {

        if (mClusterManager != null) {
            List<OmniClusterItem> previousClusterItemList = DataCacheManager.getInstance().getClusterListByBuildingId(buildingId);
            if (previousClusterItemList != null) {
                mClusterManager.clearItems();
                previousClusterItemList.clear();
            }
            mClusterManager.cluster();
        }

        List<Marker> zMarkerList = DataCacheManager.getInstance().getZMarkerListByBuildingId(buildingId);
        if (zMarkerList != null) {

            for (Marker zMarker : zMarkerList) {
                zMarker.remove();
            }

            zMarkerList.clear();
        }
    }

    private void moveCameraByUserLocation(final List<LatLng> pointList) {
        Log.e("OKOK", "moveCameraByUserLocation");
        if (pointList != null && mUserMarker != null) {
            userPosition = mUserMarker.getPosition();
            previousPoint = null;
            closestPoint = null;
            closestDistance = -1;
            heading = -1;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("OKOK", " new Thread");
                    for (LatLng point : pointList) {

                        if (previousPoint != null) {
                            double r_numerator = (userPosition.longitude - previousPoint.longitude) * (point.longitude - previousPoint.longitude) +
                                    (userPosition.latitude - previousPoint.latitude) * (point.latitude - previousPoint.latitude);
                            double r_denominator = (point.longitude - previousPoint.longitude) * (point.longitude - previousPoint.longitude) +
                                    (point.latitude - previousPoint.latitude) * (point.latitude - previousPoint.latitude);
                            double r = r_numerator / r_denominator;

                            double px = previousPoint.longitude + r * (point.longitude - previousPoint.longitude);
                            double py = previousPoint.latitude + r * (point.latitude - previousPoint.latitude);

                            double s = ((previousPoint.latitude - userPosition.latitude) * (point.longitude - previousPoint.longitude) -
                                    (previousPoint.longitude - userPosition.longitude) * (point.latitude - previousPoint.latitude)) / r_denominator;

                            distanceLine = Math.abs(s) * Math.sqrt(r_denominator);

                            double xx = px;
                            double yy = py;

                            if ((r >= 0) && (r <= 1)) {
                                distanceSegment = distanceLine;
                            } else {

                                double dist1 = (userPosition.longitude - previousPoint.longitude) * (userPosition.longitude - previousPoint.longitude) +
                                        (userPosition.latitude - previousPoint.latitude) * (userPosition.latitude - previousPoint.latitude);
                                double dist2 = (userPosition.longitude - point.longitude) * (userPosition.longitude - point.longitude) +
                                        (userPosition.latitude - point.latitude) * (userPosition.latitude - point.latitude);
                                if (dist1 < dist2) {
                                    xx = previousPoint.longitude;
                                    yy = previousPoint.latitude;
                                    distanceSegment = Math.sqrt(dist1);
                                } else {
                                    xx = point.longitude;
                                    yy = point.latitude;
                                    distanceSegment = Math.sqrt(dist2);
                                }
                            }

                            if (closestDistance == -1 || closestDistance > distanceSegment) {
                                closestDistance = distanceSegment;
                                closestPoint = new LatLng(yy, xx);
                                heading = SphericalUtil.computeHeading(previousPoint, point);
                            }
                        }
                        previousPoint = point;
                    }

                    if (closestPoint == null) {
                        Log.e("OKOK", " closestPoint == null");
                        return;
                    }

                    final LatLng pointOnRoute = SphericalUtil.computeOffset(closestPoint, closestDistance, heading);

//            if (mNavigationMarker == null) {
//                mNavigationMarker = mMap.addMarker(new MarkerOptions()
//                        .position(pointOnRoute)
//                        .rotation((float) heading)
//                        .flat(true)
//                        .zIndex(SynTrendText.MARKER_Z_INDEX)
//                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_navigation_start)));
//                mNavigationMarker.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
//                        .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(this)));
//            } else {
//                mNavigationMarker.setPosition(pointOnRoute);
//                mNavigationMarker.setRotation((float) heading);
//                mNavigationMarker.setZIndex(SynTrendText.MARKER_Z_INDEX);
//                mNavigationMarker.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel()
//                        .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(this)));
//            }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int zoomLevel = (int) mMap.getCameraPosition().zoom;
                            CameraPosition cameraPosition;
                            if (autoHeading) {
                                cameraPosition = new CameraPosition.Builder()
//                    .target(pointOnRoute)
                                        .target(userPosition)
                                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
//                    .zoom((cameraMoveTimes < 5 && zoomLevel < NLPIText.MAP_ZOOM_LEVEL) ? NLPIText.MAP_ZOOM_LEVEL : zoomLevel)
                                        .bearing(mUserMarker.getRotation())
//                    .bearing((float) heading)
//                    .tilt(20)
                                        .build();
                            } else {
                                cameraPosition = new CameraPosition.Builder()
                                        .target(userPosition)
                                        .zoom(SynTrendText.MAP_ZOOM_LEVEL)
                                        .build();
                            }

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            cameraMoveTimes++;

//            if (startRecordFlag) {
//                String distanceBetween = Tools.getInstance().getDistanceStr(pointOnRoute.latitude, pointOnRoute.longitude, userPosition.latitude, userPosition.longitude);
//                recordData.add(new String[]{String.valueOf(pointOnRoute.latitude), String.valueOf(pointOnRoute.longitude),
//                        String.valueOf(userPosition.latitude), String.valueOf(userPosition.longitude),
//                        distanceBetween});
//            }

                            if (mCurrentRouteList != null) {
                                Log.e("OKOK", "mCurrentRouteList != null");
                                int lastOfArr = mCurrentRouteList.size() - 1;
                                double routeLat = Double.parseDouble(mCurrentRouteList.get(lastOfArr).getLatitude());
                                double routeLon = Double.parseDouble(mCurrentRouteList.get(lastOfArr).getLongitude());
                                float userDisToTarget = getDistance(pointOnRoute, routeLat, routeLon);
                                Log.e("OKOK", "userDisToTarget" + userDisToTarget);
                                if (userDisToTarget <= 5 && userDisToTarget > 0.5 &&
                                        mCurrentRouteList.get(lastOfArr).getFloorNumber()
                                                .equals(DataCacheManager.getInstance().getUserCurrentFloorLevel(SynTrendSDKActivity.this))) {
                                    if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A)) {
                                        if (NavigationRoutePOIListA.size() != 0) {
                                            if (NavigationRoutePOIListA.get(0).getStoreID().equals(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getStoreID())) {
                                                NavigationRoutePOIListA.remove(0);
                                                route_custom = "";
                                                for (NavigationRoutePOI poi : NavigationRoutePOIListA) {
                                                    route_custom = route_custom + poi.getStoreID() + ",";
                                                }
                                                PreferencesTools.getInstance().saveProperty(SynTrendSDKActivity.this, PreferencesTools.KEY_STORE_ROUTE_A, route_custom);
                                            }
                                        }
                                        if (NavigationRoutePOIListB.size() != 0) {
                                            if (NavigationRoutePOIListB.get(0).getStoreID().equals(NavigationRoutePOIListAll.get(NavigationRoutePOIListAllPosition).getStoreID())) {
                                                NavigationRoutePOIListB.remove(0);
                                                route_recommend = "";
                                                for (NavigationRoutePOI poi : NavigationRoutePOIListB) {
                                                    route_recommend = route_recommend + poi.getStoreID() + ",";
                                                }
                                                PreferencesTools.getInstance().saveProperty(SynTrendSDKActivity.this, PreferencesTools.KEY_STORE_ROUTE_B, route_recommend);
                                            }
                                        }
                                        Log.e("OKOK", "route_custom" + route_custom);
                                        Log.e("OKOK", "route_recommend" + route_recommend);
                                    }

                                    if (getIntent().getExtras().containsKey(ARG_KEY_STORE_ROUTE_A) && NavigationRoutePOIListAllPosition >= NavigationRoutePOIListAll.size() - 1) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SynTrendSDKActivity.this)
                                                .setTitle(R.string.dialog_title_hint)
                                                .setMessage(R.string.dialog_message_arrive_destination)
                                                .setPositiveButton(R.string.dialog_button_ok_text, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        finish();
                                                    }
                                                });
                                        builder.create().show();
                                    } else {
                                        DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
                                                R.string.dialog_title_hint,
                                                R.string.dialog_message_arrive_destination);
                                    }
                                    NavigationRoutePOIListAllPosition++;
                                    MarkerListPosition++;
                                    leaveNavigation();
                                }
//                if (getArguments().containsKey(ARG_KEY_BOOK_NAVIGATION_ROUTE) && userDisToTarget <= 3) {
//                    String bookName = getArguments().getString(ARG_KEY_BOOK_NAME);
//                    BookLocationInfo locationInfo = (BookLocationInfo) getArguments().getSerializable(ARG_KEY_BOOK_LOCATION_INFO);
//                    BookNavigationRoute navigationRoute = (BookNavigationRoute) getArguments().getSerializable(ARG_KEY_BOOK_NAVIGATION_ROUTE);
//                    openFragmentPage(BookshelfFragment.newInstance(bookName,
//                            locationInfo,
//                            navigationRoute.getBookshelfInfo(),
//                            navigationRoute.getBookshelfMsg()),
//                            BookshelfFragment.TAG);
//                }
                            }
                        }
                    });
                }
            }).start();
        }

    }

    private float getDistance(LatLng userlatlong, double routeLat, double routeLon) {

        Location l1 = new Location("One");
        l1.setLatitude(userlatlong.latitude);
        l1.setLongitude(userlatlong.longitude);
        Log.e("OKOK", "userlatlong.latitude" + userlatlong.latitude);
        Log.e("OKOK", "userlatlong.longitude" + userlatlong.longitude);
        Location l2 = new Location("Two");
        l2.setLatitude(routeLat);
        l2.setLongitude(routeLon);
        Log.e("OKOK", "routeLat" + routeLat);
        Log.e("OKOK", "routeLon" + routeLon);

        float distance = l1.distanceTo(l2);
        String dist = distance + " M";

        if (distance > 1000.0f) {
            distance = distance / 1000000.0f;
            dist = distance + "M";
        }

        return distance;
    }

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }

    private boolean checkTileExists(int x, int y, int zoom) {
        if ((zoom < SynTrendText.MAP_MIN_ZOOM_LEVEL || zoom > SynTrendText.MAP_MAX_ZOOM_LEVEL)) {
            return false;
        }
        return true;
    }

    private void refreshMapTextMarkers(int zoomLevel) {
        OmniFloor omniFloor = DataCacheManager.getInstance().getCurrentShowFloor();
        if (omniFloor != null) {
            String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(this, omniFloor.getFloorPlanId());

            List<Marker> zMarkerList = DataCacheManager.getInstance().getZMarkerListByBuildingId(buildingId);
            if (zMarkerList != null) {
                for (Marker marker : zMarkerList) {
                    POI poi = (POI) marker.getTag();
//                    if (poi != null && poi.getZoomLevelData() != null) {
//                        int min = poi.getZoomLevelData().getMinLevelInt();
//                        int max = poi.getZoomLevelData().getMaxLevelInt();
//
//                        marker.setVisible((zoomLevel >= min && zoomLevel <= max));
//                    }
                }
            }
        }
    }

    private void drawPolyline(final String floorNumber, final List<LatLng> pointList,
                              int colorIndex, float width) {
        Log.e("OKOK", "drawPolyline colorIndex" + colorIndex);
        PolylineOptions lineOptions = null;

        if (!pointList.isEmpty()) {
            lineOptions = new PolylineOptions()
                    .addAll(pointList)
                    .width(width)
                    .color(colorIndex);
        }
        if (lineOptions != null) {
            final PolylineOptions finalLineOptions = lineOptions;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Polyline polyline = mMap.addPolyline(finalLineOptions);
                    polyline.setZIndex(SynTrendText.POLYLINE_Z_INDEX);
                    // only show the floor's route poly line
                    polyline.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel().equals(floorNumber));

//            addArrowOnPolyline(pointList, floorNumber);

                    Log.e("@W@", "drawPolyline floorNumber : " + floorNumber + ", polyline : " + polyline);
                    DataCacheManager.getInstance().getFloorPolylineMap().put(floorNumber, polyline);
                    DataCacheManager.getInstance().setFloorRoutePointsMap(floorNumber, pointList);
                    if (pointList.size() == 4) {
                        Marker arrowMarker = mMap.addMarker(new MarkerOptions()
                                .flat(true)
                                .rotation((float) SphericalUtil.computeHeading(
                                        new LatLng(pointList.get(2).latitude, pointList.get(2).longitude),
                                        new LatLng(pointList.get(3).latitude, pointList.get(3).longitude)))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_path_arrow_r))
                                .position(new LatLng(pointList.get(pointList.size() - 1).latitude,
                                        pointList.get(pointList.size() - 1).longitude))
                                .zIndex(SynTrendText.MARKER_Z_INDEX));
                        arrowMarker.setVisible(DataCacheManager.getInstance().getCurrentShowFloor().getFloorLevel().equals(floorNumber));
                    }
                }
            });

        } else {
            DialogTools.getInstance().showErrorMessage(this,
                    R.string.error_dialog_title_text_normal,
                    "There's no POI!");
        }
    }

    private void checkLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ensurePermissions();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("位置服務尚未開啟，請設定");
            dialog.setPositiveButton("open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
//                    DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
//                            getString(R.string.error_dialog_title_text_normal),
//                            "沒有開啟位置服務，地圖無法顯示");
                }
            });
            dialog.show();
        }
    }

    private void ensurePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
//            registerService();
        }
    }

    private void registerService() {
        if (mOGService == null) {
            mOGService = new OGService(this);
        }
        Log.e("OKOK", "registerService");
        mOGService.startService(new OGService.GoogleApiClientConnectCallBack() {
            @Override
            public void onGoogleApiClientConnected() {
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                            grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        finish();
                    }
                }
            }
        }
        registerService();
    }

    private void getEmergencyRoute(final String type) {
        if (!mIsMapInited) {
            mEventBus.post(new OmniEvent(OmniEvent.TYPE_REQUEST_LAST_LOCATION, ""));

        } else {
            Log.e("OKOK", "getEmergencyRoute type" + type);
            mEventBus.post(new OmniEvent(OmniEvent.TYPE_REQUEST_LAST_LOCATION, ""));

            String floorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
            String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(this, floorPlanId);
            String floorLevel = DataCacheManager.getInstance().getUserCurrentFloorLevel(this);
            Double lat;
            Double lng;

            BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
            if (mIsIndoor && !floorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9") && mLastLocation != null) {
                fetchFloorPlan(floorPlanId, false, DataCacheManager.getInstance().getFloorNumberByPlanId(this, floorPlanId));
                lat = mLastLocation.getLatitude();
                lng = mLastLocation.getLongitude();
            } else {
                fetchFloorPlan(floor.getFloorPlanId(), false, "1");
                POI entrancePOI = DataCacheManager.getInstance().getEntrancePOI(floor);
                lat = entrancePOI.getLatitude();
                lng = entrancePOI.getLongitude();
                buildingId = "1";
                floorLevel = "1";
            }
            Log.e("OKOK", "getEmergencyRoute type" + buildingId + floorLevel + lat + lng + type);
            LocationApi.getInstance().getEmergencyRoute(this,
                    buildingId,
                    floorLevel,
                    lat,
                    lng,
                    type,
                    new NetworkManager.NetworkManagerListener<NavigationRoutePOI[]>() {
                        @Override
                        public void onSucceed(NavigationRoutePOI[] routePOIs) {
                            DialogTools.getInstance().dismissProgress(SynTrendSDKActivity.this);
                            if (routePOIs.length != 0) {
                                startNavigation(Arrays.asList(routePOIs));
                                final String lastPOIFloorLevel = routePOIs[routePOIs.length - 1].getFloorNumber();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!titleSetting) {
                                            mNaviInfoTitleTV.setText((lastPOIFloorLevel.contains("-") ?
                                                    lastPOIFloorLevel.replace("-", "B") :
                                                    lastPOIFloorLevel + "F") + " " + mNaviInfoTitleTV.getText());
                                            titleSetting = true;
                                        }
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this, R.string.error_dialog_title_text_normal, R.string.dialog_message_route_empty);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFail(String errorMsg, boolean shouldRetry) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
                                            R.string.error_dialog_title_text_normal,
                                            R.string.error_dialog_title_text_json_parse_error);
                                }
                            });
                        }

                    });
        }
    }

    private void getRecommendRoute(final String routeA, final String routeB) {
        Log.e("OKOK", "routeA" + routeA);
        Log.e("OKOK", "routeB" + routeB);
        if (!mIsMapInited) {
            mEventBus.post(new OmniEvent(OmniEvent.TYPE_REQUEST_LAST_LOCATION, ""));

        } else {
            final String floorPlanId = DataCacheManager.getInstance().getUserCurrentFloorPlanId();
            String buildingId = DataCacheManager.getInstance().getBuildingIdByFloorPlanId(this, floorPlanId);
            String floorLevel = DataCacheManager.getInstance().getUserCurrentFloorLevel(this);
            Double lat;
            Double lng;

            final BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(this);
            if (mIsIndoor && !floorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9") && mLastLocation != null) {
//                fetchFloorPlan(floorPlanId, false, DataCacheManager.getInstance().getFloorNumberByPlanId(this, floorPlanId));
                lat = mLastLocation.getLatitude();
                lng = mLastLocation.getLongitude();
            } else {
//                fetchFloorPlan(floor.getFloorPlanId(), false, "1");
                POI entrancePOI = DataCacheManager.getInstance().getEntrancePOI(floor);
                lat = entrancePOI.getLatitude();
                lng = entrancePOI.getLongitude();
                buildingId = "1";
                floorLevel = "1";
            }

            LocationApi.getInstance().getRecommendRoute(this,
                    routeA,
                    routeB,
                    floorLevel,
                    lat,
                    lng,
                    new NetworkManager.NetworkManagerListener<NavigationRoutePOI[]>() {
                        @Override
                        public void onSucceed(NavigationRoutePOI[] routePOIs) {
                            DialogTools.getInstance().dismissProgress(SynTrendSDKActivity.this);
                            if (routePOIs.length != 0) {
//                                startNavigation(Arrays.asList(routePOIs));
                                routePOIList = new ArrayList<>();
                                routePOIList = Arrays.asList(routePOIs);
                                initRoutePOIList(routePOIList);
                                BuildingFloor floor = DataCacheManager.getInstance().getSearchFloorPlanId(
                                        SynTrendSDKActivity.this, Integer.parseInt(routePOIs[0].getID()));
//                                BuildingFloor floor = DataCacheManager.getInstance().getMainGroundFloorPlanId(SynTrendSDKActivity.this);
//                                if (mIsIndoor && !floorPlanId.equals("919f0ac4-62e4-48ae-8217-dcb707bbcdc9")) {
//                                    fetchFloorPlan(floorPlanId, false, DataCacheManager.getInstance().getFloorNumberByPlanId(SynTrendSDKActivity.this, floorPlanId));
//                                } else {
                                fetchFloorPlan(floor.getFloorPlanId(), false, routePOIs[0].getFloorNumber());
//                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this, R.string.error_dialog_title_text_normal, R.string.dialog_message_route_empty);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFail(String errorMsg, boolean shouldRetry) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogTools.getInstance().showErrorMessage(SynTrendSDKActivity.this,
                                            R.string.error_dialog_title_text_normal,
                                            R.string.error_dialog_title_text_json_parse_error);
                                }
                            });
                        }

                    });
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        BatteryPowerData powerData = BatteryPowerData.generateBatteryBeacon(scanRecord);

        if (powerData != null && powerData.BatteryUuid.toUpperCase().startsWith("00112233-4455-6677-8899-AABBCCDDEEFF")) {
            mBBHandler.obtainMessage(MSG_GET_DATA).sendToTarget();
            Log.e("@W@", "BatteryPower:" + powerData.batteryPower +
                    "\nBatteryUuid : " + powerData.BatteryUuid +
                    "\naddress : " + device.getAddress() +
                    "\ndevice name : " + device.getName() +
                    "\nrssi : " + rssi);
            if (!device.getAddress().equals(mLastSendBatteryMac)) {

                SynTrendApi.getInstance().setBeaconBatteryLevel(SynTrendSDKActivity.this,
                        device.getAddress(),
                        powerData.batteryPower + "",
                        new NetworkManager.NetworkManagerListener<SetBeaconBatteryResponse>() {
                            @Override
                            public void onSucceed(SetBeaconBatteryResponse response) {
                                if (response.isSuccess()) {
                                    mLastSendBatteryMac = device.getAddress();
                                }
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
                } catch (RemoteException e) {
                    Log.e("@W@", "RemoteException #999 ");
                }
            }

            @Override
            public void didExitRegion(Region region) {
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                mBeaconManager.addRangeNotifier(new RangeNotifier() {
                    @Override
                    public void didRangeBeaconsInRegion(final Collection<Beacon> collection, final Region region) {
                        SynTrendSDKActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                receiveBeacon(collection);
                            }
                        });
                    }
                });

            }
        });

        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void receiveBeacon(final Collection<Beacon> collection) {

        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        if (collection.size() > 0) {

            String uuid = "";
            String major;
            String minor;
            List<String> majorList = new ArrayList<>();
            List<String> minorList = new ArrayList<>();

            for (Beacon b : collection) {
                uuid = b.getId1().toString();
                major = b.getId2().toString();
                minor = b.getId3().toString();
                if (b.getDistance() <= Tools.getInstance().getBeaconTrigger()) {
                    if (SynTrend_BEACON_MAJOR_LIST.contains(major)) {
                        if (!minorList.contains(minor)) {
                            majorList.add(major);
                            minorList.add(minor);
                        }
                    }
                }

                if ((mNotificationSendTime == -1 || currentTimeInMillis - mNotificationSendTime > (mBeaconCooldownTime * 1000))
                        && !minorList.isEmpty()) {

                    String allMajorsStr = "";
                    String allMinorsStr = "";
                    if (!majorList.isEmpty()) {
                        for (int i = 0; i < majorList.size(); i++) {
                            String majorInList = majorList.get(i);
                            String minorInList = minorList.get(i);

                            if (BeaconDataManager.getInstance().shouldPush(SynTrendSDKActivity.this, majorInList, minorInList)) {
                                if (!allMajorsStr.isEmpty()) {
                                    allMajorsStr = allMajorsStr + ",";
                                }
                                allMajorsStr = allMajorsStr + majorInList;

                                if (!allMinorsStr.isEmpty()) {
                                    allMinorsStr = allMinorsStr + ",";
                                }
                                allMinorsStr = allMinorsStr + minorInList;
                            }
                        }
                    }

                    if (!allMinorsStr.isEmpty() && !isGettingAllBeaconInfo) {
                        isGettingAllBeaconInfo = true;
                        getAllBeaconInfo(allMajorsStr, allMinorsStr);
                    }
                }
            }
        }
    }

    private void getAllBeaconInfo(String allMajors, String allMinors) {
//        Log.e("@W@", "allMajors : " + allMajors + ", allMinors : " + allMinors);

//        String loginToken = "";
//        UserLoginInfo loginInfo = UserInfoManager.getInstance().getUserInfo(MainActivity.this);
//        if (loginInfo != null && !TextUtils.isEmpty(loginInfo.getServerLoginToken())) {
//            loginToken = loginInfo.getServerLoginToken();
//        }
//
//        SynTrendApi.getInstance().getBeaconsPushInfo(MainActivity.this,
//                allMajors,
//                allMinors,
//                loginToken,
//                new NetworkManager.NetworkManagerListener<BeaconResponse[]>() {
//                    @Override
//                    public void onSucceed(BeaconResponse[] responses) {
//                        isGettingAllBeaconInfo = false;
//
//                        if (responses != null && responses.length != 0) {
//                            BeaconResponse response = responses[0];
//
//                            BeaconInfo[] beaconInfos = response.getBeaconInfos();
//                            if (beaconInfos != null && beaconInfos.length != 0) {
//
//                                BeaconDataManager.getInstance().addReceivedBeaconData(MainActivity.this,
//                                        response.getAllBeaconInfo());
//
//                                mBeaconCooldownTime = response.getCooldown();
//
//                                for (BeaconInfo info : beaconInfos) {
////
//                                    BeaconPushContent[] contents = info.getBeaconPushContents();
//                                    if (contents != null && contents.length != 0) {
////
//                                        BeaconPushContent content = contents[0];
//                                        sendNotification(info.getMajor(), info.getMinor(), info, content);
//
//                                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_NOTIFICATION_HISTORY_STATUS_CHANGED, ""));
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFail(String errorMsg, boolean shouldRetry) {
//                        isGettingAllBeaconInfo = false;
//                    }
//                });
    }

    public class FloorAdapter extends BaseAdapter {

        private Context context;

        public FloorAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return floorNum;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View grid;

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                grid = layoutInflater.inflate(R.layout.item_floor_gridview, null);
                TextView textView = grid.findViewById(R.id.item_floor_gridview_fl_tv);
                textView.setText(String.valueOf(position + 1));
            } else {
                grid = convertView;
            }

            return grid;
        }
    }
}
