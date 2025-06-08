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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;

public class BottomSheetDialog_Create_Lock extends BottomSheetDialogFragment {

    private EditText title;
    private EditText content;
    private LinearLayout closeBtn;
    private LinearLayout createBtn;

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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) requireContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_lock_create, container, false);

        connectViews(v);

        return v;
    }

    private void connectViews(View v) {

        title = v.findViewById(R.id.title);
        content = v.findViewById(R.id.content);
        closeBtn = v.findViewById(R.id.close);
        createBtn = v.findViewById(R.id.create);

    }

    public EditText getTitle() {
        return title;
    }

    public void setTitle(EditText title) {
        this.title = title;
    }

    public EditText getContent() {
        return content;
    }

    public void setContent(EditText content) {
        this.content = content;
    }

    public LinearLayout getCloseBtn() {
        return closeBtn;
    }

    public void setCloseBtn(LinearLayout closeBtn) {
        this.closeBtn = closeBtn;
    }

    public LinearLayout getCreateBtn() {
        return createBtn;
    }

    public void setCreateBtn(LinearLayout createBtn) {
        this.createBtn = createBtn;
    }
}
