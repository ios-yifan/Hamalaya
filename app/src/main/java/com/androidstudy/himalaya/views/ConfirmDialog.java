package com.androidstudy.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidstudy.himalaya.R;

public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogActionListener clickListener= null;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onGiveUp();
                    dismiss();
                }
            }
        });
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCancelSubClick();
                dismiss();
            }
        });
    }

    private void initView() {
        mCancelSub = findViewById(R.id.dialog_cancel_sub);
        mGiveUp = findViewById(R.id.dialog_give_up_tv);
    }

    public void setOnDialogActionListener(OnDialogActionListener listener){
        this.clickListener = listener;
    }

    public interface OnDialogActionListener{
        void onCancelSubClick();

        void onGiveUp();
    }
}
