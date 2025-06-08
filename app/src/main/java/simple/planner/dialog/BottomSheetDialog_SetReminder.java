package simple.planner.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;

public class BottomSheetDialog_SetReminder extends BottomSheetDialogFragment
{
    private TextView textView;
    private FlexboxLayout pickerBox;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button noButton;
    private Button yesButton;

    public TextView getTextView()       { return textView; }
    public FlexboxLayout getPickerBox() { return pickerBox; }
    public DatePicker getDatePicker()   { return datePicker; }
    public TimePicker getTimePicker()   { return timePicker; }
    public Button getNoButton()         { return noButton; }
    public Button getYesButton()        { return yesButton; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_set_reminder, container, false);

        this.textView       = v.findViewById(R.id.text);
        this.pickerBox      = v.findViewById(R.id.pickerBox);
        this.datePicker     = v.findViewById(R.id.datePicker);
        this.timePicker     = v.findViewById(R.id.timePicker);
        this.noButton       = v.findViewById(R.id.noButton);
        this.yesButton      = v.findViewById(R.id.yesButton);

        return v;
    }

}
