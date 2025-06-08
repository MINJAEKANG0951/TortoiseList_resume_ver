package simple.planner.activities.LockScreenActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.receivers.PhoneStateReceiver;

public class LockScreenActivity extends BaseActivity {

    private Realm realm;
    private ImageView createBtn;
    private ImageView exitBtn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private RealmResults<TODOListItem> Todo;
    private RealmResults<TODOListItem> Done;

    private OnPlusBtnClickListener onPlusBtnClickListener;

    public Realm getRealm() {
        return realm;
    }

    private BroadcastReceiver userPresentReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock2);
        realm = Realm.getDefaultInstance();

        configureToShowOnLockScreen();
        connectViews();
        valueSetting();
        viewSetting();

        userPresentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction()==null) return;

                if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    finish();
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(userPresentReceiver, intentFilter);

    }

    private void configureToShowOnLockScreen() {


        /**

         when device api level < 30
         >> Immersive mode

         when >= 30
         >> WindowInsetsController

         to hide / show statusBar or NavigationBar

         **/


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//      window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  // unlock lockScreen when the lock security is weak(like swipe unlock has applied to the device)
//      window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // turn off when there is no user interaction
//      window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);    // no need to turn screen on when this activity already starts from screen on signal
        window.setStatusBarColor(android.graphics.Color.BLACK);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {       // since android api level 30, if you want to hide navigation bar / status bar, you use 'WindowInsetsController'
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                // problem : no perfect replacement for View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }

        } else {
            View decorView = getWindow().getDecorView();
            int uiSettings =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE   |               // even when navigation bar appears, the activity keep the same layout structure
//                  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |        // the activity stretch to the space where status bar is.
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |               // hide navigation bar
//                  View.SYSTEM_UI_FLAG_FULLSCREEN |                    // hide both navigation bar and status bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;               // the activity becomes immersive mode. the activity becomes full screen(hide system bars[nav,status]). you can drag the edge to make the system bars appear again.
            decorView.setSystemUiVisibility(uiSettings);                // ** SYSTEM_UI_FLAG_IMMERSIVE_STICKY does not itself hide navigation bar or status bar. you have to include them with this to hide them.
            // problem : navigation var lefts after bottom sheet dialog
        }

    }


    private void connectViews() {

        createBtn = findViewById(R.id.create);
        exitBtn = findViewById(R.id.exit);
        tabLayout = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.viewPager);

    }

    private void valueSetting() {

        Todo = realm.where(TODOListItem.class).equalTo("done",false).findAll();
        Done = realm.where(TODOListItem.class).equalTo("done",true).findAll();

        Todo.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @Override
            public void onChange(RealmResults<TODOListItem> todoListItems) {
                updateTab(0, todoListItems.size());
            }
        });
        Done.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @Override
            public void onChange(RealmResults<TODOListItem> todoListItems) {
                updateTab(1, todoListItems.size());
            }
        });

    }

    private void viewSetting() {

        createBtn.setAlpha(Build.VERSION.SDK_INT < 26?0.1f:1f);

        createBtn.setOnClickListener(v->{

            if(Build.VERSION.SDK_INT < 26) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.feature_not_supported), Snackbar.LENGTH_LONG).show();
                return;
            }

            if(onPlusBtnClickListener!=null) {
                onPlusBtnClickListener.onPlusBtnClick();
            }

        });


        exitBtn.setOnClickListener(v->{
            finish();       // finishAndRemoveTask(); 둘 중 뭘로 해야 할지..
        });

        int howManyTodo = Todo.size();
        int howManyDone = Done.size();

        String TodoText = getResources().getString(R.string.items_status_not_yet_completed) + " ( " + howManyTodo + " )";
        String DoneText = getResources().getString(R.string.items_status_completed) + " ( " + howManyDone + " )";

        tabLayout.addTab(tabLayout.newTab().setText(TodoText));
        tabLayout.addTab(tabLayout.newTab().setText(DoneText));

        viewPager2.setAdapter(new LockScreenPageAdapter(this));
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void updateTab(int tabIndex, int howMany) {

        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);

        if(tab != null) {
            String TabText = "";
            if(tabIndex == 0) {
                TabText = getResources().getString(R.string.items_status_not_yet_completed)  + " ( " + howMany + " )";
            } else {
                TabText = getResources().getString(R.string.items_status_completed)  + " ( " + howMany + " )";
            }
            tab.setText(TabText);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(realm!=null) {
            if(!realm.isClosed()) { realm.close(); }
            realm = null;
        }
        if (userPresentReceiver != null) {
            unregisterReceiver(userPresentReceiver);
        }
        super.onDestroy();
    }


    public interface OnPlusBtnClickListener {
        void onPlusBtnClick();
    }

    public void setOnPlusBtnClickListener(OnPlusBtnClickListener onPlusBtnClickListener) {
        this.onPlusBtnClickListener = onPlusBtnClickListener;
    }

}
