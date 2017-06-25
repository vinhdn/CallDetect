package com.vinhdn.phonedetect;

/**
 * Created by vinh on 6/25/17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CallRecordingService extends Service {
    public static final int NOTIFICATION_ID_RECEIVED = 4641;
    private static String phoneNumber;
    private String LOG_TAG = CallRecordingService.class.getName();
    private String fileName = "";
    private boolean isDelete = false;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private boolean prepareFailed = false;
    private boolean startFailed = false;
    public static final int STATE_INCOMING_NUMBER = 0;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;

    private void showNotification(String content, boolean isCancel,
                                  String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder localBuilder = new Notification.Builder(this);
            localBuilder.setSmallIcon(R.mipmap.ic_launcher);
            localBuilder
                    .setContentTitle(getString(R.string.record_call_starting));
            localBuilder.setContentText(content);
            Notification notification;
            localBuilder.setAutoCancel(isCancel);
//
//			/*
//			 * Action Add note
//			 */
//            Intent makeNoteIntent = new Intent(this, MakeNoteActivity.class);
//            makeNoteIntent.putExtra("message", content);
//            makeNoteIntent.putExtra(getString(R.string.record_call_filename),
//                    filePath);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addParentStack(MakeNoteActivity.class);
//            stackBuilder.addNextIntent(makeNoteIntent);
//            PendingIntent pendingMakeNote = stackBuilder.getPendingIntent(0,
//                    PendingIntent.FLAG_CANCEL_CURRENT);
//
//			/*
//			 * Action Delete record
//			 */
//            Intent deleteIntent = new Intent(this, CallRecordingService.class);
//            deleteIntent.putExtra("DELETE", filePath);
//            PendingIntent pendingDelete = PendingIntent.getService(this, 3,
//                    deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            if (!filePath.equals("")) {
//                localBuilder.setContentIntent(pendingMakeNote);
//                localBuilder.addAction(R.drawable.add_icon,
//                        getString(R.string.new_note), pendingMakeNote);
//                localBuilder.addAction(R.drawable.delete_icon,
//                        getString(R.string.record_call_delete), pendingDelete);
//            }

            notification = localBuilder.build();

            if (!isCancel) {
                notification.flags = Notification.FLAG_ONGOING_EVENT;
            } else {
                notification.flags = Notification.FLAG_AUTO_CANCEL;
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(this.LOG_TAG, 0, notification);
            return;
        }

        NotificationCompat.Builder localBuilder = new NotificationCompat.Builder(
                this);
        localBuilder.setSmallIcon(R.mipmap.ic_launcher);
        localBuilder.setContentTitle(getString(R.string.record_call_starting));
        localBuilder.setContentText(content);
        Notification notification;
        localBuilder.setAutoCancel(isCancel);

//		/*
//		 * Action Delete record
//		 */
//        Intent deleteIntent = new Intent(this, CallRecordingService.class);
//        deleteIntent.putExtra("DELETE", filePath);
//        PendingIntent pendingDelete = PendingIntent.getService(this, 3,
//                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        if (!filePath.equals("")) {
//            localBuilder.setContentIntent(pendingMakeNote);
//            localBuilder.addAction(R.drawable.add_icon,
//                    getString(R.string.new_note), pendingMakeNote);
//            localBuilder.addAction(R.drawable.delete_icon,
//                    getString(R.string.record_call_delete), pendingDelete);
//        }
        notification = localBuilder.build();

        if (!isCancel) {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        } else {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(this.LOG_TAG, 0, notification);
    }

    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.d("OnCreate", "Start::");
        // if (this.isDelete)
        // ;
        // Thread aThread = new Thread(this);
        // aThread.start();
    }

    public void onDestroy() {
        super.onDestroy();
        if (!this.isDelete)
            stopRecording();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("OnStartCommand", "on");
        int commandType = intent.getIntExtra("commandType",
                STATE_INCOMING_NUMBER);
        String str = intent.getStringExtra("DELETE");
        if ((str != null) && (!str.equals(""))) {
            Log.d("onStartCommand", "DELETE: " + str);
            this.isDelete = true;
            phoneNumber = null;
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancelAll();
            File f = new File(str);
            Log.d("isDelete", "" + f.delete());
            stopSelf();
            return START_NOT_STICKY;
        }
        if (commandType == STATE_INCOMING_NUMBER) {
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");
        } else if (commandType == STATE_CALL_START) {
            if (phoneNumber == null)
                phoneNumber = intent.getStringExtra("phoneNumber");

            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.reset();
                mediaRecorder
                        .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                mediaRecorder
                        .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder
                        .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                this.fileName = createFileRecordCall();
                Log.d("FileName", fileName);
                mediaRecorder.setOutputFile(fileName);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return terminateAndEraseFile();
            } catch (Exception e) {
                e.printStackTrace();
                return terminateAndEraseFile();
            }

            OnErrorListener errorListener = new OnErrorListener() {

                public void onError(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e("OnErrorListener: ", arg1 + "," + arg2);
                    arg0.stop();
                    arg0.reset();
                    arg0.release();
                    arg0 = null;
                    terminateAndEraseFile();
                }
            };
            //mediaRecorder.setOnErrorListener(errorListener);
            OnInfoListener infoListener = new OnInfoListener() {

                public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
                    Log.e("OnInfoListener: ", arg1 + "," + arg2);
                    arg0.stop();
                    arg0.reset();
                    arg0.release();
                    arg0 = null;
                    terminateAndEraseFile();
                }
            };
            //mediaRecorder.setOnInfoListener(infoListener);

            try {
                mediaRecorder.prepare();
                Thread.sleep(1000);
                this.mediaRecorder.start();
                this.isRecording = true;
                showNotification("Call Recording", false, "");

            } catch (IllegalStateException e) {
                e.printStackTrace();
                terminateAndEraseFile();
                //e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                terminateAndEraseFile();
                e.printStackTrace();
            } catch (Exception e) {
                terminateAndEraseFile();
                e.printStackTrace();
            }

        } else if (commandType == STATE_CALL_END) {
            stopRecording();
            showNotification("Record finished", true, "");
        }

        return START_NOT_STICKY;
    }

    /**
     * in case it is impossible to record
     */
    private int terminateAndEraseFile() {
        if (mediaRecorder != null)
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        this.isRecording = false;
        showNotification(
                "Unfortunately, it is not possible to record calls in this device.",
                true, "");
        return START_NOT_STICKY;
    }

    void stopRecording() {
        Log.d("CallRecord", "StopRecording " + isRecording);
        if (this.isRecording) {
            Log.d("CallRecord", "StopRecording");
            this.mediaRecorder.stop();
            this.isRecording = false;
            showNotification("Record finished", true,
                    this.fileName);
        }
        if (this.mediaRecorder != null) {
            this.mediaRecorder.reset();
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
        this.stopSelf();
    }

    public static String createFileRecordCall()
    {
        File file = new File(Environment
                .getExternalStorageDirectory(), "CallRecord");
        file.mkdirs();
        String str = file.getAbsolutePath() + "/%s.3gp";
        String filePath = "Record_call_" + System.currentTimeMillis();
        return String.format(str, filePath);
    }
}
