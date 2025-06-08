package simple.planner.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;
import simple.planner.realmObjects.TODOListItem;

public class BottomSheetDialog_WorkOnItem_Lock extends BottomSheetDialogFragment {


    private @NonNull TextView title;
    private LinearLayout readAndUpdate;
    private LinearLayout reminder;
    private LinearLayout delete;
    private Button cancelButton;

    private TODOListItem item;

    public LinearLayout getReadAndUpdate() {
        return readAndUpdate;
    }

    public LinearLayout getReminder() {
        return reminder;
    }

    public LinearLayout getDelete() {
        return delete;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    @NonNull
    public TextView getTitle() {
        return title;
    }

    public BottomSheetDialog_WorkOnItem_Lock(@NonNull TODOListItem item) {
        this.item = item;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_work_on_item_lock, container, false);

        this.title          = v.findViewById(R.id.title);
        this.readAndUpdate  = v.findViewById(R.id.readOrEdit);
        this.reminder       = v.findViewById(R.id.reminder);
        this.delete         = v.findViewById(R.id.delete);
        this.cancelButton   = v.findViewById(R.id.cancelButton);

        title.setText(item.getTitle());

        return v;
    }

}
