package com.gao.jiefly.jieflysbooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TestActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_reader_config_popup);
        ButterKnife.inject(this);

    }
}
