package com.feifan.locate.sampling.workline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feifan.locate.R;
import com.feifan.locate.widget.BaseFragment;

/**
 * Created by xuchunlei on 16/10/17.
 */

public final class LinePickerFragment extends BaseFragment implements View.OnClickListener {

    public static final String EXTRA_KEY_LINES = "lines";
    public static final String EXTRA_KEY_RESULT = "result";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_line_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String[] lines = getArguments().getStringArray(EXTRA_KEY_LINES);
        final LinearLayout container = (LinearLayout)view;
        container.setDividerDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        if(lines != null) {
            for(String line : lines) {
                TextView lineV = new TextView(getContext());
                lineV.setPadding(15, 15, 15, 15);
                lineV.setText(line);
                lineV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                lineV.setTag(line);
                lineV.setOnClickListener(this);
                container.addView(lineV);
            }
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.line_picker_title_text;
    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_RESULT, v.getTag().toString());
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }
}
