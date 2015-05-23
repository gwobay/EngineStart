package com.cable.dctvcloud.demo;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by erickou on 2015/4/26.
 */
public class MyJson {

    public final static int SPLIT_SIZE=200*1024;
    public final static String TESTKEY="e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==";
    public final static String USRID="C6153873-13CD-4E1D-8B9A-47374DC8393F";
    public final static String TESTSN="B3A4D01A-4270-4D0F-A710-1311DECFFE4D";
    public final static String TESTPHONE="GT-P3100";

    String mString;

    public MyJson()
    {
        mString = null;
    }
    public MyJson(String s)
    {
        mString=s;
    }


    private String buildRequestJsonHead(String key, String usrId, String myPhoneName, String phoneSN)
    {
        String json="";
        json +="{\"Changingtec\":";
        json += "{\"UserInfo\":";
        json += "{\"Key\":";
        json += "\""+key+"\",";//"\"e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==\",";
        json += "\"UserID\":";
        json += "\""+usrId+"\",";//"\"C6153873-13CD-4E1D-8B9A-47374DC8393F\",";
        json += "\"PhoneDevice\":";
        json += "\""+myPhoneName+"\",";
        json += "\"PhoneSN\":";
        json += "\""+phoneSN+"\"";//"\"B3A4D01A-4270-4D0F-A710-1311DECFFE4D\"}}}";
        //json += "}}}";
        return json;
    }

    public String getDirectoryRequestJason(String key, String usrId, String phoneName, String phoneSN)
    {
        return buildRequestJsonHead(key, usrId, phoneName, phoneSN)+"}}}";
    }

    static public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("OOOz..", "Out of memory error ");
        }
        return temp;
    }

    public String buildJsonToDownLoadRequest(String key, String usrId, String phoneName, String phoneSN,
                                           String fileId)
    {
        /*
        {"Changingtec":
            {"UserInfo":
                {"Key":"[密鑰]",
                        "UserID":"Admin",
                        "PhoneDevice":"[手機裝置名稱]",
                        "PhoneSN":"[手機序號]"},
                "File":{"Name":"[檔案名稱]",
                    "ID":"[fileId"}
            }
            }
        }
        */

        String json=buildRequestJsonHead(key, usrId, phoneName,phoneSN);
        json += "},";
        json +="\"File\":";
        json += "{\"ID\":";
        json += "\""+fileId+"\"";
        json += "}}}";
        return json;
    }

    public String buildJsonToUpLoadRequest(String key, String usrId, String pName,String phoneSN,
                                            String fileName, long fileSize,String ext, int splitCount,
                                            String category, String targetDirectory)
    {
        /*
        {"Changingtec":
            {"UserInfo":
                {"Key":"[密鑰]",
                        "UserID":"Admin",
                        "PhoneDevice":"[手機裝置名稱]",
                        "PhoneSN":"[手機序號]"},
                "File":{"Name":"[檔案名稱]",
                    "Size":"[檔案尺寸(Byte)]",
                    "Extension":"[副檔名]",
                    "PackageNumber":"[切割檔案總數]",
                    "Type":{"@Class":"Videos|Files|Images","#text":"[自訂資料夾路徑]"}
            }
            }
        }
        */
        String json=buildRequestJsonHead(key, usrId, pName, phoneSN);
        json += "},";
        json +="\"File\":";
        json += "{\"Name\":";
        json += "\""+fileName+"\",";
        json += "\"Size\":";
        json += "\""+fileSize+"\",";
        json += "\"Extension\":";
        json += "\""+ext+"\",";
        json += "\"PackageNumber\":";
        json += "\""+splitCount+"\",";
        json += "\"Type\":{";
        json += "\"@Class\":";
        json += "\""+category+"\",";
        json += "\"#text\":";
        json += "\""+targetDirectory+"\"";
        json += "}}}}";
        return json;
    }

    public String buildJsonUpLoadingRequest(String fileID,  int splitSize,
                                            int splitIndex, String endCodedImg)
                                            //Bitmap icon)
    {
      /*
    {"Changingtec":{"File":
        {"ID":"87b1f5d5-e342-4b1b-9fa2-a9aa85a94bbe","Size":"[切割檔案尺寸(Byte)]",
        "PackageIndex":"[切割檔案索引(從0開始)]","Serialize":"[Base64]"}}}*/
        String json = "{\"Changingtec\":{";
        json +="\"File\":";
        json += "{\"ID\":";
        json += "\""+fileID+"\",";
        json += "\"Size\":";
        json += "\""+splitSize+"\",";
         json += "\"PackageIndex\":";
        json += "\""+splitIndex+"\",";
        json += "\"Serialize\":";
        json += "\""+endCodedImg+"\"";
        json += "}}}";
        return json;
    }

    public int scanForCode(String jData)
    {
        int idx=jData.indexOf("Code");
        if (idx <0) return -1;
        int i0= idx + 7;
        while (jData.charAt(++i0) != '"');
        try {
            String sCode=jData.substring(idx+8, i0);
            idx=Integer.parseInt(sCode);
        } catch (NumberFormatException e)
        {
            return -1;
        }
        return idx;
    }

    public String scanNameForData(String name)
    {
        String jData=mString;
        int iDx=jData.indexOf(name);
       if (iDx < 0) return null;
        int icol=jData.indexOf(":");
        if (icol <0) return null;

        int iMax=jData.length();
        //assume name is " quoted
        //iDx += name.length();
        while (iDx < iMax && jData.charAt(++iDx) != '"');
        if (iDx == iMax ) return null;
        int i0=iDx+1;
        while (i0 < iMax && jData.charAt(++i0) != '"');
        if (i0 == iMax ) return null;
        int i9 = ++i0;
        //StringBuffer buf=new StringBuffer();
        while (i9 < iMax && jData.charAt(i9) != '"')
        {
            //buf.append(jData.charAt(i9));
            i9++;
        }
        if (i9 == iMax ) return null;
        String retS=jData.substring(i0, i9);
        return retS;
    }

    static public String getBase64Msg(String sCoded)
    {
        byte[] sByte=Base64.decode(sCoded.getBytes(), Base64.DEFAULT);
        if (sByte.length < 1 || sByte == null) return null;
        return new String(sByte);
    }
}

