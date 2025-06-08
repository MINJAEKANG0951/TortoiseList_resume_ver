package simple.planner.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.util.TimeFormatter;

public class BottomSheetDialog_Item_Lock extends BottomSheetDialogFragment {

    private EditText title;
    private EditText content;
    private TextView created;
    private TextView updated;

    private LinearLayout scheduledContainer;
    private LinearLayout unScheduledContainer;
    private TextView scheduled;

    private LinearLayout closeBtn;
    private LinearLayout saveBtn;

    private @NonNull TODOListItem item;
    private @Nullable String languageCode;

    public BottomSheetDialog_Item_Lock(@NonNull TODOListItem item, @Nullable String languageCode) {
        this.item = item;
        this.languageCode = languageCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            setupRadio(bottomSheetDialog);
        });

        return dialog;
    }

    private void setupRadio(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if(bottomSheet == null) return;
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = getBottomSheetDialogDefaultHeight();
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getBottomSheetDialogDefaultHeight() {
        return getWindowHeight() * 85 / 100;
    }
    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) requireContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_lock_item, container, false);

        connectViews(v);
        viewSetting();

        return v;
    }

    private void connectViews(View v) {
        title = v.findViewById(R.id.title);
        content = v.findViewById(R.id.content);
        created = v.findViewById(R.id.created);
        updated = v.findViewById(R.id.updated);
        scheduled = v.findViewById(R.id.scheduled);
        scheduledContainer = v.findViewById(R.id.scheduledBox);
        unScheduledContainer = v.findViewById(R.id.unScheduledBox);
        closeBtn = v.findViewById(R.id.close);
        saveBtn = v.findViewById(R.id.save);
    }

    private void viewSetting() {

        title.setText(item.getTitle());
        content.setText(item.getContent());
        created.setText( TimeFormatter.toString(item.getCreated(), languageCode) );
        updated.setText( TimeFormatter.toString(item.getUpdated(), languageCode) );
        if(item.getAlarm() == null) {
            scheduledContainer.setVisibility(View.GONE);
            unScheduledContainer.setVisibility(View.VISIBLE);
        } else {
            scheduled.setText( TimeFormatter.toString(item.getAlarm().getScheduled(), languageCode) );
            unScheduledContainer.setVisibility(View.GONE);
            scheduledContainer.setVisibility(View.VISIBLE);
        }

    }


    public EditText getTitle() {
        return title;
    }

    public EditText getContent() {
        return content;
    }

    public LinearLayout getCloseBtn() {
        return closeBtn;
    }

    public LinearLayout getSaveBtn() {
        return saveBtn;
    }

    public TextView getCreated() {
        return created;
    }

    public TextView getUpdated() {
        return updated;
    }

    public LinearLayout getScheduledContainer() {
        return scheduledContainer;
    }

    public LinearLayout getUnScheduledContainer() {
        return unScheduledContainer;
    }

    public TextView getScheduled() {
        return scheduled;
    }

    @NonNull
    public TODOListItem getItem() {
        return item;
    }
}
