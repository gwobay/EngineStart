/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prod.intelligent7.engineautostart;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

//import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class ConnectDaemonService extends Service
        implements TcpConnectDaemon.StatusChangeListener
{
    //public static final int NOTIFICATION_ID = 1;
    public static int NOTIFICATION_ID;
    public static final String DAEMON_COMMAND="COMMAND";
    public static final ReentrantLock fileLock=new ReentrantLock(); 
    public static Ringtone noise=null;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Logger log;
    public ConnectDaemonService() {
        super();
    }
    public static final String TAG = "EAS JOB";

    //@Override
   // protected void onHandleIntent(Intent intent) {
        //onBind(intent);
        //Bundle extras = intent.getExtras();
        //GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        //String messageType = null;//gcm.getMessageType(intent);

        //if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

            } */
           // sendNotification("Received: " + extras.toString());
           // Log.i(TAG, "Received: " + extras.toString());
        //}
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //EASBroadcastReceiver.completeWakefulIntent(intent);
   // }
    public static String ramFileName=MainActivity.package_name+".profile";
    static TcpConnectDaemon mDaemon=null;
    ArrayBlockingQueue<String> outBoundMailBox;
    Thread hourlyAlarm;
    boolean stopHourAlarm;
    @Override
    public void onCreate() {
        ramFileName=MainActivity.package_name+".profile";
        log=Logger.getAnonymousLogger();
        log.info(getPackageName()+" got activated ");
        if (mDaemon==null ||
                !mDaemon.isAlive())
        startDaemon();
        startScheduledJobs();
        stopHourAlarm=false;
        hourlyAlarm=new Thread(new Runnable() {
            @Override
            public void run() {

                while(!stopHourAlarm){

                        try {
                            Thread.sleep(60 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    startScheduledJobs();
                }
            }
        });
        hourlyAlarm.start();
    }
    public static final String SERVER_IP="server_ip";
    public static final String SERVER_PORT="server_port";
    void startDaemon()
    {
        mDaemon=null;
        String mHost=getSharedPreferences(MainActivity.package_name+".profile", MODE_PRIVATE).getString(SERVER_IP, "220.134.85.189");
        //start a thread to talk to server every minute

        String mPort=getSharedPreferences(MainActivity.package_name+".profile", MODE_PRIVATE).getString(SERVER_PORT, "9696");
        //start a thread to talk to server every minute
        if (mHost.charAt(0)=='-'){
            mHost=getResources().getString(R.string.prod_server);
            mPort=getResources().getString(R.string.port);
        }
        mDaemon=new TcpConnectDaemon(mHost, Integer.parseInt(mPort));
        mDaemon.setModeInterval(TcpConnectDaemon.MODE_REPEAT, 60*1000);
        Vector<String> keep=null;
        if (outBoundMailBox!= null && outBoundMailBox.size() > 0)
        {
            keep=new Vector<String>();
            while (outBoundMailBox.size()> 0)
            {
                try {
                    keep.add(outBoundMailBox.take());
                } catch(InterruptedException e){}
            }
        }
        outBoundMailBox=mDaemon.getOutBoundDataQ();
        if (keep!=null && keep.size() > 0)
        {
            for (int i=0; i<keep.size(); i++){
                try {
                    outBoundMailBox.put(keep.get(i));
                } catch(InterruptedException e){}
            }
            keep.clear();
            keep=null;
        }
        mDaemon.attachToService(this);
        mDaemon.addListener(this);
        mDaemon.start();
        // need start schedule too; MainActivity.N_BOOT_PARAMS, nBootParam); //HH:MM-on minutes-off minutes-cycle last for minutes
        // MainActivity.ONE_BOOT_PARAMS, bootParam); //  yy/mm/dd:hh:mm-last for minutes
    }

    public void onDaemonDying(){
        if (mDaemon!=null && mDaemon.isAlive()){
            try {
                mDaemon.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startDaemon();
    }
    void confirmDaemonAlive()
    {
        if (mDaemon!=null && mDaemon.isAlive()) return;
        mDaemon=null;
        startDaemon();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        if (intent!=null)
            onBind(intent);
        return START_STICKY;//mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        String command=intent.getExtras().getString(DAEMON_COMMAND);
        confirmDaemonAlive();
        if (command != null)
        {
            if (command.charAt(0)=='M') {
                putInDaemonOutboundQ(command);
                //need to check if new schedule is set and interrupt the schedule thread to reschedule
                //mDaemon.putOutBoundMsg(command);
                //mDaemon.wakeUp4Command();
                //mDaemon.interrupt();
            }
            if (command.indexOf("SCHEDULE")<0) return null;

            stopScheduledJobs();
            startScheduledJobs();

        }
        return null;//mBinder;
    }

    boolean confirmHasMailBox()
    {
        if (mDaemon==null) return false;
        outBoundMailBox=mDaemon.getOutBoundDataQ();
        return (outBoundMailBox!=null);
    }

    public void putInDaemonOutboundQ(String msg){
        if (!confirmHasMailBox())
            return;
        final String outMsg=msg;
        final TcpConnectDaemon toWakeUp=mDaemon;

        new Thread(new Runnable(){
            public void run()
            {
                if (outBoundMailBox.size() == TcpConnectDaemon.Q_SIZE){
                    log.warning("Warning : too many msg in my Q");
                    return;
                }
                try {
                    outBoundMailBox.put(outMsg);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                toWakeUp.hasCommand=true;
                toWakeUp.interrupt();
            }
        }).start();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()

        return false;//mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

    int saveData(String tblName, String fixLine)
    {
    	String fileName="notification"+tblName;
    	int iPending=0;
    	Set<String> nullSet=null;
    	synchronized(fileLock){
    	SharedPreferences sharedPref = getSharedPreferences(fileName, Context.MODE_PRIVATE);
    	Set<String> pendingData=sharedPref.getStringSet("notification", nullSet);
    	if (pendingData==null)
    		pendingData=new HashSet<String>();
    	pendingData.add(fixLine);
    	SharedPreferences.Editor adder=sharedPref.edit();
    	adder.putStringSet("notification", pendingData);
    	adder.commit();
    	iPending=pendingData.size();
    	}
    	return iPending;
    }

    public static class MyTime{
        int year;
        int month;
        int day;
        int hour;
        int minute;
        public MyTime(GregorianCalendar gc)
        {
            year=gc.get(Calendar.YEAR);
            month=gc.get(Calendar.MONTH)+1;
            day=gc.get(Calendar.DAY_OF_MONTH);
            hour=gc.get(Calendar.HOUR_OF_DAY);
            minute=gc.get(Calendar.MINUTE);
        }
        public MyTime()
        {
            GregorianCalendar gc=new GregorianCalendar();
            year=gc.get(Calendar.YEAR);
            month=gc.get(Calendar.MONTH)+1;
            day=gc.get(Calendar.DAY_OF_MONTH);
            hour=gc.get(Calendar.HOUR_OF_DAY);
            minute=gc.get(Calendar.MINUTE);
        }
    }
    boolean car_theft;
    void makeNoise()
    {
    	if (car_theft)
        {
            noise = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);
            noise.play();
            return;
        }


        boolean toRing=false;
        //String fileName=MainActivity.getFileHeader()+ProfilePage.getTableName();
        String fileName=MainActivity.package_name+".profile";
        SharedPreferences mem = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String sRing=mem.getString(PickActivity.CURRENT_RINGTON, "--");//, String);
        if (sRing.charAt(0) == '-') return;
        Uri ringUri=Uri.parse(sRing);//, String);
        String t0=mem.getString(PickActivity.NO_NOISE_START, "--");//, noNoiseStart);
        String t1=mem.getString(PickActivity.NO_NOISE_END, "--");//, noNoiseEnd);
        String[] sT0=t0.split(":");
        int h0=Integer.parseInt(sT0[0]);
        int m0=Integer.parseInt(sT0[1]);
        sT0=t1.split(":");
        int h1=Integer.parseInt(sT0[0]);
        int m1=Integer.parseInt(sT0[1]);
        MyTime tm=new MyTime(new GregorianCalendar(TimeZone.getTimeZone(getResources().getString(R.string.my_time_zone_en))));
        int iNow=tm.hour*60+tm.minute;
        int iU=h1*60+m1;
    	int iL=h0*60+m0;
    	if (h1 < h0) 
        	{
        		toRing=(iNow > iU && iNow < iL );
        	}
        else
        	toRing=(iNow > iU || iNow < iL);
    	//android.os.Debug.waitForDebugger();
        if (toRing)
        {
        	noise=RingtoneManager.getRingtone(getApplicationContext(), ringUri);
        	noise.play();
        }   else noise=null;           
    }
    // Put the message into a notification and post it.

    private static HashMap<String, String> mcuDictionary;
    static public HashMap<String, String> getMcuCodeDictionary(){

        if (mcuDictionary==null) buildCodeDictionary();
        return mcuDictionary;
    }

    public static String getChinese(String code){
        String retS="";
        if (mcuDictionary==null) buildCodeDictionary();
        return retS+mcuDictionary.get(code);
    }
    private static void buildCodeDictionary() {
        mcuDictionary=new HashMap<String, String>();
        mcuDictionary.put("M1-00","冷气启动");
        mcuDictionary.put("M1-01","暖气启动");
        mcuDictionary.put("M2","车载机密码更换");
        mcuDictionary.put("M3","手机號码设定");
        mcuDictionary.put("M4-00","立即关闭引擎");
        mcuDictionary.put("M4-01","立即关闭冷气");
        mcuDictionary.put("M5","立即启动");
        //mcuDictionary.put("M5","立即启动");

        mcuDictionary.put("S110", "暖气设定成功");

        mcuDictionary.put("S111", "暖气设定失败");

        mcuDictionary.put("S100", "冷氣設定成功");

        mcuDictionary.put("S101", "冷氣設定失敗");

        mcuDictionary.put("S200", "车载机密码设定成功");

        mcuDictionary.put("S201", "车载机密码设定失败");

        mcuDictionary.put("S300", "手机号码设定成功");

        mcuDictionary.put("S301", "手机号码设定失败");

        mcuDictionary.put("S400", "引擎已关闭");

        mcuDictionary.put("S401", "引擎由车主启动,不能关闭");

        mcuDictionary.put("S410", "冷氣已關閉");

        mcuDictionary.put("S411", "冷氣關閉失敗");

        mcuDictionary.put("S500", "引擎已启动");

        mcuDictionary.put("S501", "引擎启动失败");

        mcuDictionary.put("S502", "引擎启动成功");

        mcuDictionary.put("S503", "偷车");

        mcuDictionary.put("S504", "暖车失败");

        mcuDictionary.put("S505", "暖车完毕");

        mcuDictionary.put("S999", "手机号码未授权");
    }


    //static //String header=MainActivity.getFileHeader();
    
    public void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        car_theft=false;
        if (msg.indexOf("505")>0)
            car_theft=true;
        makeNoise();
        String msgShow=msg;
        //msg :   msg in SXX@time<sender> format
        //SXX need translation from code to simplified chinese

        int i0=msg.indexOf("@");
        int idx=msg.indexOf("<");
        if (i0>0) msgShow=msg.substring(0, i0);
        else if (idx>0) msgShow=msg.substring(0, idx);
        String chinese=getChinese(msgShow);
        if (chinese==null) chinese="DAEMON STATUS";
        if (msg.toLowerCase().indexOf("finish") > 0)
        {
            startDaemon();
        }

        Intent jobIntent=null;//=new Intent(this, MainActivity.class);

        jobIntent=new Intent(this, MainActivity.class);//main will invoke other activities
        String tblName="";
        int iPending=1;
        


        jobIntent.putExtra("MCU_RESP", "EAS>>"+chinese);

        
        String header="EAS";//getResources().getString(R.string.candidate_name);
        String MSGs="EAS";//getResources().getString(R.string.msg_for_you);

        //DisplayMetrics metrics = new DisplayMetrics();
        //((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        //.setLargeIcon(Bitmap.createBitmap(metrics,80, 80, Bitmap.Config.ARGB_4444))//
        .setSmallIcon(R.drawable.engine_auto_start)
        .setContentTitle(header + "(" + iPending + ")")
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msgShow))
        .setContentText(chinese)
        .setLights(0xFF0000, 2000, 5000)
        .setAutoCancel(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, jobIntent, PendingIntent.FLAG_UPDATE_CURRENT); //the intent to open when clicked
        
        mBuilder.setContentIntent(contentIntent); //so MainActivity will be opened when notification is clicked
        mNotificationManager.notify(getPackageName().hashCode(), mBuilder.build());
        
        if (noise != null)
        {
        	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	noise.stop();
        	noise=null;
        }
    }
    
    public void localNotification(String msg)
    {
    	sendNotification(msg);
    }

    boolean killScheduledJob;
    OneTimeScheduler oneBootJob;

    ContinueScheduler onOffJob;

    public void startScheduledJobs()
    {
        killScheduledJob=false;
        if (oneBootJob== null || !oneBootJob.isAlive()){
        oneBootJob=new OneTimeScheduler(this, mDaemon);
        oneBootJob.start();
        }
        if (onOffJob == null || !onOffJob.isAlive()){
            onOffJob=new ContinueScheduler(this, mDaemon);
        onOffJob.start();
        }
    }
    public void stopScheduledJobs()
    {
        killScheduledJob=true;
        if (oneBootJob!= null && oneBootJob.isAlive())
            oneBootJob.killJob();
        if (onOffJob!= null && onOffJob.isAlive())
            onOffJob.killJob();
    }
    public void resetScheduledJobs()
    {
        if (oneBootJob!= null && oneBootJob.isAlive())
            oneBootJob.killJob();
        if (onOffJob!= null && onOffJob.isAlive())
            onOffJob.killJob();

        new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        //Thread.sleep(10000L);
                        onOffJob.join();
                        oneBootJob.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                startScheduledJobs();
            }
        }).start();
    }
    }

    class OneTimeScheduler extends JobScheduler {
        // MainActivity.ONE_BOOT_PARAMS, bootParam); //  yyyy/mm/dd-hh:mm-last for minutes
        public OneTimeScheduler(ConnectDaemonService cs, TcpConnectDaemon dm){
            super(cs, dm);
        }
        @Override
        protected void readParameter()
        {
            SharedPreferences sharedPref = mContext.getSharedPreferences(MainActivity.package_name + ".profile", Context.MODE_PRIVATE);
            String param=sharedPref.getString(MainActivity.ONE_BOOT_PARAMS,"--");
            if (param.charAt(0) != '-'){
                String[] terms=param.split("-");
                if (terms.length == 3){
                    int year=Integer.parseInt(terms[0].substring(0, 4));
                    int month=Integer.parseInt(terms[0].substring(5, 7));
                    int day=Integer.parseInt(terms[0].substring(8, 10));
                    GregorianCalendar gToday=new GregorianCalendar(TimeZone.getTimeZone(mContext.getResources().getString(R.string.my_time_zone_en)));
                    if (gToday.get(Calendar.YEAR) != year ||
                            gToday.get(Calendar.MONTH) != month ||
                            gToday.get(Calendar.DAY_OF_MONTH) != day) {
                        last4=-1;
                        return;
                    }
                    int iH=Integer.parseInt(terms[1].substring(0,2));
                    int iM=Integer.parseInt(terms[1].substring(3,5));
                    int iHr=gToday.get(Calendar.HOUR_OF_DAY);
                    int iMin=gToday.get(Calendar.MINUTE);
                    init_wait=((iH-iHr)*60+(iM-iMin))*60*1000;//in milli secs
                    last4=60*1000*Integer.parseInt(terms[2]); //in milli secs
                    //last4=init_wait+end_time;
                }
            }
        }
        public void setResetStatus(boolean ya)
        {
            isWakenByReset=ya;
        }

        public void run()
        {
            boolean okStart=false;
            boolean iWasReset=true;
            try {

                    readParameter();
                    //iWasReset=false;
                    if (on_time < 1) return;
                    if (last4 < 0) return;
                if (init_wait < -10*60*1000) return;
                    if (init_wait>0){
                    sleep(init_wait);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (!killScheduledJob)
                    ((ConnectDaemonService)mContext).resetScheduledJobs();
                    return;
                }


                sendStartCommand(on_time/60000);
                try {
                    sleep(last4);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();

                    sendStopCommand();
                    if (!killScheduledJob)
                    ((ConnectDaemonService)mContext).resetScheduledJobs();
                    return;
                }
                //long next_time=start_time+on_time+off_time;
                sendStopCommand();//in case it is still running
        }
    }

    class ContinueScheduler extends JobScheduler {
    // need start schedule too; MainActivity.N_BOOT_PARAMS, nBootParam); //HH:MM-on minutes-off minutes-cycle last for minutes

        /*boolean isWakenByReset=false;
        public void refresh(){

        }
        long start_time;
        long on_time;
        long off_time;
        long end_time;
        */
        public ContinueScheduler(ConnectDaemonService cs, TcpConnectDaemon dm){
            super(cs, dm);
        }
        @Override
        protected void readParameter()
        {
            SharedPreferences sharedPref = mContext.getSharedPreferences(MainActivity.package_name + ".profile", Context.MODE_PRIVATE);
            String param=sharedPref.getString(MainActivity.N_BOOT_PARAMS,"--");
            if (param.charAt(0) != '-'){
                String[] terms=param.split("-");
                if (terms.length == 4){
                    int icx=terms[0].indexOf(":");
                    if (icx < 0) {
                        last4=-1;
                        return;
                    }
                   int iH=Integer.parseInt(terms[0].substring(0,icx));
                    int iM=Integer.parseInt(terms[0].substring(icx+1));
                    GregorianCalendar gToday=new GregorianCalendar(TimeZone.getTimeZone(mContext.getResources().getString(R.string.my_time_zone_en)));
                    int iHr=gToday.get(Calendar.HOUR_OF_DAY);
                    int iMin=gToday.get(Calendar.MINUTE);
                    init_wait=((iH-iHr)*60+(iM-iMin))*60*1000; //in milli secs
                    on_time=1000*60*Integer.parseInt(terms[1]); //in milli secs
                    off_time=1000*3600*Integer.parseInt(terms[2]); //in milli secs
                    end_time=1000*60*Integer.parseInt(terms[3]); //in milli secs
                    last4=init_wait+end_time;
                }
            }
        }
        public void setResetStatus(boolean ya)
        {
            isWakenByReset=ya;
        }

        public void run()
        {
            boolean okStart=false;
            boolean iWasReset=true;
            try {
                    readParameter();
                    //iWasReset=false;
                    if (last4 < 0) return;
                    if (init_wait>0) {
                    sleep(init_wait);
                    iWasReset=false;
                    last4 -= init_wait;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                if (!killScheduledJob)
                    ((ConnectDaemonService)mContext).resetScheduledJobs();
                    return;
                }

            start_time=new Date().getTime();

            end_time = start_time+last4;

            while (start_time < end_time) {
                /*pending*/
                sendStartCommand(on_time/60000); //change to minute
                try {
                    sleep(on_time);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();

                    sendStopCommand();
                    if (!killScheduledJob)
                    ((ConnectDaemonService)mContext).resetScheduledJobs();
                    return;
                }
                //long next_time=start_time+on_time+off_time;
                sendStopCommand();//in case it is still running
                try {
                    sleep(off_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (!killScheduledJob)
                    ((ConnectDaemonService)mContext).resetScheduledJobs();
                    return;
                }
                start_time = new Date().getTime();
            }
        }
    }

