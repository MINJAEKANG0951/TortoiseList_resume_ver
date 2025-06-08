package simple.planner.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.appcompat.widget.SwitchCompat;

import io.realm.Realm;
import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.dialog.BottomSheetDialog_YesOrNo;
import simple.planner.realmObjects.Setting;
import simple.planner.realmObjects.realmHelpers.SettingHelper;
import simple.planner.services.LockScreenService;
import simple.planner.sharedPreferences.PrefsUtil;

public class SettingActivity extends BaseActivity
{
    private Realm realm;
    private Setting settingValues;

    private ImageButton backButton;

    private RadioButton English;
    private RadioButton Korean;

    private LinearLayout changeListNameBtn;
    private TextView todoListName;
    private SwitchCompat showTitle;
    private SwitchCompat showContent;
    private SwitchCompat showCreated;
    private SwitchCompat showUpdated;
    private SwitchCompat showProgress;
    private SwitchCompat showScheduled;
    private SwitchCompat showCheckIcon;
    private RadioButton newToOld;
    private RadioButton oldToNew;
    private RadioButton showUncompleted;
    private RadioButton showCompleted;
    private RadioButton showUncompletedCompletedBoth;
    private SwitchCompat notificationOnOff;
    private SwitchCompat lockScreenModeOnOff;


    private BottomSheetDialog_YesOrNo bottomSheetDialog_yesOrNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        realm = Realm.getDefaultInstance();

        connectViews();
        setViewDefaults();
        defineViewListeners();

    }

    private void connectViews() {

        changeListNameBtn               = findViewById(R.id.changeListNameBtn);
        backButton                      = findViewById(R.id.backButton);
        English                         = findViewById(R.id.English);
        Korean                          = findViewById(R.id.Korean);
        todoListName                    = findViewById(R.id.todoListName);
        showTitle                       = findViewById(R.id.showTitle);
        showContent                     = findViewById(R.id.showContent);
        showCreated                     = findViewById(R.id.showCreated);
        showUpdated                     = findViewById(R.id.showUpdated);
        showProgress                    = findViewById(R.id.showProgress);
        showScheduled                   = findViewById(R.id.showScheduled);
        showCheckIcon                   = findViewById(R.id.showCheckIcon);
        newToOld                        = findViewById(R.id.newToOld);
        oldToNew                        = findViewById(R.id.oldToNew);
        showUncompleted                 = findViewById(R.id.showUncompleted);
        showCompleted                   = findViewById(R.id.showCompleted);
        showUncompletedCompletedBoth    = findViewById(R.id.showCompletedUnCompletedBoth);
        notificationOnOff               = findViewById(R.id.notificationOnOff);
        lockScreenModeOnOff             = findViewById(R.id.lockScreenModeOnOff);

    }

    private void setViewDefaults() {

        settingValues    = realm.where(Setting.class).findFirst();
        if(settingValues==null) return;

        if(settingValues.getLanguageCode() == null) {
            English.setChecked(true);
        } else if(settingValues.getLanguageCode().equals("ko")) {
            Korean.setChecked(true);
        }

        todoListName    .setText(settingValues.getListName());
        showTitle       .setChecked(settingValues.isShowTitle());
        showContent     .setChecked(settingValues.isShowContent());
        showCreated     .setChecked(settingValues.isShowCreated());
        showUpdated     .setChecked(settingValues.isShowUpdated());
        showProgress    .setChecked(settingValues.isShowProgress());
        showScheduled   .setChecked(settingValues.isShowScheduled());
        showCheckIcon   .setChecked(settingValues.isShowCheckIcon());

        if(settingValues.isFromOldToNew()) {
            oldToNew.setChecked(true);
        } else {
            newToOld.setChecked(true);
        }

        if(settingValues.isShowUncompleted() && settingValues.isShowCompleted() ) {
            showUncompletedCompletedBoth.setChecked(true);
        } else if (settingValues.isShowUncompleted()) {
            showUncompleted.setChecked(true);
        } else if (settingValues.isShowCompleted()) {
            showCompleted.setChecked(true);
        }

        notificationOnOff.setChecked(settingValues.isNotificationOn());
        lockScreenModeOnOff.setChecked(settingValues.isLockScreenModeOn() && LockScreenService.isIsServiceRunning());

    }

    private void defineViewListeners(){

        backButton.setOnClickListener(v->finish());
        changeListNameBtn.setOnClickListener(v->{ /* 나중에 기능 넣을 생각 있으면 */});


        English.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                if(bottomSheetDialog_yesOrNo==null) showLanguageSetDialog(null);
            }
        });
        Korean.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                if(bottomSheetDialog_yesOrNo==null) showLanguageSetDialog("ko");
            }
        });

        showContent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try
            {
                realm.beginTransaction();
                settingValues.setShowContent(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });
        showCreated.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try
            {
                realm.beginTransaction();
                settingValues.setShowCreated(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });
        showUpdated.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try
            {
                realm.beginTransaction();
                settingValues.setShowUpdated(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });
        showProgress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try
            {
                realm.beginTransaction();
                settingValues.setShowProgress(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });
        showScheduled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try
            {
                realm.beginTransaction();
                settingValues.setShowScheduled(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });

//      showCheckIcon.setOnCheckedChangeListener((buttonView, isChecked) -> {});

        oldToNew.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    realm.beginTransaction();
                    settingValues.setFromOldToNew(true);
                    realm.commitTransaction();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });
        newToOld.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    realm.beginTransaction();
                    settingValues.setFromOldToNew(false);
                    realm.commitTransaction();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });

        // 여기부터 다시. 람다식으로 바꾸고, radio 는 모두 onCheckChangeListener 로 
        showUncompletedCompletedBoth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    realm.beginTransaction();
                    settingValues.setShowUncompleted(true);
                    settingValues.setShowCompleted(true);
                    realm.commitTransaction();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });
        showUncompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    realm.beginTransaction();
                    settingValues.setShowUncompleted(true);
                    settingValues.setShowCompleted(false);
                    realm.commitTransaction();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });
        showCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                try {
                    realm.beginTransaction();
                    settingValues.setShowUncompleted(false);
                    settingValues.setShowCompleted(true);
                    realm.commitTransaction();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });

        notificationOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                realm.beginTransaction();
                settingValues.setNotificationOn(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }
        });

        lockScreenModeOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {

            try {
                realm.beginTransaction();
                settingValues.setLockScreenModeOn(isChecked);
                realm.commitTransaction();
            } catch(Exception e) {
                realm.cancelTransaction();
            }

            Setting newSetting = realm.where(Setting.class).findFirst();
            if(newSetting == null) return;
            boolean lockScreenModeOnOff = newSetting.isLockScreenModeOn();

            Intent intent = new Intent(this, LockScreenService.class);

            if(lockScreenModeOnOff)
            {
                // 시작
                if(!LockScreenService.isIsServiceRunning()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
            }
            else
            {
                // 종료
                if(LockScreenService.isIsServiceRunning()) {
                    stopService(intent);
                }
            }

        });
    }


    private void showLanguageSetDialog(String languageCode) {
        // 다시 시작, 아니면 적어도 realm, sharedPreference 재설정 후 다시 TODOListActivity 띄우기

        String textToShow = getResources().getString(R.string.when_language_change_to_korean);
        String btnYesText = getResources().getString(R.string.when_language_change_to_korean_yes);
        String btnNoText = getResources().getString(R.string.when_language_change_to_korean_no);

        if(languageCode == null) {
            textToShow = getResources().getString(R.string.when_language_change_to_english);
            btnYesText = getResources().getString(R.string.when_language_change_to_english_yes);
            btnNoText = getResources().getString(R.string.when_language_change_to_english_no);
        }

        if(bottomSheetDialog_yesOrNo!=null){
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setText(textToShow);
        bottomSheetDialog_yesOrNo.setYesBtnText(btnYesText);
        bottomSheetDialog_yesOrNo.setNoBtnText(btnNoText);
        bottomSheetDialog_yesOrNo.show(getSupportFragmentManager(),null);
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{

            // back to former language setting
            if(settingValues.getLanguageCode() == null) {
                English.setChecked(true);
            } else if(settingValues.getLanguageCode().equals("ko")) {
                Korean.setChecked(true);
            }

            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        });
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{

            if( SettingHelper.setLanguageCode(languageCode) ) {
                PrefsUtil.setLanguage(this, SettingHelper.getLanguageCode());
            }

            if(LockScreenService.isIsServiceRunning()) {
                Intent intent = new Intent(this, LockScreenService.class);
                stopService(intent);
            }

            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;

            Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
        bottomSheetDialog_yesOrNo.setOnDismissListener(new BottomSheetDialog_YesOrNo.OnDismissListener() {
            @Override
            public void whenDismiss() {

                if(settingValues.getLanguageCode() == null) {
                    English.setChecked(true);
                } else if(settingValues.getLanguageCode().equals("ko")) {
                    Korean.setChecked(true);
                }

                bottomSheetDialog_yesOrNo = null;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm!=null) {
            realm.close();
        }

    }
}
