package com.amperas17.wonderstest.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;


public class LoadingDialog extends DialogFragment {
    private static final String TAG = "progress";

    public static void show(FragmentManager fm){
        DialogFragment d = new LoadingDialog();
        d.show(fm, TAG);
    }

    public static void dismiss(FragmentManager fm){
        DialogFragment d = (DialogFragment) fm.findFragmentByTag(TAG);
        d.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getParentFragment()!= null){
            if (getParentFragment() instanceof ILoadingDialog){
                ((ILoadingDialog) getParentFragment()).onLoadingDialogCancel();
            } else {
                Log.d(TAG, "onCancel: " + getParentFragment() + "should implement onAuthProgressCancel");
            }
        } else {
            if (getActivity()!=null){
                if (getActivity() instanceof ILoadingDialog){
                    ((ILoadingDialog) getActivity()).onLoadingDialogCancel();
                } else {
                    Log.d(TAG, "onCancel: " + getActivity() + "should implement onAuthProgressCancel");
                }
            }
        }
    }

    public interface ILoadingDialog{
        void onLoadingDialogCancel();
    }
}
