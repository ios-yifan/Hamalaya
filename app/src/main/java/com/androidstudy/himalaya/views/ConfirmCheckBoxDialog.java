package com.androidstudy.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidstudy.himalaya.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogActionListener clickListener= null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box);
        initView();
        initListener();
    }

    private void initListener() {
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    clickListener.onGiveUp(checked);
                    dismiss();
                }
            }
        });
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {

                    clickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancelSub = findViewById(R.id.dialog_check_box_cancel);
        mGiveUp = findViewById(R.id.dialog_check_box_confirm);
        mCheckBox = findViewById(R.id.dialog_check_box);
    }

    public void setOnDialogActionListener(OnDialogActionListener listener){
        this.clickListener = listener;
    }

    public interface OnDialogActionListener{
        void onCancelSubClick();

        void onGiveUp(boolean checked);
    }
}
