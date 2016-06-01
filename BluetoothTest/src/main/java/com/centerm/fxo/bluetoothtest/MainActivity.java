package com.centerm.fxo.bluetoothtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.fxo.lib.FXOManager;
import com.centerm.fxo.lib.RegClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "Centerm:MainActivity";

    private FXOManager manager;

    AlertDialog progressDialog;
    MyDatabaseHelper dbHelper;
    DatabaseContext databaseContext = new DatabaseContext(this);
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FXOManager.RECEIVE_DATA:
                    updateOutRegs(manager.data);
                    updateAcceleration(manager.data);
                    //ffmdetection(manager.data);
                    //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    System.out.println(manager.data.get(RegClass.OUT_X_LSB) +
                            manager.data.get(RegClass.OUT_Y_LSB) + manager.data.get(RegClass.OUT_Z_LSB));
                    //insertData(dbHelper.getReadableDatabase(), manager.data.get(RegClass.OUT_X_LSB),
                    //        manager.data.get(RegClass.OUT_Y_LSB), manager.data.get(RegClass.OUT_Z_LSB), df.format(new Date()));
                    //Log.v("aaaaaa", "-------------------");
                    break;
                case FXOManager.CONNECT_SUCCESS:
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case FXOManager.CONNECT_FAIL:
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    // OUT_*_***nihou
    private TextView oxl;
    private TextView oxm;
    private TextView oyl;
    private TextView oym;
    private TextView ozl;
    private TextView ozm;
    // OFF_*
    private TextView offx;
    private TextView offy;
    private TextView offz;
    // ACCELERATION*
    String  x;
    String  y;
    String  z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(databaseContext, "myDict3.db3",1);
        Log.v(TAG,"------------------------create");
        manager = FXOManager.getInstance(uiHandler);

        initOutRegs();
        initOffRegs();


    }
    private void insertData(SQLiteDatabase db, String x
            , String y, String z, String timeStamp)
    {
        // 执行插入语句
        db.execSQL("insert into dict values(null , ? , ?, ?, ?)"
                , new String[] {x, y, z, timeStamp});
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        manager.setUiHandler(uiHandler);
    }

    private void initOutRegs() {
        oxl = (TextView) findViewById(R.id.oxl);
        oxm = (TextView) findViewById(R.id.oxm);
        oyl = (TextView) findViewById(R.id.oyl);
        oym = (TextView) findViewById(R.id.oym);
        ozl = (TextView) findViewById(R.id.ozl);
        ozm = (TextView) findViewById(R.id.ozm);
    }

    private void initOffRegs() {
        offx = (TextView) findViewById(R.id.off_x);
        offy = (TextView) findViewById(R.id.off_y);
        offz = (TextView) findViewById(R.id.off_z);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_info) {
            Intent intent = new Intent();
            intent.setClass(this, InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_connect) {
            manager.connectFXO(this);
            ProgressDialog.Builder dialog = new ProgressDialog.Builder(this);
            dialog.setTitle("Connecting....");
            progressDialog = dialog.create();
            progressDialog.show();
        } else if (id == R.id.action_disconnect) {
            manager.disconnectFXO(this);
        } else if (id == R.id.action_command_a) {
            manager.startReceiverRegisterValues();

        } else if (id == R.id.action_command_w) {
            manager.read();
            // manager.write(RegClass.OFF_X, "FF");
        }else if (id== R.id.show_stringbuffer){
            Runnable threadJob = new Upload();
            Thread myThread = new Thread(threadJob);
            myThread.start();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateOutRegs(Map<String, String> data) {
        oxl.setText(data.get(RegClass.OUT_X_LSB));
        oxm.setText(data.get(RegClass.OUT_X_MSB));
        oyl.setText(data.get(RegClass.OUT_Y_LSB));
        oym.setText(data.get(RegClass.OUT_Y_MSB));
        ozl.setText(data.get(RegClass.OUT_Z_LSB));
        ozm.setText(data.get(RegClass.OUT_Z_MSB));
    }
    private void updateAcceleration(Map<String, String> data) {
        x = c(combineValue(data.get(RegClass.OUT_X_MSB),data.get(RegClass.OUT_X_LSB)));
        y = c(combineValue(data.get(RegClass.OUT_Y_MSB),data.get(RegClass.OUT_Y_LSB)));
        z = c(combineValue(data.get(RegClass.OUT_Z_MSB),data.get(RegClass.OUT_Z_LSB)));
        offx.setText(c(combineValue(data.get(RegClass.OUT_X_MSB),data.get(RegClass.OUT_X_LSB))));
        offy.setText(c(combineValue(data.get(RegClass.OUT_Y_MSB),data.get(RegClass.OUT_Y_LSB))));
        offz.setText(c(combineValue(data.get(RegClass.OUT_Z_MSB),data.get(RegClass.OUT_Z_LSB))));

    }
    private void ffmdetection(Map<String, String> data) {
        if (Integer.parseInt(data.get(RegClass.A_FFMT_SRC),16) >= 128) {
            offx.setText("yes");
            Runnable threadJob = new UploadString(toJSON(c(combineValue(manager.data.get(RegClass.OUT_X_MSB),manager.data.get(RegClass.OUT_X_LSB))),
                    c(combineValue(manager.data.get(RegClass.OUT_Y_MSB),manager.data.get(RegClass.OUT_Y_LSB))),
                    c(combineValue(manager.data.get(RegClass.OUT_Z_MSB),manager.data.get(RegClass.OUT_Z_LSB)))));
            Thread myThread = new Thread(threadJob);
            myThread.start();

        }else
            offx.setText("no");
    }
    //合并加速度MSB和LSB
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
    //封装JSON
    public String toJSON(String x, String y, String z){
        String JSON= null;
        JSONObject obj = new JSONObject();
        try{
            obj.put("x", x);
            obj.put("y", y);
            obj.put("z", z);
        }catch (JSONException ex){

        }
        JSON = obj.toString();
        return JSON;
    }
}