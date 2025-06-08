package simple.planner.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.dialog.BottomSheetDialog_SetReminder;
import simple.planner.dialog.BottomSheetDialog_WorkOnReminder;
import simple.planner.dialog.BottomSheetDialog_YesOrNo;
import simple.planner.realmObjects.realmHelpers.SettingHelper;
import simple.planner.realmObjects.realmHelpers.TODOAlarmHelper;
import simple.planner.realmObjects.realmHelpers.TODOListItemHelper;
import simple.planner.util.TimeFormatter;

public class TODOCreateActivity extends BaseActivity
{
    private ImageView backButton;
    private LinearLayout setAlarmButton;
    private LinearLayout createButton;
    private LinearLayout cancelButton;
    private EditText etTitle;
    private EditText etContent;
    private LinearLayout scheduledContainer;
    private TextView tvScheduled;
    private LinearLayout unscheduledContainer;
    private BottomSheetDialog_SetReminder dialog_setReminder;
    private BottomSheetDialog_WorkOnReminder dialog_workOnAlarm;
    private BottomSheetDialog_YesOrNo dialog_yesOrNo;

    private long scheduled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_create);

        backButton              = findViewById(R.id.backButton);
        setAlarmButton          = findViewById(R.id.timer);
        createButton            = findViewById(R.id.create);
        cancelButton            = findViewById(R.id.close);
        etTitle                 = findViewById(R.id.title);
        etContent               = findViewById(R.id.content);
        scheduledContainer      = findViewById(R.id.scheduledBox);
        tvScheduled             = findViewById(R.id.scheduled);
        unscheduledContainer    = findViewById(R.id.unScheduledBox);
        scheduled               = 0;

        etTitle.requestFocus();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scheduled == 0) {
                    showAlarmSettingDialog();
                } else {
                    showWorkOnAlarmDialog();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title    = etTitle.getText().toString();
                String content  = etContent.getText().toString();

                if(TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_no_title), Toast.LENGTH_SHORT).show();
                    return;
                }

                createTODO(title, content);
                finish();
            }
        });


        // refresh view
        setScheduled(scheduled);
    }

    private void createTODO(String title, String content)
    {
        if(scheduled==0) {
            TODOListItemHelper.createTODO(title, content);
        } else {
            String newId = TODOListItemHelper.createTODO(title, content);
            TODOAlarmHelper.putAlarm(this, newId, scheduled);
        }
    }

    private void showAlarmSettingDialog() {
        if(dialog_setReminder !=null) dialog_setReminder.dismiss();
        if(dialog_workOnAlarm !=null) dialog_workOnAlarm.dismiss();
        if(dialog_yesOrNo != null) dialog_yesOrNo.dismiss();

        dialog_setReminder = new BottomSheetDialog_SetReminder();
        dialog_setReminder.show(getSupportFragmentManager(), dialog_setReminder.getTag());
        getSupportFragmentManager().executePendingTransactions();

        dialog_setReminder.getYesButton().setOnClickListener(view -> {

            int year = dialog_setReminder.getDatePicker().getYear();
            int month = dialog_setReminder.getDatePicker().getMonth();
            int day = dialog_setReminder.getDatePicker().getDayOfMonth();
            int hour = dialog_setReminder.getTimePicker().getHour();
            int minute = dialog_setReminder.getTimePicker().getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);
            long scheduledTimestamp = calendar.getTimeInMillis();

            setScheduled(scheduledTimestamp);
            dialog_setReminder.dismiss();
        });
        dialog_setReminder.getNoButton().setOnClickListener(view -> {
            dialog_setReminder.dismiss();
        });

    }
    private void showWorkOnAlarmDialog() {
        if(dialog_setReminder !=null) dialog_setReminder.dismiss();
        if(dialog_workOnAlarm !=null) dialog_workOnAlarm.dismiss();
        if(dialog_yesOrNo != null) dialog_yesOrNo.dismiss();

        dialog_workOnAlarm = new BottomSheetDialog_WorkOnReminder();
        dialog_workOnAlarm.show(getSupportFragmentManager(), dialog_workOnAlarm.getTag());
        getSupportFragmentManager().executePendingTransactions();

        dialog_workOnAlarm.getUpdate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlarmSettingDialog();
            }
        });
        dialog_workOnAlarm.getDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlarmDeleteDialog();
            }
        });
        dialog_workOnAlarm.getCancelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_workOnAlarm.dismiss();
            }
        });

    }

    private void showAlarmDeleteDialog() {
        if(dialog_setReminder !=null) dialog_setReminder.dismiss();
        if(dialog_workOnAlarm !=null) dialog_workOnAlarm.dismiss();
        if(dialog_yesOrNo != null) dialog_yesOrNo.dismiss();

        dialog_yesOrNo = new BottomSheetDialog_YesOrNo();
        dialog_yesOrNo.setText(getResources().getString(R.string.when_delete_an_alarm));
        dialog_yesOrNo.setYesBtnText(getResources().getString(R.string.btnText_yes));
        dialog_yesOrNo.setNoBtnText(getResources().getString(R.string.btnText_no));

        dialog_yesOrNo.show(getSupportFragmentManager(), dialog_yesOrNo.getTag());
        getSupportFragmentManager().executePendingTransactions();

        dialog_yesOrNo.getYesButton().setOnClickListener(view -> {
            setScheduled(0);
            dialog_yesOrNo.dismiss();
        });
        dialog_yesOrNo.getNoButton().setOnClickListener(view -> {
            dialog_yesOrNo.dismiss();
        });

    }

    private void setScheduled(long scheduled) {

        this.scheduled = scheduled;
        String timeStr = TimeFormatter.toString(this.scheduled, SettingHelper.getLanguageCode());
        tvScheduled.setText(timeStr);

        if(scheduled==0) {
            unscheduledContainer.setVisibility(View.VISIBLE);
            scheduledContainer.setVisibility(View.GONE);
        } else {
            unscheduledContainer.setVisibility(View.GONE);
            scheduledContainer.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
