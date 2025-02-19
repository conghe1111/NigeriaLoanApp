package com.chocolate.nigerialoanapp.ui.loading;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chocolate.nigerialoanapp.R;


public class ProgressDialogFragment extends DialogFragment {

    private boolean mCancelFlag = false;
    private String mMsg = "";

    public ProgressDialogFragment(boolean cancelFlag, String msg) {
        mCancelFlag = cancelFlag;
        mMsg = msg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int theme = resolveTheme();
        Dialog dialog = new Dialog(getActivity(), theme);

        Bundle args = getArguments();
        if (args != null) {
            dialog.setCanceledOnTouchOutside(mCancelFlag);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvLoading = view.findViewById(R.id.tv_login_dialog);
        tvLoading.setText(mMsg);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowTraceCallback traceCallback = new WindowTraceCallback(window.getCallback());
                window.setCallback(traceCallback);
            }
        }
    }

    @Override
    public void onDestroyView() {
        // bug in the compatibility library
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    /**
     * Resolves the theme to be used for the dialog.
     *
     * @return The theme.
     */
    @StyleRes
    public int resolveTheme() {
        return R.style.LoadingDialog;
    }

}
