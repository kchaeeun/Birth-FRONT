package kr.ac.duksung.birth;

/*
 *
 * webnautes@naver.com
 *
 * 참고
 * https://github.com/googlesamples/android-BluetoothChat
 * http://www.kotemaru.org/2013/10/30/android-bluetooth-sample.html
 *
 * 주의사항) 애뮬레이터와 서버의 WIFI가 동일해야함
 */


import static android.text.TextUtils.split;

import static com.google.gson.internal.$Gson$Types.arrayOf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.duksung.birth.Retrofit.NumApiService;
import kr.ac.duksung.birth.Retrofit.Serial;
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver;
//import kr.ac.duksung.birth.service.RealService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import kr.ac.duksung.birth.alarm.AlarmManagerUtil;
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver;

public class BluetoothActivity extends AppCompatActivity
{
    private Intent serviceIntent;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_BLUETOOTH_ENABLE = 100;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 101;

    private TextView mConnectionStatus;
    private TextView mInputEditText;
    private TextView mName;

    ConnectedTask mConnectedTask = null;
    static BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    static boolean isConnectionError = false;
    private static final String TAG = "BluetoothClient";
    private String numValue = "sbs";
    private Integer boolValue;

    private Context mcontext;

    private static final String BASE_URL = "http://172.20.10.2:8080";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer noAction = intent.getIntExtra("no-action", -1);
            // noAction 값이 0인 경우
            if (noAction != null) {
                boolValue = noAction;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Intent intentNum = getIntent();
        numValue = intentNum.getStringExtra("num");
        boolValue = intentNum.getIntExtra("apiCallResult", -1);

        Log.d("happy", numValue);
        Log.d("bool", String.valueOf(boolValue));

        if (numValue != null && boolValue != -1) {
            makeApiCall(numValue);

        }

        IntentFilter filter = new IntentFilter("kr.ac.duksung.birth.DATA_ACTION");
        registerReceiver(receiver, filter);

        // 알림 설정
//        if (boolValue == 1) {
//            // 정적 선언
//            CheckAlarmReceiver.Companion.setupNotificationChannel(this);
//
//        }

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

//        if (RealService.serviceIntent==null) {
//            serviceIntent = new Intent(this, RealService.class);
//            startService(serviceIntent);
//        } else {
//            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
//            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
//        }

        // Bluetooth 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // Bluetooth 활성화 확인
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                showErrorDialog("This device does not support Bluetooth.");
            } else if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
            }
        }

//        Button sendButton = (Button)findViewById(R.id.send_button);
//        sendButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                String sendMessage = mInputEditText.getText().toString();
//                if ( sendMessage.length() > 0 ) {
//                    sendMessage(sendMessage);
//                }
//            }
//        });

        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        mInputEditText = (TextView)findViewById(R.id.input_string_text);
        mName = (TextView)findViewById(R.id.textView2);
//        ListView mMessageListview = (ListView) findViewById(R.id.message_listview);



        // SharedPreferences 객체 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // "num" 키에 해당하는 값을 저장
        editor.putString("num", String.valueOf(numValue));

        // 변경사항 적용
        editor.apply();

//
//        List<String> info = Arrays.asList(numValue.split("-"));
//
//        mName.setText(info.get(0) + " 임산부님 환영합니다.");
//
//        // 날짜 전처리
//        String year = info.get(1).substring(0,4);
//        String month = info.get(1).substring(4,6);
//        String day = info.get(1).substring(6);
//        String date = year + "년 " + month + "월 " + day + "일";
//        mInputEditText.append(date);


        mConversationArrayAdapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1 );
//        mMessageListview.setAdapter(mConversationArrayAdapter);

        // 권한 처리를 계속 해줘야함. 메서드마다 *************
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가
//            startBluetoothService();
        }

        Log.d( TAG, "Initalizing Bluetooth adapter...");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showErrorDialog("This device is not implement Bluetooth.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
        }
        else {
            Log.d(TAG, "Initialisation successful.");
            startBluetoothConnection();
        }

        Log.d(TAG, "numValue in onCreate: " + numValue);
    }

    private void startBluetoothService() {
        Log.d(TAG, "numValue in startBluetoothService: " + numValue);

//        String deviceAddress = "YOUR_DEVICE_ADDRESS";  // 연결할 Bluetooth 장치의 주소를 설정하세요.
//        Intent serviceIntent = new Intent(this, TestService.class);
//        serviceIntent.putExtra("device_address", deviceAddress);
//        ContextCompat.startForegroundService(this, serviceIntent);

//        Intent intent = new Intent(getApplicationContext(), BluetoothService.class); // 실행시키고픈 서비스클래스 이름
//        intent.putExtra("command", numValue); // 필요시 인텐트에 필요한 데이터를 담아준다
//        startService(intent); // 서비스 실행!
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            } else {
                // 권한이 거부된 경우 사용자에게 알림 표시 또는 다른 조치 수행
            }
        }
    }

    // 주기적으로 메시지를 보내기 위한 Handler
    private final Handler mHandler = new Handler();
    private static final int MESSAGE_SEND_INTERVAL = 5000; // 5 초

    private final Runnable mSendRunnable = new Runnable() {
        @Override
        public void run() {
            // boolValue가 1일 때만 메시지를 보냅니다.
            sendMessage(boolValue);

            Log.d("boolValue", boolValue.toString());

            // 일정한 간격 이후에 다음 메시지를 보낼 수 있도록 예약
            mHandler.postDelayed(this, MESSAGE_SEND_INTERVAL);
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        // 액티비티가 다시 시작될 때 메시지를 주기적으로 보내기 시작
        mHandler.postDelayed(mSendRunnable, MESSAGE_SEND_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }

        if ( mConnectedTask != null ) {

            mConnectedTask.cancel(true);
        }
    }

    private void makeApiCall(String serialNumber) {
        NumApiService apiService = getRetrofitInstance().create(NumApiService.class);
        Call<Serial> call = apiService.getBySerial(serialNumber);

        call.enqueue(new Callback<Serial>() {
            @Override
            public void onResponse(Call<Serial> call, Response<Serial> response) {
                if (response.isSuccessful()) {
                    Serial serial = response.body();
                    if (serial != null) {
                        String name = serial.getName();
                        String expireDate = serial.getExpireDate();

                        runOnUiThread(() -> {
                            if (expireDate != null) {
                                if (boolValue == 1) {
                                    mName.setText(name + " 임산부님 환영합니다.");
                                    mInputEditText.append(expireDate.toString());
                                } else {
                                    mName.setText("임산부 시리얼 넘버 만료 기간이 종료되었습니다.");
                                    mInputEditText.append(expireDate.toString());
                                    Toast.makeText(BluetoothActivity.this,"임산부 인증에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mName.setText("임산부가 아닙니다.");
                                mInputEditText.setText("");
                                Log.e("Error", "expireDate is done");
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            mName.setText("임산부가 아닙니다.");
                            mInputEditText.setText("");
                            Log.e("Error", "expireDate is done");

                            Toast.makeText(BluetoothActivity.this,"임산부 인증에 실패하였습니다.", Toast.LENGTH_LONG).show();

                        });
                    }
                } else {
                    // 서버 응답이 실패한 경우의 처리
                    Log.e("Retrofit Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Serial> call, Throwable t) {
                Log.e("Retrofit Error", "Failure: " + t.getMessage());
            }
        });
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;

            if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
            } else {
                // 권한이 이미 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            }
            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d( TAG, "create socket for "+mConnectedDeviceName);

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }

            mConnectionStatus.setText("connecting...");
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "numValue in ConnectTask doInBackground: " + numValue);

            if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
            } else {
                // 권한이 이미 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            }
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }

                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean isSucess) {

            if ( isSucess ) {
                connected(mBluetoothSocket);

                // 호출 조건 추가
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    // 이제 boolValue가 1인 경우에만 알람 매니저 설정
//                    Log.d("alarmManagerUtil", "alarmStart");
//                    AlarmManagerUtil.Companion.setRepeatingAlarm(getApplicationContext());
//                }
//                checkNotificationPermission();
            }

            else{

                isConnectionError = true;
                Log.d( TAG,  "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }


    public void connected( BluetoothSocket socket ) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();

        // 연결이 성립되면 자동으로 메시지 전송 - sendButton 대신 실행
        Integer sendMessage = boolValue;
        Log.d("sbs", String.valueOf(sendMessage));
        if (sendMessage != null) {
            Log.d("write", sendMessage.toString());
            mConnectedTask.write(sendMessage);
        }
    }



    private class ConnectedTask extends AsyncTask<Void, Integer, Boolean> {

        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.d( TAG, "connected to "+mConnectedDeviceName);
            mConnectionStatus.setText( "connected to "+mConnectedDeviceName);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "numValue in ConnectedTas " + numValue);

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;


            while (true) {

                if ( isCancelled() ) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if(bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        int receivedValue = ByteBuffer.wrap(packetBytes).getInt();
                        Log.d("receivedValue", String.valueOf(receivedValue));

                        publishProgress(receivedValue);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    return false;
                }
//                        for(int i=0;i<bytesAvailable;i++) {
//
//                            byte b = packetBytes[i];
//                            if(b == '\n')
//                            {
//                                byte[] encodedBytes = new byte[readBufferPosition];
//                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
//                                        encodedBytes.length);
//                                String recvMessage = new String(encodedBytes, "UTF-8");
//
//                                readBufferPosition = 0;
//
//                                Log.d(TAG, "recv message: " + recvMessage);
//                                publishProgress(recvMessage);
//                            }
//                            else
//                            {
//                                readBuffer[readBufferPosition++] = b;
//                            }
//                        }

            }

        }

        void write(int msg) {
            try {
                // ByteBuffer to convert int to bytes
                ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(msg);

                mOutputStream.write(buffer.array());
                mOutputStream.flush();
                Log.d(TAG, "Sent message: " + msg);
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e);
            }
        }


        @Override
        protected void onProgressUpdate(Integer... receivedValue) {
            int value = receivedValue[0];
            Log.d("vvvv", String.valueOf(value));
            if (value == 1) {
                CheckAlarmReceiver.Companion.setupNotificationChannel(BluetoothActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 이제 boolValue가 1인 경우에만 알람 매니저 설정
                    Log.d("alarmManagerUtil", "alarmStart");
                    AlarmManagerUtil.Companion.setRepeatingAlarm(getApplicationContext());
                }
                checkNotificationPermission();
            } else if (value == 2) {
                boolValue = 1;
            }


        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

            if ( !isSucess ) {


                closeSocket();
                Log.d(TAG, "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket(){

            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        //int 값을 표현하는 바이트를 전송하는 문제를 ByteBuffer을 사용해 해결
        void write(Integer msg) {
            try {
                // ByteBuffer를 생성하고 int 값을 해당 ByteBuffer에 기록
                ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(msg);


                // ByteBuffer의 내용을 바이트 배열로 가져와 전송
                mOutputStream.write(buffer.array());
                mOutputStream.flush();
                Log.d(TAG, "Sent message: " + msg);
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e);
            }
        }

    }

    private void startBluetoothConnection() {
        if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가
        }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.d("pariedDevicesALL", "Paired Devices: " + pairedDevices);
            if (pairedDevices != null && !pairedDevices.isEmpty()) {
                // 원하는 기기 이름을 설정
                String desiredDeviceName = "Line3-1A";

                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(desiredDeviceName)) {
                        ConnectTask connectTask = new ConnectTask(device);
                        connectTask.execute();
                        // 원하는 기기를 찾았으면 반복문 종료
                        break;
                    }
                }

        } else {
            // Bluetooth가 비활성화된 경우 사용자에게 블루투스 활성화 요청
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }



    public void showPairedDevicesListDialog()
    {

        if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가
        }
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if ( pairedDevices.length == 0 ){
            showQuitDialog( "No devices have been paired.\n"
                    +"You must pair it with another device.");
            return;
        }

        String[] items;
        items = new String[pairedDevices.length];
        for (int i=0;i<pairedDevices.length;i++) {
            items[i] = pairedDevices[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("앉은 자리를 선택하세요");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                ConnectTask task = new ConnectTask(pairedDevices[which]);
                task.execute();
            }
        });
        builder.create().show();
    }



    public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if ( isConnectionError  ) {
                    isConnectionError = false;
                    finish();
                }
            }
        });
        builder.create().show();
    }


    public void showQuitDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    void sendMessage(Integer msg){

        if ( mConnectedTask != null ) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);
            mConversationArrayAdapter.insert("Me:  " + msg, 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                //BlueTooth is now Enabled
//                showPairedDevicesListDialog();
            }
            if (resultCode == RESULT_CANCELED) {
                showQuitDialog("You need to enable bluetooth");
            }
        }
    }

    private void checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 요청
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    CheckAlarmReceiver.REQUEST_NOTIFICATION_PERMISSION
            );
        }
    }



}