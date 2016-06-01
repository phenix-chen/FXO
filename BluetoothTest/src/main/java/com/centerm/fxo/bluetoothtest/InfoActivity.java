package com.centerm.fxo.bluetoothtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.centerm.fxo.lib.FXOManager;
import com.centerm.fxo.lib.RegClass;


public class InfoActivity extends Activity {

    private static final String TAG = "Centerm:InfoActivity";

    private final FXOManager manager;

    /*水平和垂直*/
    private TextView pl_status;
    private TextView pl_count;
    private TextView pl_bf_zcomp;
    private TextView pl_ths_reg;

    /*下落和运动*/
    private TextView a_ffmt_cfg;
    private TextView a_ffmt_src;
    private TextView a_ffmt_ths;
    private TextView a_ffmt_count;

    /*加速度矢量*/
    private TextView a_vecm_cfg;
    private TextView a_vecm_ths_msb;
    private TextView a_vecm_ths_lsb;
    private TextView a_vecm_cnt;
    private TextView a_vecm_initx_msb;
    private TextView a_vecm_initx_lsb;
    private TextView a_vecm_inity_msb;
    private TextView a_vecm_inity_lsb;
    private TextView a_vecm_initz_msb;
    private TextView a_vecm_initz_lsb;

    /*瞬时加速度*/
    private TextView transient_cfg;
    private TextView transient_src;
    private TextView transient_ths;
    private TextView transient_count;

    /*脉冲*/
    private TextView pulse_cfg;
    private TextView pulse_src;
    private TextView pulse_thsx;
    private TextView pulse_thsy;
    private TextView pulse_thsz;
    private TextView pulse_tmlt;
    private TextView pulse_ltcy;
    private TextView pulse_wind;

    /*磁力计*/
    private TextView m_dr_status;
    private TextView m_out_x_msb;
    private TextView m_out_x_lsb;
    private TextView m_out_y_msb;
    private TextView m_out_y_lsb;
    private TextView m_out_z_msb;
    private TextView m_out_z_lsb;
    private TextView cmp_x_msb;
    private TextView cmp_x_lsb;
    private TextView cmp_y_msb;
    private TextView cmp_y_lsb;
    private TextView cmp_z_msb;
    private TextView cmp_z_lsb;
    private TextView max_x_msb;
    private TextView max_x_lsb;
    private TextView max_y_msb;
    private TextView max_y_lsb;
    private TextView max_z_msb;
    private TextView max_z_lsb;
    private TextView min_x_msb;
    private TextView min_x_lsb;
    private TextView min_y_msb;
    private TextView min_y_lsb;
    private TextView min_z_msb;
    private TextView min_z_lsb;

    /*磁力偏移*/
    private TextView m_off_x_msb;
    private TextView m_off_x_lsb;
    private TextView m_off_y_msb;
    private TextView m_off_y_lsb;
    private TextView m_off_z_msb;
    private TextView m_off_z_lsb;

    /*磁力阙值*/
    private TextView m_ths_cfg;
    private TextView m_ths_src;
    private TextView m_ths_x_msb;
    private TextView m_ths_x_lsb;
    private TextView m_ths_y_msb;
    private TextView m_ths_y_lsb;
    private TextView m_ths_z_msb;
    private TextView m_ths_z_lsb;
    private TextView m_ths_count;

    /*磁力矢量*/
    private TextView m_vecm_cfg;
    private TextView m_vecm_ths_msb;
    private TextView m_vecm_ths_lsb;
    private TextView m_vecm_cnt;
    private TextView m_vecm_initx_msb;
    private TextView m_vecm_initx_lsb;
    private TextView m_vecm_inity_msb;
    private TextView m_vecm_inity_lsb;
    private TextView m_vecm_initz_msb;
    private TextView m_vecm_initz_lsb;
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FXOManager.RECEIVE_DATA:
                    updatePl();
                    updateA_ffmt();
                    updateA_vecm();
                    updateTransient();
                    updatePulse();
                    updateMagnetometer();
                    updateM_off();
                    updateM_ths();
                    updateM_vecm();
                    Log.i(TAG,"------------------------------");
                    Log.i(TAG,"M_CTRL_REG1 : " + manager.data.get(RegClass.M_CTRL_REG1));
                    Log.i(TAG,"M_CTRL_REG2 : " + manager.data.get(RegClass.M_CTRL_REG2));
                    Log.i(TAG,"XYZ_DATA_CFG : " + manager.data.get(RegClass.XYZ_DATA_CFG));
                    Log.i(TAG,"CTRL_REG1 : " + manager.data.get(RegClass.CTRL_REG1));
                    break;
            }
        }
    };

    public InfoActivity() {
        manager = FXOManager.getInstance(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setUiHandler(uiHandler);
    }

    private void init() {
        initPL();
        initA_ffmt();
        initA_vecm();
        initTransient();
        initPulse();
        initMagnetometer();
        initM_off();
        initM_ths();
        initM_vecm();
    }

    private void initPL() {
        pl_status = (TextView) findViewById(R.id.pl_status);
        pl_count = (TextView) findViewById(R.id.pl_count);
        pl_bf_zcomp = (TextView) findViewById(R.id.pl_bf_zcomp);
        pl_ths_reg = (TextView) findViewById(R.id.pl_ths_reg);
    }

    private void initA_ffmt() {
        a_ffmt_cfg = (TextView) findViewById(R.id.a_ffmt_cfg);
        a_ffmt_src = (TextView) findViewById(R.id.a_ffmt_src);
        a_ffmt_ths = (TextView) findViewById(R.id.a_ffmt_ths);
        a_ffmt_count = (TextView) findViewById(R.id.a_ffmt_count);
    }

    private void initA_vecm() {
        a_vecm_cfg = (TextView) findViewById(R.id.a_vecm_cfg);
        a_vecm_cnt = (TextView) findViewById(R.id.a_vecm_cnt);
        a_vecm_ths_msb = (TextView) findViewById(R.id.a_vecm_ths_msb);
        a_vecm_ths_lsb = (TextView) findViewById(R.id.a_vecm_ths_lsb);
        a_vecm_initx_msb = (TextView) findViewById(R.id.a_vecm_initx_msb);
        a_vecm_initx_lsb = (TextView) findViewById(R.id.a_vecm_initx_lsb);
        a_vecm_inity_msb = (TextView) findViewById(R.id.a_vecm_inity_msb);
        a_vecm_inity_lsb = (TextView) findViewById(R.id.a_vecm_inity_lsb);
        a_vecm_initz_msb = (TextView) findViewById(R.id.a_vecm_initz_msb);
        a_vecm_initz_lsb = (TextView) findViewById(R.id.a_vecm_initz_lsb);
    }

    private void initTransient() {
        transient_cfg = (TextView) findViewById(R.id.transient_cfg);
        transient_count = (TextView) findViewById(R.id.transient_count);
        transient_src = (TextView) findViewById(R.id.transient_src);
        transient_ths = (TextView) findViewById(R.id.transient_ths);
    }

    private void initPulse() {
        pulse_cfg = (TextView) findViewById(R.id.pulse_cfg);
        pulse_src = (TextView) findViewById(R.id.pulse_src);
        pulse_thsx = (TextView) findViewById(R.id.pulse_thsx);
        pulse_thsy = (TextView) findViewById(R.id.pulse_thsy);
        pulse_thsz = (TextView) findViewById(R.id.pulse_thsz);
        pulse_tmlt = (TextView) findViewById(R.id.pulse_tmlt);
        pulse_ltcy = (TextView) findViewById(R.id.pulse_ltcy);
        pulse_wind = (TextView) findViewById(R.id.pulse_wind);
    }

    private void initMagnetometer() {
        m_dr_status = (TextView) findViewById(R.id.m_dr_status);

        m_out_x_lsb = (TextView) findViewById(R.id.m_out_x_lsb);
        m_out_x_msb = (TextView) findViewById(R.id.m_out_x_msb);
        m_out_y_lsb = (TextView) findViewById(R.id.m_out_y_lsb);
        m_out_y_msb = (TextView) findViewById(R.id.m_out_y_msb);
        m_out_z_lsb = (TextView) findViewById(R.id.m_out_z_lsb);
        m_out_z_msb = (TextView) findViewById(R.id.m_out_z_msb);

        cmp_x_lsb = (TextView) findViewById(R.id.cmp_x_lsb);
        cmp_x_msb = (TextView) findViewById(R.id.cmp_x_msb);
        cmp_y_lsb = (TextView) findViewById(R.id.cmp_y_lsb);
        cmp_y_msb = (TextView) findViewById(R.id.cmp_y_msb);
        cmp_z_lsb = (TextView) findViewById(R.id.cmp_z_lsb);
        cmp_z_msb = (TextView) findViewById(R.id.cmp_z_msb);

        max_x_lsb = (TextView) findViewById(R.id.max_x_lsb);
        max_x_msb = (TextView) findViewById(R.id.max_x_msb);
        max_y_lsb = (TextView) findViewById(R.id.max_y_lsb);
        max_y_msb = (TextView) findViewById(R.id.max_y_msb);
        max_z_lsb = (TextView) findViewById(R.id.max_z_lsb);
        max_z_msb = (TextView) findViewById(R.id.max_z_msb);

        min_x_lsb = (TextView) findViewById(R.id.min_x_lsb);
        min_x_msb = (TextView) findViewById(R.id.min_x_msb);
        min_y_lsb = (TextView) findViewById(R.id.min_y_lsb);
        min_y_msb = (TextView) findViewById(R.id.min_y_msb);
        min_z_lsb = (TextView) findViewById(R.id.min_z_lsb);
        min_z_msb = (TextView) findViewById(R.id.min_z_msb);
    }

    private void initM_off() {
        m_off_x_lsb = (TextView) findViewById(R.id.m_off_x_lsb);
        m_off_x_msb = (TextView) findViewById(R.id.m_off_x_msb);
        m_off_y_lsb = (TextView) findViewById(R.id.m_off_y_lsb);
        m_off_y_msb = (TextView) findViewById(R.id.m_off_y_msb);
        m_off_z_lsb = (TextView) findViewById(R.id.m_off_z_lsb);
        m_off_z_msb = (TextView) findViewById(R.id.m_off_z_msb);
    }

    private void initM_ths() {
        m_ths_cfg = (TextView) findViewById(R.id.m_ths_cfg);
        m_ths_src = (TextView) findViewById(R.id.m_ths_src);

        m_ths_x_lsb = (TextView) findViewById(R.id.m_ths_x_lsb);
        m_ths_x_msb = (TextView) findViewById(R.id.m_ths_x_msb);
        m_ths_y_lsb = (TextView) findViewById(R.id.m_ths_y_lsb);
        m_ths_y_msb = (TextView) findViewById(R.id.m_ths_y_msb);
        m_ths_z_lsb = (TextView) findViewById(R.id.m_ths_z_lsb);
        m_ths_z_msb = (TextView) findViewById(R.id.m_ths_z_msb);

        m_ths_count = (TextView) findViewById(R.id.m_ths_count);
    }

    private void initM_vecm() {
        m_vecm_cfg = (TextView) findViewById(R.id.m_vecm_cfg);
        m_vecm_cnt = (TextView) findViewById(R.id.m_vecm_cnt);
        m_vecm_ths_msb = (TextView) findViewById(R.id.m_vecm_ths_msb);
        m_vecm_ths_lsb = (TextView) findViewById(R.id.m_vecm_ths_lsb);
        m_vecm_initx_msb = (TextView) findViewById(R.id.m_vecm_initx_msb);
        m_vecm_initx_lsb = (TextView) findViewById(R.id.m_vecm_initx_lsb);
        m_vecm_inity_msb = (TextView) findViewById(R.id.m_vecm_inity_msb);
        m_vecm_inity_lsb = (TextView) findViewById(R.id.m_vecm_inity_lsb);
        m_vecm_initz_msb = (TextView) findViewById(R.id.m_vecm_initz_msb);
        m_vecm_initz_lsb = (TextView) findViewById(R.id.m_vecm_initz_lsb);
    }

    private void updatePl() {
        pl_status.setText(manager.data.get(RegClass.PL_STATUS));
        pl_count.setText(manager.data.get(RegClass.PL_COUNT));
        pl_bf_zcomp.setText(manager.data.get(RegClass.PL_BF_ZCOMP));
        pl_ths_reg.setText(manager.data.get(RegClass.PL_THS_REG));
    }

    private void updateA_ffmt() {
        a_ffmt_cfg.setText(manager.data.get(RegClass.A_FFMT_CFG));
        a_ffmt_src.setText(manager.data.get(RegClass.A_FFMT_SRC));
        a_ffmt_ths.setText(manager.data.get(RegClass.A_FFMT_THS));
        a_ffmt_count.setText(manager.data.get(RegClass.A_FFMT_COUNT));
    }

    private void updateA_vecm() {
        a_vecm_cfg.setText(manager.data.get(RegClass.A_VECM_CFG));
        a_vecm_ths_msb.setText(manager.data.get(RegClass.A_VECM_THS_MSB));
        a_vecm_ths_lsb.setText(manager.data.get(RegClass.A_VECM_THS_LSB));
        a_vecm_cnt.setText(manager.data.get(RegClass.A_VECM_CNT));
        a_vecm_initx_msb.setText(manager.data.get(RegClass.A_VECM_INITX_MSB));
        a_vecm_initx_lsb.setText(manager.data.get(RegClass.A_VECM_INITX_LSB));
        a_vecm_inity_msb.setText(manager.data.get(RegClass.A_VECM_INITY_MSB));
        a_vecm_inity_lsb.setText(manager.data.get(RegClass.A_VECM_INITY_LSB));
        a_vecm_initz_msb.setText(manager.data.get(RegClass.A_VECM_INITZ_MSB));
        a_vecm_initz_lsb.setText(manager.data.get(RegClass.A_VECM_INITZ_LSB));

    }

    private void updateTransient() {
        transient_cfg.setText(manager.data.get(RegClass.TRANSIENT_CFG));
        transient_src.setText(manager.data.get(RegClass.TRANSIENT_SRC));
        transient_ths.setText(manager.data.get(RegClass.TRANSIENT_THS));
        transient_count.setText(manager.data.get(RegClass.TRANSIENT_COUNT));
    }

    private void updateMagnetometer() {
        m_dr_status.setText(manager.data.get(RegClass.M_DR_STATUS));

        m_out_x_msb.setText(manager.data.get(RegClass.M_OUT_X_MSB));
        m_out_x_lsb.setText(manager.data.get(RegClass.M_OUT_X_LSB));
        m_out_y_msb.setText(manager.data.get(RegClass.M_OUT_Y_MSB));
        m_out_y_lsb.setText(manager.data.get(RegClass.M_OUT_Y_LSB));
        m_out_z_msb.setText(manager.data.get(RegClass.M_OUT_Z_MSB));
        m_out_z_lsb.setText(manager.data.get(RegClass.M_OUT_Z_LSB));

        cmp_x_msb.setText(manager.data.get(RegClass.CMP_X_MSB));
        cmp_x_lsb.setText(manager.data.get(RegClass.CMP_X_LSB));
        cmp_y_msb.setText(manager.data.get(RegClass.CMP_Y_MSB));
        cmp_y_lsb.setText(manager.data.get(RegClass.CMP_Y_LSB));
        cmp_z_msb.setText(manager.data.get(RegClass.CMP_Z_MSB));
        cmp_z_lsb.setText(manager.data.get(RegClass.CMP_Z_LSB));

        max_x_msb.setText(manager.data.get(RegClass.MAX_X_MSB));
        max_x_lsb.setText(manager.data.get(RegClass.MAX_X_LSB));
        max_y_msb.setText(manager.data.get(RegClass.MAX_Y_MSB));
        max_y_lsb.setText(manager.data.get(RegClass.MAX_Y_LSB));
        max_z_msb.setText(manager.data.get(RegClass.MAX_Z_MSB));
        max_z_lsb.setText(manager.data.get(RegClass.MAX_Z_LSB));

        min_x_msb.setText(manager.data.get(RegClass.MIN_X_MSB));
        min_x_lsb.setText(manager.data.get(RegClass.MIN_X_LSB));
        min_y_msb.setText(manager.data.get(RegClass.MIN_Y_MSB));
        min_y_lsb.setText(manager.data.get(RegClass.MIN_Y_LSB));
        min_z_msb.setText(manager.data.get(RegClass.MIN_Z_MSB));
        min_z_lsb.setText(manager.data.get(RegClass.MIN_Z_LSB));

    }

    private void updatePulse() {

        pulse_cfg.setText(manager.data.get(RegClass.PULSE_CFG));
        pulse_src.setText(manager.data.get(RegClass.PULSE_SRC));
        pulse_wind.setText(manager.data.get(RegClass.PULSE_WIND));
        pulse_tmlt.setText(manager.data.get(RegClass.PULSE_TMLT));
        pulse_ltcy.setText(manager.data.get(RegClass.PULSE_LTCY));
        pulse_thsx.setText(manager.data.get(RegClass.PULSE_THSX));
        pulse_thsy.setText(manager.data.get(RegClass.PULSE_THSY));
        pulse_thsz.setText(manager.data.get(RegClass.PULSE_THSZ));
    }

    private void updateM_off() {
        m_off_x_msb.setText(manager.data.get(RegClass.M_OFF_X_MSB));
        m_off_x_lsb.setText(manager.data.get(RegClass.M_OFF_X_LSB));
        m_off_y_msb.setText(manager.data.get(RegClass.M_OFF_Y_MSB));
        m_off_y_lsb.setText(manager.data.get(RegClass.M_OFF_Y_LSB));
        m_off_z_msb.setText(manager.data.get(RegClass.M_OFF_Z_MSB));
        m_off_z_lsb.setText(manager.data.get(RegClass.M_OFF_Z_LSB));
    }

    private void updateM_ths() {
        m_ths_cfg.setText(manager.data.get(RegClass.M_THS_CFG));
        m_ths_src.setText(manager.data.get(RegClass.M_THS_SRC));
        m_ths_count.setText(manager.data.get(RegClass.M_THS_COUNT));

        m_ths_x_msb.setText(manager.data.get(RegClass.M_THS_X_MSB));
        m_ths_x_lsb.setText(manager.data.get(RegClass.M_THS_X_LSB));
        m_ths_y_msb.setText(manager.data.get(RegClass.M_THS_Y_MSB));
        m_ths_y_lsb.setText(manager.data.get(RegClass.M_THS_Y_LSB));
        m_ths_z_msb.setText(manager.data.get(RegClass.M_THS_Z_MSB));
        m_ths_z_lsb.setText(manager.data.get(RegClass.M_THS_Z_LSB));
    }

    private void updateM_vecm() {
        m_vecm_cfg.setText(manager.data.get(RegClass.M_VECM_CFG));
        m_vecm_ths_msb.setText(manager.data.get(RegClass.M_VECM_THS_MSB));
        m_vecm_ths_lsb.setText(manager.data.get(RegClass.M_VECM_THS_LSB));
        m_vecm_cnt.setText(manager.data.get(RegClass.M_VECM_CNT));
        m_vecm_initx_msb.setText(manager.data.get(RegClass.M_VECM_INITX_MSB));
        m_vecm_initx_lsb.setText(manager.data.get(RegClass.M_VECM_INITX_LSB));
        m_vecm_inity_msb.setText(manager.data.get(RegClass.M_VECM_INITY_MSB));
        m_vecm_inity_lsb.setText(manager.data.get(RegClass.M_VECM_INITY_LSB));
        m_vecm_initz_msb.setText(manager.data.get(RegClass.M_VECM_INITZ_MSB));
        m_vecm_initz_lsb.setText(manager.data.get(RegClass.M_VECM_INITZ_LSB));
    }
}
