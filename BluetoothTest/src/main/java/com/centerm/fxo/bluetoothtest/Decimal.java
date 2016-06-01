package com.centerm.fxo.bluetoothtest;

/**
 * Created by Chen on 16/5/20.
 */
public class Decimal {
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
