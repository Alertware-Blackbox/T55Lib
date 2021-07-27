package com.blackbox.utils;

/**
 * Created by krishnendu on 6/3/2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.blackbox.myt55library.R;


/**
 * Created by krishnendu on 5/17/2016.
 */
public class CoalGrid extends Activity {
    //Coal Grid Transformation
    double E0=3000000,N0=1000000;
    double g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,AA,AB,AC,AD,AE,AF,AG;
    double AH,AI,AJ,AK,AL,AM,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ,BA,BB,BC,BD,BE,BF,BG,BH;
    String latdeg,latmin,latsec,londeg,lonmin,lonsec;
    double op1,op2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        latdeg=getIntent().getExtras().getString("latdeg");
        latmin=getIntent().getExtras().getString("latmin");
        latsec=getIntent().getExtras().getString("latsec");
        londeg=getIntent().getExtras().getString("londeg");
        lonmin=getIntent().getExtras().getString("lonmin");
        lonsec=getIntent().getExtras().getString("lonsec");
        g=(Double.parseDouble(latdeg)*3600+ Double.parseDouble(latmin)*60+ Double.parseDouble(latsec))/206264.806246;
        h= Double.parseDouble(londeg)+ Double.parseDouble(lonmin)/60+ Double.parseDouble(lonsec)/3600;
        i=6377301.243;
        j=6356100.231;
        k=(Math.pow(i,2)- Math.pow(j,2))/ Math.pow(i,2);
        l= Math.sqrt(k);
        m=l/2;
        n=3000000;
        o=1000000;
        p=20421174.5092207;
        q=0.329759217152455;
        r=0.303754667109034;
        s=0.377283283970982;
        t=0.340368652311444;
        u=0.999394420825938;
        v=18253058.0129509;
        w=6379604.07984102;
        x=17;
        y=15;
        z=0;
        AA=21;
        AB=15;
        AC=0;
        AD=(x*3600+y*60+z)/206264.806246;
        AE=(AA*3600+AB*60+AC)/206264.806246;
        AF=79;
        AG=2.71828182845905;
        AH=(Math.sin(g))*l;
        AI= Math.log(Math.pow((1 - AH) / (1 + AH),m)*(Math.tan(((45*3600/206264.806246)+g/2))));
        AJ=(i/(Math.sqrt(1 - k * Math.pow(Math.sin(g), 2))));
        AK=(j*(1-k))/ Math.pow((1 - l * Math.pow(Math.sin(h), 2)), 1.5);
        AL=AJ*(Math.cos(g));
        AM=(i/ Math.sqrt(1 - k * Math.pow(Math.sin(AD), 2)))* Math.cos(AD);
        AN=(i/ Math.sqrt(1 - k * Math.pow(Math.sin(AE), 2)))* Math.cos(AE);
        AO= Math.log(AM)- Math.log(AN);
        AP= Math.log(Math.pow((1 - l * Math.sin(AD)) / (1 + l * Math.sin(AD)),m)* Math.tan(45*3600/206264.806246+AD/2));
        AQ= Math.log(Math.pow(((1 - l* Math.sin(AE))/(1+l* Math.sin(AE))),m)* Math.tan((45*3600/206264.806246+AE/2)));
        AR=(AO/(AQ-AP));
        AS=(-AR*AP);
        AT=(Math.pow(AG,AS))*AR;
        AU=(AM/AT);
        AV=(-AR*AQ);
        AW=(Math.pow(AG,AV))*AR;
        AX=AN/AW;
        AY=(AU+AX)/2;
        AZ= Math.asin(AR);
        BA= Math.log(Math.pow(((1 - l * Math.sin(AZ)) / (1 + l * Math.sin(AZ))), m) * Math.tan((45 * 3600 / 206264.806246 + AZ / 2)));
        BB=AY/(Math.pow(AG,(AR*BA)));
        BC=u*w*(1/ Math.tan(AZ));
        BD=(BB+BC)/2;
        BE=(AY/(Math.pow(AG,(AR*AI))));
        BF=AR*(h-AF);
        BG=(Math.sin(BF*3600/206264.806246))*BE;
        BH=(BD-(BE* Math.cos(BF*3600/206264.806246)));
        op1=N0+BH;
        op2=E0+BG;
        TextView tv1=(TextView)findViewById(R.id.textView9);
        tv1.setText(String.valueOf(op1));
        TextView tv2=(TextView)findViewById(R.id.textView12);
        tv2.setText(String.valueOf(op2));
    }
}



