package simple.planner.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.dialog.BottomSheetDialog_SetReminder;
import simple.planner.dialog.BottomSheetDialog_WorkOnReminder;
import simple.planner.dialog.BottomSheetDialog_YesOrNo;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.realmObjects.realmHelpers.SettingHelper;
import simple.planner.realmObjects.realmHelpers.TODOAlarmHelper;
import simple.planner.realmObjects.realmHelpers.TODOListItemHelper;
import simple.planner.util.TimeFormatter;

public class TODOItemActivity extends BaseActivity {

    private Realm realm;
    private Context context;
    private OnBackPressedCallback backPressCallback;
    private String idReceived;
    private TODOListItem Todo;

    private ImageView backButton;
    private ImageView checkedStateBtn;
    private EditText title;
    private EditText content;
    private TextView created;
    private TextView updated;
    private LinearLayout scheduledBox;
    private TextView scheduled;
    private LinearLayout unScheduledBox;
    private LinearLayout closeBtn;
    private LinearLayout deleteBtn;
    private LinearLayout timerBtn;
    private LinearLayout saveBtn;

    private BottomSheetDialog_YesOrNo bottomSheetDialog_yesOrNo;
    private BottomSheetDialog_WorkOnReminder bottomSheetDialog_workOnReminder;
    private BottomSheetDialog_SetReminder bottomSheetDialog_setReminder;



    // --> get savedTitle/content from editText.getText()
    private long savedTime;
    private boolean savedDone;



    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_todo_item);
        context = this;
        realm = Realm.getDefaultInstance();

        Intent intent   = getIntent();
        idReceived      = intent.getStringExtra("TodoId");
        Todo            = realm.where(TODOListItem.class).equalTo("id",idReceived).findFirst();
        if(Todo == null) return;

        Todo.addChangeListener(new RealmObjectChangeListener<TODOListItem>() {
            @Override
            public void onChange(@Nullable TODOListItem item, @Nullable ObjectChangeSet changeSet) {
                if (changeSet != null && changeSet.isDeleted()) {
                    // Item has been deleted, close the page
                    finish();
                    return;
                }
                uploadViews();
            }
        });

        connectViews();
        uploadViews();
        backPressSetting();
    }

    private void connectViews() {

        backButton = findViewById(R.id.backButton);
        checkedStateBtn = findViewById(R.id.checkedState);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        created = findViewById(R.id.created);
        updated = findViewById(R.id.updated);
        scheduledBox = findViewById(R.id.scheduledBox);
        scheduled = findViewById(R.id.scheduled);
        unScheduledBox = findViewById(R.id.unScheduledBox);
        closeBtn = findViewById(R.id.close);
        deleteBtn = findViewById(R.id.delete);
        timerBtn = findViewById(R.id.timer);
        saveBtn = findViewById(R.id.save);

        backButton.setOnClickListener(v-> {
            if(backPressCallback!=null) backPressCallback.handleOnBackPressed();
        });
        checkedStateBtn.setOnClickListener(v->{
            showChangeCheckedStateDialog();
        });
        closeBtn.setOnClickListener(v->{
            if(backPressCallback!=null) backPressCallback.handleOnBackPressed();
        });
        deleteBtn.setOnClickListener(v->{
            showDeleteDialog();
        });
        timerBtn.setOnClickListener(v->{
            setTimerDialog();
        });
        saveBtn.setOnClickListener(v->{
            if( savedTodo() ) {
                finish();
            }
        });
    }

    private void uploadViews() {

        title.setText(Todo.getTitle());
        title.requestFocus();
        content.setText(Todo.getContent());
        created.setText( TimeFormatter.toString(Todo.getCreated(), SettingHelper.getLanguageCode()) );
        updated.setText( TimeFormatter.toString(Todo.getUpdated(), SettingHelper.getLanguageCode()) );
        checkedStateBtn.setAlpha(Todo.isDone()?1f:0.2f);
        if(Todo.getAlarm()==null) {
            unScheduledBox.setVisibility(View.VISIBLE);
            scheduledBox.setVisibility(View.GONE);
            savedTime = 0;
        } else {
            unScheduledBox.setVisibility(View.GONE);
            scheduledBox.setVisibility(View.VISIBLE);
            scheduled.setText(TimeFormatter.toString( Todo.getAlarm().getScheduled(), SettingHelper.getLanguageCode() ));
            savedTime = Todo.getAlarm().getScheduled();
        }
        savedDone = Todo.isDone();

    }

    private void backPressSetting() {
        backPressCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                boolean isThereUnsavedCharges = false;
                if( !title.getText().toString().equals(Todo.getTitle()))      isThereUnsavedCharges = true;
                if( !content.getText().toString().equals(Todo.getContent()))   isThereUnsavedCharges = true;
                if( Todo.getAlarm() == null ) {
                    if(savedTime != 0) isThereUnsavedCharges = true;
                } else {
                    if (Todo.getAlarm().getScheduled() != savedTime) isThereUnsavedCharges = true;
                }
                if (Todo.isDone() != savedDone) {
                    isThereUnsavedCharges = true;
                }

                if(isThereUnsavedCharges) {
                    showUnsavedDialog();
                } else {
                    finish();
                }
            }
        };

        // Add the callback to the dispatcher
        getOnBackPressedDispatcher().addCallback(this, backPressCallback);
    }


    private void showChangeCheckedStateDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setNoBtnText("no");
        bottomSheetDialog_yesOrNo.setYesBtnText("yes");
        bottomSheetDialog_yesOrNo.setText(
                savedDone?
                        getResources().getString(R.string.when_uncheck_clicked):
                        getResources().getString(R.string.when_check_clicked)
        );
        bottomSheetDialog_yesOrNo.show(getSupportFragmentManager(),null);
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{
            savedDone = !savedDone;
            checkedStateBtn.setAlpha(savedDone?1f:0.2f);
            bottomSheetDialog_yesOrNo.dismiss();
        });
        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_yesOrNo.dismiss();
        });

    }
    private void showDeleteDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setWarningMode(true);
        bottomSheetDialog_yesOrNo.setText(getResources().getString(R.string.when_delete_the_item));
        bottomSheetDialog_yesOrNo.show(getSupportFragmentManager(),null);
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->bottomSheetDialog_yesOrNo.dismiss());
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{
            if( TODOAlarmHelper.deleteAlarm( context ,Todo.getId() ) ) {
                if ( TODOListItemHelper.deleteTODO( Todo.getId() ) ) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_item_deleted), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_item_delete_failed), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_item_delete_failed), Toast.LENGTH_SHORT).show();
            }
            bottomSheetDialog_yesOrNo.dismiss();
        });

    }
    private void setTimerDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        if(savedTime == 0)
        {
            createPickerDialog();
        }
        else
        {

            bottomSheetDialog_workOnReminder = new BottomSheetDialog_WorkOnReminder();
            bottomSheetDialog_workOnReminder.show(getSupportFragmentManager(), bottomSheetDialog_workOnReminder.getTag());
            getSupportFragmentManager().executePendingTransactions();

            bottomSheetDialog_workOnReminder.getDelete().setOnClickListener(v->{
                bottomSheetDialog_workOnReminder.dismiss();
                createAlarmDeleteDialog();
            });
            bottomSheetDialog_workOnReminder.getUpdate().setOnClickListener(v->{
                bottomSheetDialog_workOnReminder.dismiss();
                createPickerDialog();
            });
            bottomSheetDialog_workOnReminder.getCancelBtn().setOnClickListener(v->{
                bottomSheetDialog_workOnReminder.dismiss();
            });

        }



    }

    private void createPickerDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }


        bottomSheetDialog_setReminder = new BottomSheetDialog_SetReminder();
        bottomSheetDialog_setReminder.show(getSupportFragmentManager(), bottomSheetDialog_setReminder.getTag());
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_setReminder.getYesButton().setOnClickListener(view -> {

            int year = bottomSheetDialog_setReminder.getDatePicker().getYear();
            int month = bottomSheetDialog_setReminder.getDatePicker().getMonth();
            int day = bottomSheetDialog_setReminder.getDatePicker().getDayOfMonth();
            int hour = bottomSheetDialog_setReminder.getTimePicker().getHour();
            int minute = bottomSheetDialog_setReminder.getTimePicker().getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);
            savedTime = calendar.getTimeInMillis();

            if(savedTime!=0) {
                scheduledBox.setVisibility(View.VISIBLE);
                scheduled.setText(TimeFormatter.toString(savedTime, SettingHelper.getLanguageCode()));
                unScheduledBox.setVisibility(View.GONE);
            }

            bottomSheetDialog_setReminder.dismiss();
        });
        bottomSheetDialog_setReminder.getNoButton().setOnClickListener(view -> {
            bottomSheetDialog_setReminder.dismiss();
        });

    }


    private void createAlarmDeleteDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setWarningMode(true);

        bottomSheetDialog_yesOrNo.setText(getResources().getString(R.string.when_delete_an_alarm));
        bottomSheetDialog_yesOrNo.setYesBtnText(getResources().getString(R.string.btnText_delete));
        bottomSheetDialog_yesOrNo.setNoBtnText(getResources().getString(R.string.btnText_cancel));

        bottomSheetDialog_yesOrNo.show(getSupportFragmentManager(),null);
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_yesOrNo.dismiss();
        });
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{

            savedTime = 0;
            scheduledBox.setVisibility(View.GONE);
            unScheduledBox.setVisibility(View.VISIBLE);

            bottomSheetDialog_yesOrNo.dismiss();
        });


    }


    private void showUnsavedDialog() {

        if(bottomSheetDialog_yesOrNo!=null) {
            bottomSheetDialog_yesOrNo.dismiss();
            bottomSheetDialog_yesOrNo = null;
        }
        if(bottomSheetDialog_workOnReminder !=null) {
            bottomSheetDialog_workOnReminder.dismiss();
            bottomSheetDialog_workOnReminder = null;
        }
        if(bottomSheetDialog_setReminder!=null) {
            bottomSheetDialog_setReminder.dismiss();
            bottomSheetDialog_setReminder = null;
        }

        bottomSheetDialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        bottomSheetDialog_yesOrNo.setUpdateMode(true);
        bottomSheetDialog_yesOrNo.setText(getResources().getString(R.string.when_unsaved_changes));
        bottomSheetDialog_yesOrNo.setYesBtnText(getResources().getString(R.string.btnText_save));
        bottomSheetDialog_yesOrNo.setNoBtnText(getResources().getString(R.string.btnText_exit));
        bottomSheetDialog_yesOrNo.show(getSupportFragmentManager(),null);
        getSupportFragmentManager().executePendingTransactions();

        bottomSheetDialog_yesOrNo.getNoButton().setOnClickListener(v->{
            bottomSheetDialog_yesOrNo.dismiss();
            finish();
        });
        bottomSheetDialog_yesOrNo.getYesButton().setOnClickListener(v->{
            if( savedTodo() ) {
                bottomSheetDialog_yesOrNo.dismiss();
                finish();
            } else {
                bottomSheetDialog_yesOrNo.dismiss();
            }
        });

    }

    private boolean savedTodo() {

        TODOListItem subject = realm.where(TODOListItem.class).equalTo("id",idReceived).findFirst();
        if(subject == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_item_not_exist), Toast.LENGTH_SHORT).show();
            finish();
        }


        if(title.getText()==null ||title.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_no_title), Toast.LENGTH_SHORT).show();
            return false;
        }

        // alarm update
        boolean alarmSaveResult = false;
        if(savedTime==0) {
            alarmSaveResult = TODOAlarmHelper.deleteAlarm(context, Todo.getId());
        } else {
            if(Todo.getAlarm()!=null && Todo.getAlarm().getScheduled() == savedTime) {
                alarmSaveResult = true;
            } else {
                alarmSaveResult = TODOAlarmHelper.putAlarm(context, Todo.getId(), savedTime);
            }
        }
        if(!alarmSaveResult) Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_alarm_save_failed), Toast.LENGTH_SHORT).show();


        // item update
        boolean itemSaveResult = false;
        itemSaveResult = TODOListItemHelper.updateTODO(
                Todo.getId(),
                title.getText().toString(),
                content.getText().toString(),
                savedDone
        );
        if(!itemSaveResult) Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_item_save_failed), Toast.LENGTH_SHORT).show();

        if(alarmSaveResult && itemSaveResult) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_save_successful), Toast.LENGTH_SHORT).show();
        }

        return true;
    }

}
