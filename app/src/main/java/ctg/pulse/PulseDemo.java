package ctg.pulse;



import android.content.res.Resources;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import java.util.ArrayList;

import java.util.Map;

import ctg.adapter.MyFragPagerAdapter;
import toolbarslidingtab.SlidingTabLayout;
import util.proxy.HashMapProxy;

public class PulseDemo extends AppCompatActivity {

    private ArrayList<Fragment> fragments;
    ArrayList<Map<String, Object>> adaptParam;
    ViewPager viewPager;
    SlidingTabLayout slidingTabLayout;
    MyFragPagerAdapter vpAdapter;
    Toolbar toolbar;
    static int mWidth, mHeight, statusBarHeight,realHeight, mDensityInt;
    static float scale, mDensity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pulse);
        viewPager = (ViewPager) findViewById(R.id.pulse_vp);
        fragments = new ArrayList<Fragment>();
        fragments.add(new FragColorSplash());
        fragments.add(new FragPattern());
        fragments.add(new FragMic());
        fragments.add(new FragCamera());
        fragments.add(new FragSet());
        adaptParam = new ArrayList<Map<String, Object>>();
        adaptParam.add(new HashMapProxy<String, Object>().putObject("title", "Color").putObject("image", (Object) R.mipmap.splash));
        adaptParam.add(new HashMapProxy<String, Object>().putObject("title", "Pattern").putObject("image", (Object) R.mipmap.broadcast));
        adaptParam.add(new HashMapProxy<String, Object>().putObject("title", "Mic").putObject("image", (Object) R.mipmap.mic));
        adaptParam.add(new HashMapProxy<String, Object>().putObject("title", "Camera").putObject("image", (Object) R.mipmap.camera));
        adaptParam.add(new HashMapProxy<String, Object>().putObject("title", "Settings").putObject("image", (Object) R.mipmap.setting));

        vpAdapter = new MyFragPagerAdapter(getSupportFragmentManager(), fragments, adaptParam);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(vpAdapter);

        LocationManager locationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE);
        WindowManager wm = (WindowManager)getSystemService(this.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
        statusBarHeight = getStatusBarHeight();
        realHeight = mHeight-statusBarHeight;
        scale = getResources().getDisplayMetrics().density/2;
        mDensity = getResources().getDisplayMetrics().density;
        mDensityInt = (int) mDensity;
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        // 设置SlidingTab
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.pulse_toolbar);
        slidingTabLayout.setCustomTabView(R.layout.toolbaritem, R.id.toolbar_img, R.id.toolbar_txt, R.id.toolbar_under);
        slidingTabLayout.setViewPager(viewPager, mWidth);
    }

    public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height",
                        "dimen", "android"));
    }

}
