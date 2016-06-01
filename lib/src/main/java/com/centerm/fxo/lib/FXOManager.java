package com.centerm.fxo.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Map;

/**
 * Created by administrator on 14-4-18.
 */
public class FXOManager implements OnReceiveDataListener, FXOService.OnConnectResultListener {
    private static final String TAG = "Centerm:FXOManager";

    public static final int RECEIVE_DATA = 0;
    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNECT_FAIL = 2;

    public Map<String, String> data;

    private static FXOManager instance;

    private FXOService fxoService;

    private Handler uiHandler;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fxoService = ((FXOService.FXOServiceBinder) service).getService();
            fxoService.setOnReceiveDataListener(FXOManager.this);
            fxoService.setOnConnectResultListener(FXOManager.this);
            fxoService.startConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "FXOService Stopped!");
        }
    };

    private FXOManager(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public static FXOManager getInstance(Handler uiHandler) {
        if (instance == null) {
            instance = new FXOManager(uiHandler);
        }
        return instance;
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
    //连接芯片
    public void connectFXO(Context context) {
        Log.i(TAG, "Connect to FXO");
        if (fxoService != null) {
            fxoService.startConnect();
        } else {
            startFXOService(context);
        }
    }
    //断开连接
    public void disconnectFXO(Context context) {
        Log.i(TAG, "Disonnect to FXO");
        if (fxoService != null) {
            fxoService.disconnect();
        }
    }
    //通知芯片开始发送寄存器数据上来
    public void startReceiverRegisterValues() {
        command("A");
    }
    public void read_5C(){
        fxoService.read();
    }
    public void showLog(){
        fxoService.showLog();
    }

    // -----------------------------------------------------


    private void startFXOService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FXOService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void command(String command) {
        fxoService.sendCommand(command);
    }
    // 向寄存器中写入数据
    public void write(String register, String value) {
        fxoService.write(register, value);
    }

    public void read (){
        fxoService.read();
    }
    @Override
    public void onReceiveFXOData(Map<String, String> data) {
        this.data = data;
        if (uiHandler != null) {
            uiHandler.obtainMessage(RECEIVE_DATA).sendToTarget();
            //Log.v(TAG, "sendToTarget");
        }
    }

    @Override
    public void onConnectSuccess() {
        /*作用：开启磁力计、加速度计混合开启使用模式；过采样率为标准采样率4倍；*/
       write(RegClass.M_CTRL_REG1, "1F");
        /*作用：设置磁力计数据每512个ODR后复位校准1次；*/
        write(RegClass.M_CTRL_REG2, "20");
        /*作用：加速计检测范围+/-8g；*/
        write(RegClass.XYZ_DATA_CFG, "02");
        /*作用：ODR（加速度计更新数据速率）输出速率=400HZ，并使能芯片工作；*/
        write(RegClass.CTRL_REG1, "09");

        if (uiHandler != null) {
            uiHandler.obtainMessage(CONNECT_SUCCESS).sendToTarget();
        }
    }

    public void ffm(){
        write(RegClass.A_FFMT_CFG, "B8");
        /*作用：设置磁力计数据每512个ODR后复位校准1次；*/
        write(RegClass.A_FFMT_THS, "03");
        /*作用：加速计检测范围+/-8g；*/
        write(RegClass.A_FFMT_COUNT, "06");
        /*作用：ODR（加速度计更新数据速率）输出速率=400HZ，并使能芯片工作；*/
        write(RegClass.CTRL_REG4, "04");
        write(RegClass.CTRL_REG5, "00");
    }
    @Override
    public void onConnectFail() {
        if (uiHandler != null) {
            uiHandler.obtainMessage(CONNECT_FAIL).sendToTarget();
        }
    }
}

