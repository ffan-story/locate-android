package com.feifan.sampling.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.feifan.sampling.R;
import com.libs.ui.fragments.BaseBottomDialogFragment;

/**
 * Created by mengmeng on 16/5/25.
 */
public class SettingDialog extends BaseBottomDialogFragment {
    public static final String TYPE = "type";
    public static final int FILE_TYPE = 0;
    public static final int TIME_TYPE = 1;
    public static final int SCAN_TYPE = 2;
    private SettingBtnCallBack mBtnCallback;
    private int mType = FILE_TYPE;

    public SettingDialog(SettingBtnCallBack callback){
        mBtnCallback = callback;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.biz_set_set_change_file,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        if(view != null){
            final EditText editText = (EditText) view.findViewById(R.id.edit);
            TextView titleView = (TextView) view.findViewById(R.id.title);
            Bundle bundle = getArguments();
            if(bundle != null){
                mType = bundle.getInt(TYPE,FILE_TYPE);
                switch (mType){
                    case FILE_TYPE:
                        editText.setHint("修改log保存位置");
                        titleView.setText("修改log保存位置");
                        break;
                    case TIME_TYPE:
                        editText.setHint("beacon扫描频率");
                        titleView.setText("beacon扫描频率");
                        break;
                    case SCAN_TYPE:
                        editText.setHint("扫描点数");
                        titleView.setText("扫描点数");
                        break;
                }
            }
            view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    if(mBtnCallback != null){
                        String changeStr = editText.getEditableText().toString();
                        if(!TextUtils.isEmpty(changeStr)){
                            mBtnCallback.onConfirmBtn(changeStr,mType);
                            SettingDialog.this.dismiss();
                        }else {
                            //TODO  toast
                        }
                    }
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingDialog.this.dismiss();
                }
            });
        }
    }

    public interface SettingBtnCallBack{
        public void onConfirmBtn(String str, int type);
    }
}
