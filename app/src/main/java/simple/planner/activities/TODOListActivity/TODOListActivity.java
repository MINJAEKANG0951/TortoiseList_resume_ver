package simple.planner.activities.TODOListActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import simple.planner.BaseActivity;
import simple.planner.R;
import simple.planner.activities.SettingActivity;
import simple.planner.activities.TODOCreateActivity;
import simple.planner.activities.TODOItemActivity;
import simple.planner.design.GapBorderDecoration;
import simple.planner.dialog.BottomSheetDialog_YesOrNo;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.realmObjects.realmHelpers.TODOAlarmHelper;
import simple.planner.realmObjects.realmHelpers.TODOListItemHelper;

public class TODOListActivity extends BaseActivity
{
    private final String TAG = getClass().getSimpleName();

    private CheckBox checkBox;
    private ImageView setting;
    private LinearLayout progress;
    private CardView editButton;
    private CardView plusButton;
    private CardView cancelButton;
    private CardView checkButton;
    private CardView uncheckButton;
    private CardView deleteButton;
    private LinearLayout normalMode;
    private LinearLayout editMode;
    private RecyclerView todoList;
    private ScrollView background;
    private TODOListAdapter adapter;
    private BottomSheetDialog_YesOrNo dialog_yesOrNo;

    private Context context;
    private Realm realm;
    private RealmResults<TODOListItem> totalItems;
    private RealmResults<TODOListItem> completedItems;


    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            appSwitch = true;
        }
    };

    private boolean appSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        context = this;

        backPressSetting();


        checkBox        = findViewById(R.id.checkBox);
        setting         = findViewById(R.id.setting);
        progress        = findViewById(R.id.progress);
        editButton      = findViewById(R.id.edit);
        plusButton      = findViewById(R.id.plus);
        cancelButton    = findViewById(R.id.undo);
        checkButton     = findViewById(R.id.check);
        uncheckButton   = findViewById(R.id.uncheck);
        deleteButton    = findViewById(R.id.delete);
        normalMode      = findViewById(R.id.normal_mode);
        editMode        = findViewById(R.id.edit_mode);
        todoList        = findViewById(R.id.todoList);
        background      = findViewById(R.id.background);

        appSwitch       = true;

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(adapter!=null){
                    for(int i=0;i<adapter.getItemCheckedStates().size();i++){
                        adapter.getItemCheckedStates().set(i,isChecked);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TODOListActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TODOListActivity.this, TODOCreateActivity.class);
                startActivity(intent);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter!=null && !adapter.getItemCheckedStates().contains(true)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_no_item_selected), Toast.LENGTH_SHORT).show();
                    return;
                }

                String text         = getResources().getString(R.string.when_check_items);
                String yesBtnText   = getResources().getString(R.string.btnText_yes);
                String noBtnText    = getResources().getString(R.string.btnText_no);
                createCustomDialog(text,yesBtnText,noBtnText,()->{
                    changeSelectedToDone();
                    checkBox.setChecked(false);
                }, false);
            }
        });
        uncheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter!=null && !adapter.getItemCheckedStates().contains(true)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_no_item_selected), Toast.LENGTH_SHORT).show();
                    return;
                }

                String text         = getResources().getString(R.string.when_uncheck_items);
                String yesBtnText   = getResources().getString(R.string.btnText_yes);
                String noBtnText    = getResources().getString(R.string.btnText_no);
                createCustomDialog(text,yesBtnText,noBtnText,()->{
                    changeSelectedToUndone();
                    checkBox.setChecked(false);
                }, false);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapter!=null && !adapter.getItemCheckedStates().contains(true)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_no_item_selected), Toast.LENGTH_SHORT).show();
                    return;
                }

                String text         = getResources().getString(R.string.when_delete_items);
                String yesBtnText   = getResources().getString(R.string.btnText_yes);
                String noBtnText    = getResources().getString(R.string.btnText_no);
                createCustomDialog(text,yesBtnText,noBtnText,()->{
                    deleteSelected();
                    checkBox.setChecked(false);
                }, true);
            }
        });
        todoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scrolling down
                    setButtonsAlpha(0.5f);
                } else if (dy < 0) { // Scrolling up
                    setButtonsAlpha(0.5f);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // Scrolling stopped
                    setButtonsAlpha(1f);
                }
            }

            private void setButtonsAlpha(float alpha) {
                progress.setAlpha(alpha);
                editButton.setAlpha(alpha);
                plusButton.setAlpha(alpha);
                cancelButton.setAlpha(alpha);
                checkButton.setAlpha(alpha);
                uncheckButton.setAlpha(alpha);
                deleteButton.setAlpha(alpha);
            }
        });


        //  SHOW COMPLETED/TOTAL
        realm           = Realm.getDefaultInstance();
        totalItems      = realm.where(TODOListItem.class).findAllAsync();
        completedItems  = realm.where(TODOListItem.class).equalTo("done",true).findAllAsync();
        ((TextView) progress.findViewById(R.id.progress_total)).setText( totalItems.size() + "" );
        ((TextView) progress.findViewById(R.id.progress_completed)).setText( completedItems.size() + "" );
        totalItems.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @Override
            public void onChange(RealmResults<TODOListItem> todoListItems) {
                ((TextView) progress.findViewById(R.id.progress_total)).setText( todoListItems.size() + "" );
            }
        });
        completedItems.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @Override
            public void onChange(RealmResults<TODOListItem> todoListItems) {
                ((TextView) progress.findViewById(R.id.progress_completed)).setText( todoListItems.size() + "" );
            }
        });


        //  ADAPTER
        adapter         = new TODOListAdapter(this, realm);
        adapter.setDataChangeListener(new TODOListAdapter.AdditionalDataChangeListener() {
            @Override
            public void onDataChange() {
                if(adapter.getItemCheckedStates().size()==0) {
                    todoList.setVisibility(View.GONE);
                    background.setVisibility(View.VISIBLE);
                } else {
                    todoList.setVisibility(View.VISIBLE);
                    background.setVisibility(View.GONE);
                }
            }
        });
        adapter.setOnItemClickListener(new TODOListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if(adapter.isEditMode()) {
                    adapter.getItemCheckedStates().set(position, !adapter.getItemCheckedStates().get(position));
                    adapter.notifyItemChanged(position);
                    return;
                }

                TODOListItem selected   = adapter.getData().get(position);
                if(selected==null) return;

                String id = selected.getId();
                Intent intent = new Intent(TODOListActivity.this, TODOItemActivity.class);
                intent.putExtra("TodoId",id);
                startActivity(intent);

            }
        });
        adapter.setOnItemLongClickListener(new TODOListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

                if(!adapter.isEditMode()) {
                    changeMode();
                    adapter.getItemCheckedStates().set(position, !adapter.getItemCheckedStates().get(position));
                    adapter.notifyItemChanged(position);
                }

            }
        });
        todoList.setAdapter(adapter);
        todoList.setLayoutManager(new LinearLayoutManager(this));
        GapBorderDecoration itemDecoration = new GapBorderDecoration(this, 1, Color.LTGRAY);
        todoList.addItemDecoration(itemDecoration);

        if(adapter.getItemCheckedStates().size()==0) {
            todoList.setVisibility(View.GONE);
            background.setVisibility(View.VISIBLE);
        } else {
            todoList.setVisibility(View.VISIBLE);
            background.setVisibility(View.GONE);
        }

    }


    public void changeMode()
    {
        adapter.setEditMode(!adapter.isEditMode());
        if(adapter.isEditMode())
        {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setScaleX(0f);
            checkBox.setScaleY(0f);
            checkBox.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
            normalMode.setVisibility(View.GONE);
            editMode.setVisibility(View.VISIBLE);
            editMode.setScaleX(0f);
            editMode.setScaleY(0f);
            editMode.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
        }
        else
        {
            checkBox.setVisibility(View.INVISIBLE);
            editMode.setVisibility(View.GONE);
            normalMode.setVisibility(View.VISIBLE);
            normalMode.setScaleX(0f);
            normalMode.setScaleY(0f);
            normalMode.animate().scaleX(1f).scaleY(1f).setDuration(300).start();

            checkBox.setChecked(false);
            for(int i=0;i<adapter.getItemCheckedStates().size();i++)
            {
                adapter.getItemCheckedStates().set(i,false);
            }
        }
    }
    public void deleteSelected() {

        boolean isSuccessful = true;

        List<TODOListItem> itemsToDelete = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCheckedStates().size(); i++) {
            if(adapter.getItemCheckedStates().get(i)) {
                itemsToDelete.add(adapter.getData().get(i));
            }
        }

        for( TODOListItem item : itemsToDelete ) {
            if(TODOAlarmHelper.deleteAlarm(context, item.getId())) {
                boolean result = TODOListItemHelper.deleteTODO(item.getId());
                if(!result) isSuccessful = false;
            }
        }

        if(isSuccessful) Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_items_deleted), Toast.LENGTH_SHORT).show();
    }
    public void changeSelectedToDone() {

        List<TODOListItem> itemsToChange = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCheckedStates().size(); i++) {
            if(adapter.getItemCheckedStates().get(i)) {
                itemsToChange.add(adapter.getData().get(i));
            }
        }
        for (TODOListItem item : itemsToChange) {
            boolean result = TODOListItemHelper.updateTODO(item.getId(),item.getTitle(),item.getContent(),true);
        }


    }
    public void changeSelectedToUndone() {

        List<TODOListItem> itemsToChange = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCheckedStates().size(); i++) {
            if(adapter.getItemCheckedStates().get(i)) {
                itemsToChange.add(adapter.getData().get(i));
            }
        }
        for (TODOListItem item : itemsToChange) {
            boolean result = TODOListItemHelper.updateTODO(item.getId(),item.getTitle(),item.getContent(),false);
        }
    }

    public void createCustomDialog(String text, String yesBtnText, String noBtnText, Runnable logic, boolean isWarningMode)
    {
        if(dialog_yesOrNo != null) { dialog_yesOrNo.dismiss(); }

        dialog_yesOrNo  =   new BottomSheetDialog_YesOrNo();
        dialog_yesOrNo.setText(text);
        dialog_yesOrNo.setYesBtnText(yesBtnText);
        dialog_yesOrNo.setNoBtnText(noBtnText);
        dialog_yesOrNo.setWarningMode(isWarningMode);

        dialog_yesOrNo.show(getSupportFragmentManager(), dialog_yesOrNo.getTag());
        getSupportFragmentManager().executePendingTransactions();
        // await code for dialog's onCreateView

        dialog_yesOrNo.getYesButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic.run();
                dialog_yesOrNo.dismiss();
            }
        });
        dialog_yesOrNo.getNoButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_yesOrNo.dismiss();
            }
        });
    }

    private void backPressSetting() {
        // Create a callback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event here
                if(adapter.isEditMode()) {
                    changeMode();
                } else if(appSwitch) {

                    appSwitch = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.when_back_pressed), Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 2000);

                } else {
                    finishAffinity();
                }
            }
        };

        // Add the callback to the dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    protected void onResume() {
        super.onResume();
        appSwitch = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter!=null) adapter.closeRealm();
        if(realm!=null) realm.close();
        handler.removeCallbacks(runnable);
    }
}
