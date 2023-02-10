package com.example.uventawh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainWareHouseActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private final long delayTime = 1000 * 300;
    private Handler CloseHandler = new Handler();
    private PhoneUnlockedReceiver receiver;
    public final static String BROADCAST_ACTION = "ru.uventa.uventawh.exchange_service_back_broadcast";

    protected OnBackPressedListener onBackPressedListener;

    public Boolean containerPresent = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);


    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ware_house);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                float x = toolbar.getX();

            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_scanRouteListFragment, R.id.routesListFragment, R.id.nav_placement_menu, R.id.nav_invent, R.id.nav_code_exchange)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.tvUser)).setText(getIntent().getStringExtra("user_description"));
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.tvAppTitle)).setText("WMS\n" + getResources().getString(R.string.version));

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                return true;
//            }
//        });

        receiver = new PhoneUnlockedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

//        CloseHandler.postDelayed(closeProc, delayTime);

        startServices();

    }

    public void onUserInteraction(){
//        CloseHandler.removeCallbacks(closeProc);
//        CloseHandler.postDelayed(closeProc, delayTime);
    }

    private Runnable closeProc = new Runnable() {
        public void run() {

            try {
                unregisterReceiver(receiver);

            } catch (Exception e){

            };


            finish();
        }
    };


    private void startServices() {

        Intent intent = new Intent(getBaseContext(), ExchangeService.class);
        startService(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_ware_house, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        TakeScreenShot.DoUp(getBaseContext(), findViewById(R.id.drawer_layout));

        if (containerPresent){

            HttpClient httpClient = new HttpClient(this);
            Bundle bundle = new Bundle();
            httpClient.showQuestionYesNoCancel(this, new BundleMethodInterface() {
                @Override
                public void callMethod(Bundle arguments) {

                    containerPresent = false;

                    doOnSupportNavigateUp();

                }
            }, bundle, "Контейнер не завершен. Выйти ?", "Контейнер");


        } else {

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }

        return false;

    }

    public void doOnSupportNavigateUp(){

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Boolean res = NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {

        TakeScreenShot.DoBack(getBaseContext(), findViewById(R.id.drawer_layout));

        if (containerPresent){

            HttpClient httpClient = new HttpClient(this);
            Bundle bundle = new Bundle();
            httpClient.showQuestionYesNoCancel(this, new BundleMethodInterface() {
                @Override
                public void callMethod(Bundle arguments) {

                    containerPresent = false;

                    doOnBackPressed();

                }
            }, bundle, "Контейнер не завершен. Выйти ?", "Контейнер");


        } else {

//            if (onBackPressedListener != null)
//                onBackPressedListener.doBack();
//            else
                super.onBackPressed();
        }
    }

    public void doOnBackPressed(){

        super.onBackPressed();

    }

    public class PhoneUnlockedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
//                Log.d(TAG, "Phone unlocked");
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
//                Log.d(TAG, "Phone locked");

//                finish();

            }
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

}
