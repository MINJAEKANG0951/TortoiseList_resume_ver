package simple.planner.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import simple.planner.R;

public class BottomSheetDialog_YesOrNo extends BottomSheetDialogFragment {
    private TextView textView;
    private Button yesButton;
    private Button noButton;
    public Button getYesButton() {  return yesButton; }
    public Button getNoButton() {   return noButton; }

    private String text;
    private String yesBtnText;
    private String noBtnText;
    private boolean isWarningMode;
    private boolean isUpdateMode;

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setText(String text) { this.text = text; }
    public void setYesBtnText(String yesBtnText) { this.yesBtnText = yesBtnText; }
    public void setNoBtnText(String noBtnText) { this.noBtnText = noBtnText; }
    public void setWarningMode(boolean warningMode) {
        this.isWarningMode = warningMode;
    }

    public void setUpdateMode(boolean updateMode) { this.isUpdateMode = updateMode; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_yes_or_no, container, false);

        textView    = v.findViewById(R.id.text);
        yesButton   = v.findViewById(R.id.yesButton);
        noButton    = v.findViewById(R.id.noButton);

        if(text!=null)          textView.setText(text);
        if(yesBtnText!=null)    yesButton.setText(yesBtnText);
        if(noBtnText!=null)     noButton.setText(noBtnText);

        if(isUpdateMode) yesButton.setBackgroundResource(R.drawable.shape_button_yes2);
        if(isWarningMode) yesButton.setBackgroundResource(R.drawable.shape_button_yes3);

        return v;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener!=null) {
            onDismissListener.whenDismiss();
        }
    }

    public interface OnDismissListener {
        public void whenDismiss();
    }
}
