package simple.planner.activities.LockScreenActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CombinedVibration;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import io.realm.Realm;
import simple.planner.R;
import simple.planner.design.GapBorderDecoration;
import simple.planner.dialog.BottomSheetDialog_Create_Lock;
import simple.planner.dialog.BottomSheetDialog_Item_Lock;
import simple.planner.dialog.BottomSheetDialog_SetReminder;
import simple.planner.dialog.BottomSheetDialog_WorkOnReminder;
import simple.planner.dialog.BottomSheetDialog_WorkOnItem_Lock;
import simple.planner.dialog.BottomSheetDialog_YesOrNo;
import simple.planner.realmObjects.Setting;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.realmObjects.realmHelpers.TODOAlarmHelper;
import simple.planner.realmObjects.realmHelpers.TODOListItemHelper;

public class LockScreenFragment extends Fragment {

    private static final String TAG = LockScreenFragment.class.getSimpleName();

    public static LockScreenFragment newInstance(boolean isCompleted) {
        LockScreenFragment fragment = new LockScreenFragment();
        Bundle args = new Bundle();
        args.putBoolean("isCompleted", isCompleted);
        fragment.setArguments(args);
        return fragment;
    }

    private Realm realm;
    private boolean isCompleted;
    private RecyclerView list;
    private LinearLayout emptyLayout;
    private LockScreenAdapter adapter;
    private BottomSheetDialog_WorkOnItem_Lock bottomSheetDialog_workOnItem_lock;
    private BottomSheetDialog_YesOrNo bottomSheetDialog_yesOrNo;
    private BottomSheetDialog_Item_Lock bottomSheetDialog_item_lock;
    private BottomSheetDialog_WorkOnReminder bottomSheetDialog_workOnReminder;
    private BottomSheetDialog_SetReminder bottomSheetDialog_setReminder;
    private BottomSheetDialog_Create_Lock bottomSheetDialog_create_lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = ((LockScreenActivity) requireActivity()).getRealm();
        LockScreenActivity parent = (LockScreenActivity) requireActivity();
        parent.setOnPlusBtnClickListener(new LockScreenActivity.OnPlusBtnClickListener() {
            @Override
            public void onPlusBtnClick() {
                showCreateItemDialog();
            }
        });

        if(getArguments()!=null) {
            isCompleted = getArguments().getBoolean("isCompleted",false);
        }

        connectViews(view);
        adapterSetting();
    }

    private void connectViews(View view) {
        list = view.findViewById(R.id.list);
        emptyLayout = view.findViewById(R.id.emptyLayout);
    }

    private void adapterSetting() {

        adapter = new LockScreenAdapter(requireActivity(), realm, isCompleted);
        adapter.setOnItemClickListener(new LockScreenAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TODOListItem item =  adapter.getItems().get(position);
                if(item==null) return;

                if(Build.VERSION.SDK_INT < 26) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), getResources().getString(R.string.feature_not_supported), Snackbar.LENGTH_LONG).show();
                    return;
                }
                showWorkOnItemDialog(item);

            }
        });
        adapter.setOnIconClickListener(new LockScreenAdapter.OnIconClickListener() {
            @Override
            public void onIconClick(int position) {
                TODOListItem item =  adapter.getItems().get(position);
                if(item==null) return;

                try {
                    realm.beginTransaction();
                        item.setDone(!isCompleted);
                    realm.commitTransaction();
                    clickEffect();
                } catch(Exception e) {
                    realm.cancelTransaction();
                }
            }
        });
        adapter.setDataChangeListener(new LockScreenAdapter.AdditionalDataChangeListener() {
            @Override
            public void onDataChange() {
                if(adapter.getItemCount()==0) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
            }
        });


        list.setAdapter(adapter);
        GapBorderDecoration itemDecoration = new GapBorderDecoration(requireActivity(), 1, Color.LTGRAY);
        list.addItemDecoration(itemDecoration);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));


        if(adapter.getItemCount()==0) {
            emptyLayout.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }

    }

    private void clickEffect() {

        try
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) // above 31
            {
                VibratorManager vibratorManager = (VibratorManager) requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE);
                CombinedVibration combinedVibration = CombinedVibration.createParallel(vibrationEffect);
                vibratorManager.vibrate(combinedVibration);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // above 26
            {
                Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    VibrationEffect effect = VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.vibrate(effect);
                }
            }
            else
            {
                Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {   // older than 26
                    vibrator.vibrate(5);
                }
            }
        }
        catch(Exception e)  {
            Log.e(TAG,"vibration effect error");
        }

    }


    private void showWorkOnItemDialog(@NonNull TODOListItem item) {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_workOnItem_lock = new BottomSheetDialog_WorkOnItem_Lock(item);
        bottomSheetDialog_workOnItem_lock.show(requireActivity().getSupportFragmentManager(), TAG);
        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_workOnItem_lock.getReadAndUpdate().setOnClickListener(v->{
            showReadAndUpdateDialog(item);
        });
        bottomSheetDialog_workOnItem_lock.getReminder().setOnClickListener(v->{
            if(item.getAlarm() == null) {
                showCreateReminderDialog(item);
            } else {
                showWorkOnReminderDialog(item);
            }
        });
        bottomSheetDialog_workOnItem_lock.getDelete().setOnClickListener(v->{
            bottomSheetDialog_workOnItem_lock.dismiss();
            showDeleteItemDialog(item.getId());
        });
        bottomSheetDialog_workOnItem_lock.getCancelButton().setOnClickListener(v->{
            bottomSheetDialog_workOnItem_lock.dismiss();
        });

    }

    private void showReadAndUpdateDialog(@NonNull TODOListItem item) {

        cleanAllBottomSheetDialog();

        Setting setting = realm.where(Setting.class).findFirst();
        if(setting == null) return;

        bottomSheetDialog_item_lock = new BottomSheetDialog_Item_Lock(item, setting.getLanguageCode());
        bottomSheetDialog_item_lock.show(requireActivity().getSupportFragmentManager(), TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_item_lock.getCloseBtn().setOnClickListener(v->{
            bottomSheetDialog_item_lock.dismiss();
        });
        bottomSheetDialog_item_lock.getSaveBtn().setOnClickListener(v->{

            String title    = bottomSheetDialog_item_lock.getTitle().getText().toString();
            String content  = bottomSheetDialog_item_lock.getContent().getText().toString();

            if(TextUtils.isEmpty(title)) {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_no_title), Toast.LENGTH_SHORT).show();
                return;
            }

            if ( TODOListItemHelper.updateTODO(item.getId(), title, content, item.isDone()) ) {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_save_successful), Toast.LENGTH_SHORT).show();
                bottomSheetDialog_item_lock.dismiss();
            } else {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_item_save_failed), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void showWorkOnReminderDialog(TODOListItem item) {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_workOnReminder = new BottomSheetDialog_WorkOnReminder();
        bottomSheetDialog_workOnReminder.show(requireActivity().getSupportFragmentManager(), TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_workOnReminder.getUpdate().setOnClickListener(v->{
            showCreateReminderDialog(item);
        });
        bottomSheetDialog_workOnReminder.getDelete().setOnClickListener(v->{
            showDeleteReminderDialog(item);
        });
        bottomSheetDialog_workOnReminder.getCancelBtn().setOnClickListener(v->{
            bottomSheetDialog_workOnReminder.dismiss();
        });

    }

    private void showDeleteReminderDialog(TODOListItem item) {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setWarningMode(true);
        bottomSheetDialog_yesOrNo.setText(getResources().getString(R.string.when_delete_an_alarm));
        bottomSheetDialog_yesOrNo.setYesBtnText(getResources().getString(R.string.btnText_yes));
        bottomSheetDialog_yesOrNo.setNoBtnText(getResources().getString(R.string.btnText_no));

        bottomSheetDialog_yesOrNo.show(requireActivity().getSupportFragmentManager(),TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_yesOrNo.dismiss();
        });
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{

            if ( TODOAlarmHelper.deleteAlarm(requireActivity(), item.getId()) ) {
                bottomSheetDialog_yesOrNo.dismiss();
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_alarm_deleted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showCreateReminderDialog(TODOListItem item) {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_setReminder = new BottomSheetDialog_SetReminder();
        bottomSheetDialog_setReminder.show(requireActivity().getSupportFragmentManager(),TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_setReminder.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_setReminder.dismiss();
        });
        bottomSheetDialog_setReminder.getYesButton().setOnClickListener(v->{

            int year = bottomSheetDialog_setReminder.getDatePicker().getYear();
            int month = bottomSheetDialog_setReminder.getDatePicker().getMonth();
            int day = bottomSheetDialog_setReminder.getDatePicker().getDayOfMonth();
            int hour = bottomSheetDialog_setReminder.getTimePicker().getHour();
            int minute = bottomSheetDialog_setReminder.getTimePicker().getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);
            long timeToSave = calendar.getTimeInMillis();

            TODOAlarmHelper.putAlarm(requireActivity(), item.getId(), timeToSave); // no need to delete former alarm if it exists. i already did program for that.

            bottomSheetDialog_setReminder.dismiss();
        });

    }

    private void showDeleteItemDialog(String itemId) {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setText(getResources().getString(R.string.when_delete_the_item));
        bottomSheetDialog_yesOrNo.setYesBtnText(getResources().getString(R.string.btnText_yes));
        bottomSheetDialog_yesOrNo.setNoBtnText(getResources().getString(R.string.btnText_no));
        bottomSheetDialog_yesOrNo.setWarningMode(true);
        bottomSheetDialog_yesOrNo.show(requireActivity().getSupportFragmentManager(), TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_yesOrNo.dismiss();
        });
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{

            boolean result = false;
            if( TODOAlarmHelper.deleteAlarm(requireActivity(), itemId) ) {
                result = TODOListItemHelper.deleteTODO(itemId);
            }

            if(result) {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_item_deleted), Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog_yesOrNo.dismiss();
        });

    }

    private void showCreateItemDialog() {

        cleanAllBottomSheetDialog();

        bottomSheetDialog_create_lock = new BottomSheetDialog_Create_Lock();
        bottomSheetDialog_create_lock.show(requireActivity().getSupportFragmentManager(),TAG);

        requireActivity().getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_create_lock.getCloseBtn().setOnClickListener(v->{
            bottomSheetDialog_create_lock.dismiss();
        });
        bottomSheetDialog_create_lock.getCreateBtn().setOnClickListener(v->{

            String title    = bottomSheetDialog_create_lock.getTitle().getText().toString();
            String content  = bottomSheetDialog_create_lock.getContent().getText().toString();

            if(TextUtils.isEmpty(title)) {
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_no_title), Toast.LENGTH_SHORT).show();
            } else {
                TODOListItemHelper.createTODO(title, content);
                Toast.makeText(requireActivity().getApplicationContext(), getResources().getString(R.string.when_item_created), Toast.LENGTH_SHORT).show();
                bottomSheetDialog_create_lock.dismiss();
            }

        });


    }

    private void cleanAllBottomSheetDialog() {


        if(bottomSheetDialog_workOnItem_lock != null) {
            bottomSheetDialog_workOnItem_lock.dismiss();
            bottomSheetDialog_workOnItem_lock = null;
        }

        if(bottomSheetDialog_item_lock != null) {
            bottomSheetDialog_item_lock.dismiss();
            bottomSheetDialog_item_lock = null;
        }

        if(bottomSheetDialog_yesOrNo != null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }

        if(bottomSheetDialog_workOnReminder != null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }

        if(bottomSheetDialog_setReminder != null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        if(bottomSheetDialog_create_lock != null) {
            bottomSheetDialog_create_lock.dismiss();
            bottomSheetDialog_create_lock = null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        cleanAllBottomSheetDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}