package com.centerm.fxo.lib;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FXOService extends Service {
    private static final String TAG = "Centerm:FXOService";

    private OnConnectResultListener onConnectResultListener;

    private OnReceiveDataListener onReceiveDataListener;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bondedDevice;

    private ConnectThread connectThread;

    private ConnectedThread connectedThread;

    private int regIndex = 0;

    public MyDatabaseHelper dbHelper;

    public DatabaseContext databaseContext = new DatabaseContext(this);

    private Map<String, String> regs = new HashMap<String, String>();

    private String flag = "yes";

    private int Reg_num = 6;

    private String msg2 = "";

    private String[] Reg_address = new String[Reg_num];{
        Reg_address[0] = "01";
        Reg_address[1] = "02";
        Reg_address[2] = "03";
        Reg_address[3] = "04";
        Reg_address[4] = "05";
        Reg_address[5] = "06";
    }

    private String[] Reg_address2 = new String[Reg_num];{
        Reg_address2[0] = "01";
        Reg_address2[1] = "02";
        Reg_address2[2] = "03";
        Reg_address2[3] = "04";
        Reg_address2[4] = "05";
        Reg_address2[5] = "06";
    }

    private BroadcastReceiver fondReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG,"name : " + bluetoothDevice.getName());
                if ("CENTERM".equals(bluetoothDevice.getName())){
                    Log.i(TAG, "Found FXO Device....");
                    bluetoothAdapter.cancelDiscovery();
                    bondedDevice = bluetoothDevice;
                    connect();
                }
            }
        }
    };

    public FXOService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new FXOServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "FXOService is start..!");
        regReceiver();
        dbHelper = new MyDatabaseHelper(databaseContext, "myDict3.db3",1);
    }

    public void setOnReceiveDataListener(OnReceiveDataListener listener) {
        onReceiveDataListener = listener;
    }

    public void setOnConnectResultListener(OnConnectResultListener listener) {
        onConnectResultListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void disconnect(){
        unRegReceiver();
        Log.i(TAG, ">>>>>>)*)(*()  STOP SERVICE!");
        if (connectThread != null && connectThread.isAlive()) {
            connectThread.interrupt();
        }
        if (connectedThread != null && connectedThread.isAlive()) {
            connectedThread.cancel();
        }
    }

    public void read() {
        if (connectedThread != null) {
            byte[] bytes = new byte[8];
            bytes[0] = 'R';
            bytes[1] = '6';
            bytes[2] =Integer.valueOf("01", 16).byteValue();
            bytes[3] =Integer.valueOf("02", 16).byteValue();
            bytes[4] =Integer.valueOf("03", 16).byteValue();
            bytes[5] =Integer.valueOf("04", 16).byteValue();
            bytes[6] =Integer.valueOf("05", 16).byteValue();
            bytes[7] =Integer.valueOf("06", 16).byteValue();


            //bytes[1] = Integer.valueOf(register, 16).byteValue();
            //Log.v(TAG,"bytes:" + bytes[0] + bytes[1]);
            connectedThread.write(bytes);
        }
    }

    public void write(String register, String value) {
        if (connectedThread != null) {
            byte[] bytes = new byte[3];
            bytes[0] = 'W';
            bytes[1] = Integer.valueOf(register, 16).byteValue();
            bytes[2] = Integer.valueOf(value, 16).byteValue();

            Log.i(TAG, "写入：" + bytes);

            connectedThread.write(bytes);
        }
    }

    public void showLog(){
        Log.v(TAG, connectedThread.stringBuffer.toString());
    }

    private void insertData(SQLiteDatabase db, String x
            , String y, String z, String timeStamp)
    {
        // 执行插入语句
        db.execSQL("insert into dict values(null , ? , ?, ?, ?)"
                , new String[] {x, y, z, timeStamp});
    }

    public void sendCommand(String command) {
        if (connectedThread != null) {
            connectedThread.write(command.getBytes());
        }
    }

    private void unRegReceiver() {
        unregisterReceiver(fondReceiver);
    }

    private void regReceiver() {
        IntentFilter fondActionFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(fondReceiver, fondActionFilter);
    }

    public void startConnect() {
        if (isBluetooth() && enableBluetooth()) {
            Log.i(TAG, "Bluetooth is oK!");
            if (isBondedHasFXODevice()) {
                Log.i(TAG, "there has a bonded FXO device.");
                connect();
            } else {
                Log.i(TAG, "start discovery FXO device");
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    private boolean isBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    private boolean enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            return bluetoothAdapter.enable();
        }
        return true;
    }

    private boolean isBondedHasFXODevice() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.i("Centerm", "Bonded device : " + device.getName());
            // TODO 如何判断该设备是FXO传感器？？
            if ("CENTERM".equals(device.getName())) { // 设备是否是FXO传感器
                bondedDevice = device;
                return true;
            }
        }
        return false;
    }

    private void connect() {
        connectThread = new ConnectThread(bondedDevice);
        connectThread.start();
        Log.v(TAG, "connected");
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.v(TAG,"mmSocket.connect()");
                connectedThread = new ConnectedThread(mmSocket);
                connectedThread.start();
                onConnectResultListener.onConnectSuccess();
            } catch (IOException connectException) {
                onConnectResultListener.onConnectFail();
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

        }
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket socket;
        private InputStream in;
        private OutputStream out;
        private StringBuffer stringBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            in = null;
            out = null;
            stringBuffer = new StringBuffer("");

            try {
                in = this.socket.getInputStream();
                out = this.socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (isInterrupted()) {
                        return;
                    }

                    String temp = readStr();
                    stringBuffer.append(temp);
                    //Log.v(TAG,stringBuffer.toString() );
                    if (isResultMsg()) {
                        if (stringBuffer.toString().contains("=0")) { // Fail
                            Log.i(TAG, "Fail");
                        } else if (stringBuffer.toString().contains("=1")) { // Success
                            Log.i(TAG, "Success");
                        } else {
                            continue;
                        }
                        Log.i(TAG, "Clear Buffer! And wake up out wait!");
                        stringBuffer.delete(0, stringBuffer.length());
                        synchronized (out) {
                            out.notifyAll();
                        }
                    } else {
                         //Log.v(TAG,"length" + String.valueOf(stringBuffer.length()) );
                        //Log.v(TAG, stringBuffer.toString());
                        //readAccRegMsg();
                        receiveRegMsg3(Reg_address);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {

            //Log.v(TAG,"bytes:" + bytes[0]);
            synchronized (out) {
                try {
                    if (stringBuffer.length() > 0) {
                        stringBuffer.delete(0, stringBuffer.length() - 1);
                    }
                    out.write(bytes);
                    out.wait();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            interrupt();
            try {
                socket.close();
                Log.i(TAG, "断开蓝牙了！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void receiveRegMsg() {
            check(stringBuffer);
            String msg = getAndDelCompleteMsgFrom(stringBuffer);
            //Log.v(TAG,"msg:"+ msg);
            if (msg != null) {
                synchronized (out){
                    out.notifyAll();
                }
                Register reg = new Register(msg);
                regs.put(reg.address, reg.value);
                //Log.i(TAG, "Complete msg : " + msg + " > " + regIndex);
                regIndex = (++regIndex) % 121;

                if (regIndex == 0) {
                    onReceiveDataListener.onReceiveFXOData(regs);
                    //Log.v(TAG, msg + "sendToTarget2");
                }
            }
        }

        private void receiveRegMsg2(String[] Reg_address){
            check(stringBuffer);
            String msg = getAndDelCompleteMsgFrom2(stringBuffer);
            if(msg != null) {
                synchronized (out){
                    out.notifyAll();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                msg = msg + df.format(new Date());
                //if(!msg.equals(msg2)) {e
                  //  msg2 = msg;
                    System.out.println(msg);
                    for (int i = 0, j = 0; i <= Reg_num * 2 - 2; i = i + 2) {
                        String val = msg.substring(i, i + 2);
                        //System.out.println(val);
                        regs.put(Reg_address[j], val);
                        j++;
                    }
                    regs.put("time", msg.substring(12, 40) + msg);
                    Runnable threadJob = new insert(regs);
                    Thread myThread = new Thread(threadJob);
                    myThread.start();
                    regIndex = (++regIndex) % 3;
                    //regIndex = 0;
                    if (regIndex == 0) {
                        //System.out.println("send");
                        onReceiveDataListener.onReceiveFXOData(regs);
                        //Log.v(TAG, msg + "sendToTarget2");
                    }
                //}
            }
        }

        private void receiveRegMsg3(String[] Reg_address) {
            check(stringBuffer);
            String msg = getAndDelCompleteMsgFrom2(stringBuffer);
            if (msg != null) {
                synchronized (out) {
                    out.notifyAll();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                msg = msg + df.format(new Date());
                //if(!msg.equals(msg2)) {e
                //  msg2 = msg;
                System.out.println(msg);
                Runnable threadJob = new insert2(msg);
                Thread myThread = new Thread(threadJob);
                myThread.start();
                regIndex = (++regIndex) % 3;
                //regIndex = 0;
                if (regIndex == 0) {
                    //System.out.println("send");
                    onReceiveDataListener.onReceiveFXOData(regs);
                    //Log.v(TAG, msg + "sendToTarget2");
                }
                //}
            }
        }
        private boolean isResultMsg() {
            return stringBuffer.toString().contains("=");
        }

        // 读取知道寄存器数值
        private void readAccRegMsg() {
            if(stringBuffer.toString().contains("01")) {
                Log.v(TAG, String.valueOf(stringBuffer.length()));
                int index = stringBuffer.indexOf("01");
                Log.v(TAG,"index:" + index);
                String msg;
                msg = stringBuffer.substring(index, (index + 5));
                //Log.v(TAG, msg);
                stringBuffer.delete(0, stringBuffer.length());

                if (msg != null) {
                    synchronized (out) {
                        out.notifyAll();
                    }
                    Register reg = new Register(msg);
                    regs.put(reg.address, reg.value);
                    //Log.i(TAG, "Complete msg : " + msg + " > " + regIndex);
                    regIndex = (++regIndex) % 121;

                    if (regIndex == 0) {
                        onReceiveDataListener.onReceiveFXOData(regs);
                        //Log.v(TAG, msg + "sendToTarget2");
                    }
                }
            }
        }


        private void check(StringBuffer stringBuffer) {
            //System.out.println("check");
            //Log.v(TAG, String.valueOf(stringBuffer.length()));
            while (stringBuffer.length() > 0 && stringBuffer.charAt(0) != '\n') {
                stringBuffer.deleteCharAt(0);
            }
        }

        private String getAndDelCompleteMsgFrom(StringBuffer stringBuffer) {
            String msg = null;
            //String msg2 = null;

            if (stringBuffer.length() >= 6) {
                msg = stringBuffer.substring(1, 6);
                stringBuffer.delete(0, 6);
                //msg2 = stringBuffer.substring(0,stringBuffer.length());
                // Log.v(TAG,msg2);

            }
            return msg;
        }

        private String getAndDelCompleteMsgFrom2(StringBuffer stringBuffer) {
            String msg = null;
            System.out.println("lean" + String.valueOf(stringBuffer.length()));
            //String msg2 = null;
            if (stringBuffer.length() >= 13) {
                msg = stringBuffer.substring(1, 13);
                stringBuffer.delete(0, 13);
                //msg2 = stringBuffer.substring(0,stringBuffer.length());
                // Log.v(TAG,msg2);
            }

                return msg;

        }

        private String readStr() throws IOException {
            String receive;
            byte[] buffer = new byte[1024];
            int bytes = in.read(buffer);
            receive = new String(buffer, 0, bytes);
            //Log.v(TAG, "receiver: "+ receive);
            return receive;
        }
    }

    public class insert implements Runnable{
        String x, y, z, time;
        String x_m, x_l, y_m, y_l, z_m, z_l;
        Map<String, String> data;
        public insert(Map<String, String > data) {
            time = data.get("time");
            x_m = data.get(RegClass.OUT_X_MSB);
            x_l = data.get(RegClass.OUT_X_LSB);
            y_m = data.get(RegClass.OUT_Y_MSB);
            y_l = data.get(RegClass.OUT_Y_LSB);
            z_m = data.get(RegClass.OUT_Z_MSB);
            z_l = data.get(RegClass.OUT_Z_LSB);
        }    @Override

        public void run() {
            /*System.out.println(regs.get(reg_add[0]) + regs.get(reg_add[1]) + regs.get(reg_add[2]) + regs.get(reg_add[3])
                 + regs.get(reg_add[4]) + regs.get(reg_add[5]));*/

            if(time != flag) {
                flag = time;
                x = c(combineValue(x_m, x_l));
                y = c(combineValue(y_m, y_l));
                z = c(combineValue(z_m, z_l));

            /*while (flag == "no"){
                try{
                    Thread.sleep(1);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }*/
                //flag = "no";
                insertData(dbHelper.getReadableDatabase(), x, y, z, time);
            }

            //flag = "yes";
        }
        private String combineValue(String MSB, String LSB){
            String Value = MSB + LSB;
            return Value;
        }
        //加速度进制转换
        public String c(String value){
            int input;
            input=Integer.parseInt(value,16);

            // Determine sign
            int num;
            String output;
            num=input;
            if (num<0x7FFF)
            {
                output="+";
            }
            else
            {
                output="-";
                num = ~num + 1;
                num &= 0xFFFC;
            }

            //Output integer
            int integer_=num>>12;
            output= output +String.valueOf(integer_)+".";

            //Output Decimal
            int decimal=num>>2;
            decimal &= 0x3FF;
            int[] m;
            m=new int[11]; //定义小数
            m[0]=0;
            m[1]=5000;
            m[2]=2500;
            m[3]=1250;
            m[4]=625;
            m[5]=313;
            m[6]=156;
            m[7]=78;
            m[8]=39;
            m[9]=20;
            m[10]=10;
            int result = 0;
            int bit = 512;// 10 0000 0000
            for(int i=1;i<11;i=i+1)
            {
                if((decimal & bit)>0)
                    result = result + m[i];
                bit = bit / 2;
            }
            //确保小数有四位
            if (result <100 )
                output = output + "00" + String.valueOf(result);
            else if (result < 1000)
                output = output + "0" + String.valueOf(result);
            else output = output + String.valueOf(result);
            return output;
        }


    }

    public class insert2 implements Runnable{
        String msg;
        String x, y, z, time;
        String x_m, x_l, y_m, y_l, z_m, z_l;
        public insert2(String msg) {
            this.msg = msg;
        }    @Override

        public void run() {
            /*System.out.println(regs.get(reg_add[0]) + regs.get(reg_add[1]) + regs.get(reg_add[2]) + regs.get(reg_add[3])
                 + regs.get(reg_add[4]) + regs.get(reg_add[5]));*/
            for (int i = 0, j = 0; i <= Reg_num * 2 - 2; i = i + 2) {
                String val = msg.substring(i, i + 2);
                //System.out.println(val);
                Reg_address2[j] = val;
                regs.put(Reg_address[j], val);
                j++;
            }
            regs.put("time", msg.substring(12, 40) + "  " + msg);

            time = msg.substring(12, 40) + "  " + msg;
            x_m = Reg_address2[0];
            x_l = Reg_address2[1];
            y_m = Reg_address2[2];
            y_l = Reg_address2[3];
            z_m = Reg_address2[4];
            z_l = Reg_address2[5];
            if(time != flag) {
                flag = time;
                x = c(combineValue(x_m, x_l));
                y = c(combineValue(y_m, y_l));
                z = c(combineValue(z_m, z_l));

            /*while (flag == "no"){
                try{
                    Thread.sleep(1);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }*/
                //flag = "no";
                insertData(dbHelper.getReadableDatabase(), x, y, z, time);
            }

            //flag = "yes";
        }
        private String combineValue(String MSB, String LSB){
            String Value = MSB + LSB;
            return Value;
        }
        //加速度进制转换
        public String c(String value){
            int input;
            input=Integer.parseInt(value,16);

            // Determine sign
            int num;
            String output;
            num=input;
            if (num<0x7FFF)
            {
                output="+";
            }
            else
            {
                output="-";
                num = ~num + 1;
                num &= 0xFFFC;
            }

            //Output integer
            int integer_=num>>12;
            output= output +String.valueOf(integer_)+".";

            //Output Decimal
            int decimal=num>>2;
            decimal &= 0x3FF;
            int[] m;
            m=new int[11]; //定义小数
            m[0]=0;
            m[1]=5000;
            m[2]=2500;
            m[3]=1250;
            m[4]=625;
            m[5]=313;
            m[6]=156;
            m[7]=78;
            m[8]=39;
            m[9]=20;
            m[10]=10;
            int result = 0;
            int bit = 512;// 10 0000 0000
            for(int i=1;i<11;i=i+1)
            {
                if((decimal & bit)>0)
                    result = result + m[i];
                bit = bit / 2;
            }
            //确保小数有四位
            if (result <100 )
                output = output + "00" + String.valueOf(result);
            else if (result < 1000)
                output = output + "0" + String.valueOf(result);
            else output = output + String.valueOf(result);
            return output;
        }


    }

    public class FXOServiceBinder extends Binder {
        public FXOService getService() {
            return FXOService.this;
        }
    }

    public interface OnConnectResultListener {
        public void onConnectSuccess();

        public void onConnectFail();
    }
}
