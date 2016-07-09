package com.gao.jiefly.jieflysbooks.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.gao.jiefly.jieflysbooks.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jiefly on 2016/7/9.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class ReaderSetFragment extends Fragment {
    View mView;
    @InjectView(R.id.id_set_popup_lightup_ibtn)
    ImageButton mIdSetPopupLightupIbtn;
    @InjectView(R.id.id_set_popup_sb)
    SeekBar mIdSetPopupSb;
    @InjectView(R.id.id_set_popup_lightdown_ibtn)
    ImageButton mIdSetPopupLightdownIbtn;
    @InjectView(R.id.id_set_popup_light_follow_sys_cb)
    CheckBox mIdSetPopupLightFollowSysCb;
    @InjectView(R.id.id_set_popup_radio_group)
    RadioGroup mIdSetPopupRadioGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.set_reader_config_popup, container, false);
        ButterKnife.inject(this, mView);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        mIdSetPopupRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("tag",""+checkedId);
            }
        });
    }
}
