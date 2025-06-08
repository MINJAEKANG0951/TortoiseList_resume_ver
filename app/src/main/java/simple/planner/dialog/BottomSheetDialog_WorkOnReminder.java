package simple.planner.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;

public class BottomSheetDialog_WorkOnReminder extends BottomSheetDialogFragment
{
    private LinearLayout update;
    private LinearLayout delete;
    private Button cancelBtn;

    public LinearLayout getUpdate()     { return update; }
    public LinearLayout getDelete()     { return delete; }
    public Button getCancelBtn()        { return cancelBtn; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_work_on_reminder, container, false);

        this.update         = v.findViewById(R.id.update);
        this.delete         = v.findViewById(R.id.delete);
        this.cancelBtn      = v.findViewById(R.id.cancelButton);

        return v;
    }
}
