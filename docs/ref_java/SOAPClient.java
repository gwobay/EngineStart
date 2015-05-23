package com.cable.dctvcloud.demo;

/**
 * Created by erickou on 2015/4/25.
 */
    import android.app.Activity;
    import android.util.Log;
    import android.widget.Toast;

    import org.ksoap2.*;
    import org.ksoap2.serialization.*;
    import org.ksoap2.transport.*;

    import java.util.HashMap;

/*


    <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
            <soap:Body>
            <getDirectoryTree xmlns="http://www.changingtec.com/">
            <JsonFormatData>string</JsonFormatData>
            </getDirectoryTree>
            </soap:Body>
            </soap:Envelope>

   */
public class SOAPClient {

    private static final String SOAP_ACTION = "";
    private static final String NAMESPACE = "http://www.changingtec.com/";
    private static final String SERVER = "http://220.135.181.86:7080";//http://58.115.69.175";/////http://220.135.181.86:7080
    private static final String POST_AS = "/CableTVImageFileServer/IMAPI.asmx";


    private String method_name;
    private SoapSerializationEnvelope mEnvelope;
    private SoapObject mRequest;
    private String mResponse;

    private String myKey;
    private String myID;
    private String mySN;
    private String jsonDataString;
    Activity mActivity;
    SOAPClient(String mMethod)//= "getDirectoryTree")
    {
        method_name = mMethod;
        init();
    }


    SOAPClient() {
        method_name = "";
        init();
    }

    void setActivity(Activity aa)
    {
        mActivity=aa;
    }
    public void setUserProfile(String key, String id, String sn){
        myKey=key;
        myID=id;
        mySN=sn;
    }

    public void setJsonDataString(String inString)
    {
        jsonDataString=inString;
    }

    private void init() {
        mEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        mEnvelope.dotNet = true;
        mEnvelope.implicitTypes = true;

        mRequest = new SoapObject(NAMESPACE, method_name);
        mEnvelope.setOutputSoapObject(mRequest);
    }

    private void buildSoapBody(SoapObject request, String data)
    {
        String jsonData=data;//buildJsonRequest();
        request.addProperty("JsonFormatData", jsonData);
    }

    public String getResponse()
    {
        return mResponse;
    }

    public  Object getSendResult()
    {
        try {
            return mEnvelope.getResponse();
        }catch (SoapFault f){return null;}
    }
    public void sendRequest()//HashMap<String, String> jHash)//called from UI as async
    {
            buildSoapBody(mRequest, jsonDataString);
            HttpTransportSE httpTransportSE = new HttpTransportSE(SERVER+POST_AS);
            httpTransportSE.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
            try {

                String soapAction=NAMESPACE+method_name;

                httpTransportSE.debug=true;

                httpTransportSE.call(soapAction, mEnvelope);

                HashMap<String , String > toCheck=new HashMap<String, String>();
                boolean notFound=true;
                SoapObject resp=(SoapObject)mEnvelope.bodyIn;//getResponse();
                String respString=null;
                if (resp==null) return;
                mResponse=null;
                 for (int i=0; i<resp.getPropertyCount(); i++)
                    {
                        String jString=resp.getProperty(i).toString();
                        int iR=jString.indexOf("Changingtec");
                        if (iR<0) continue;
                        mResponse=jString.substring(iR);
                        //respString=jString;
                        break;
                    }
                String toastMsg="Got Msg from  Server :  "+mResponse;
                MyJson jResp=new MyJson(mResponse);
                String retCode=jResp.scanNameForData("Code");
                try {
                    if (retCode != null) {
                        if (Integer.parseInt(retCode) != 0) {
                            String msg = jResp.scanNameForData("Message");
                            String realMsg = MyJson.getBase64Msg(msg);
                            Log.w("JASON CODE", realMsg);
                        } /*else {
                            String fileUrl = jResp.scanNameForData("URL");
                            if (fileUrl==null) return;
                            String realUrl = MyJson.getBase64Msg(fileUrl);
                            Toast.makeText(mActivity, realUrl, Toast.LENGTH_LONG).show();
                        }*/
                    }
                }catch (NumberFormatException e){e.printStackTrace();}
                //Toast.makeText(mActivity, toastMsg, Toast.LENGTH_LONG).show();

                // Show the elements of the resultant vector.
               // StadiumNamesResult response = (StadiumNamesResult) mEnvelope.getResponse();
                //for (int i = 0; i < response.size(); i++) {
                    //System.out.println(response.elementAt(i));
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private String buildJsonRequest()
    {
        String json="";
        json +="{\"Changingtec\":";
        json += "{\"UserInfo\":";
        json += "{\"Key\":";
        json += "\"e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==\",";
        json += "\"UserID\":";
        json += "\"C6153873-13CD-4E1D-8B9A-47374DC8393F\",";
        json += "\"PhoneDevice\":";
        json += "\"GT-P3100\",";
        json += "\"PhoneSN\":";
        json += "\"B3A4D01A-4270-4D0F-A710-1311DECFFE4D\"}}}";
        return json;
    }
        public static void main(String[] args) {
            new SOAPClient();
        }

    }

