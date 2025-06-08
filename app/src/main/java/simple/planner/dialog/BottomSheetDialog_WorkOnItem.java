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

public class BottomSheetDialog_WorkOnItem  extends BottomSheetDialogFragment
{
    private LinearLayout read;
    private LinearLayout update;
    private LinearLayout checked;
    private LinearLayout delete;
    private boolean checkedState;
    private Button cancelButton;

    public BottomSheetDialog_WorkOnItem(boolean defaultCheckedState) {
        this.checkedState = defaultCheckedState;
    }
    public LinearLayout getRead()       { return read; }
    public LinearLayout getUpdate()     { return update; }
    public LinearLayout getChecked()    { return checked; }
    public LinearLayout getDelete()     { return delete; }
    public Button getCancelButton()     { return cancelButton; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_work_on_item, container, false);

        this.read           = v.findViewById(R.id.read);
        this.update         = v.findViewById(R.id.update);
        this.checked        = v.findViewById(R.id.checked);
        this.delete         = v.findViewById(R.id.delete);
        this.cancelButton   = v.findViewById(R.id.cancelButton);

        if(checkedState){
            ((TextView) checked.findViewById(R.id.checkedText)).setText(getResources().getString(R.string.work_on_item_erase_mark));
            ((ImageView) checked.findViewById(R.id.checkedImage)).setAlpha(0.2f);
        } else {
            ((TextView) checked.findViewById(R.id.checkedText)).setText(getResources().getString(R.string.work_on_item_mark));
            ((ImageView) checked.findViewById(R.id.checkedImage)).setAlpha(1F);
        }

        return v;
    }
}
