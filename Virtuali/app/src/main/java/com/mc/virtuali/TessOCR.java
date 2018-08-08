package com.mc.virtuali;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by adc on 7/4/18.
 */

public class TessOCR  {
    private TessBaseAPI mTess;

    public TessOCR() {
        mTess = new TessBaseAPI();
        // AssetManager assetManager=
        String datapath = Environment.getExternalStorageDirectory() + "/DemoOCR/";
        String language = "eng";
        // AssetManager assetManager = getAssets();
        File dir = new File(datapath + "/tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        String ar[]=result.split(" ");

        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
    int edit_distance(String s1, String s2 )
    {

        int m=s1.length();
        int n=s2.length();
        int dp[][] = new int[m+1][n+1];
        for (int i=0;i<=m;i++)
        {
            for (int j=0;j<=n;j++)
            {
                if(j==0)
                    dp[i][j]=i;

                else if(i==0)
                    dp[i][j]=j;

                else if(s1.charAt(i-1)==s2.charAt(j-1))
                    dp[i][j]=dp[i-1][j-1];

                else
                    dp[i][j]=1+min(dp[i-1][j-1],dp[i-1][j],dp[i][j-1]);

            }
        }

        return dp[m][n];
    }

    public int min(int a, int b, int c){
        if(a > b && a > c)
            return a;
        else
        if(b > c)
            return b;
        else
            return c;
    }
}
