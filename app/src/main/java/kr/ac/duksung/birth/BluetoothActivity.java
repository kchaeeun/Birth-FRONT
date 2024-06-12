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


import static androidx.core.app.ActivityCompat.startActivityForResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.ac.duksung.birth.Retrofit.NumApiService;
import kr.ac.duksung.birth.Retrofit.Serial;
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver;
//import kr.ac.duksung.birth.service.RealService;

import kr.ac.duksung.birth.alarm.AlarmManagerUtil;
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver;
import kr.ac.duksung.birth.alarm.SeatReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BluetoothActivity extends AppCompatActivity
{
    private Intent serviceIntent;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_BLUETOOTH_ENABLE = 100;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 101;

    private TextView mInputEditText;
    private TextView mName;
    private TextView noCertifi;
    private TextView certifiText;
    private ConstraintLayout layout;

    ConnectedTask mConnectedTask = null;
    static BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    static boolean isConnectionError = false;
    private static final String TAG = "BluetoothClient";
    private String numValue = "sbs";
    private Integer boolValue;

    private Context mcontext;
    private AppCompatButton btnSeat;
    private AppCompatButton btnCerti;


    private static final String BASE_URL = "http://192.168.191.141:8080";
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
            Log.d("noActiont value", String.valueOf(noAction));
            // noAction 값이 0인 경우
            if (noAction != null) {
                boolValue = noAction;
            }
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, RECEIVER_EXPORTED);
        }


//        // 알림 설정
        if (boolValue == 1) {
            // 정적 선언
            CheckAlarmReceiver.Companion.setupNotificationChannel(this);

        }
//
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            showErrorDialog("This device does not support Bluetooth.");
        } else if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            Log.d(TAG, "Initialisation successful.");
//            startBluetoothConnection();
            //bluetooth is enabled, proceed with your Bluetooth operations
        }

        mInputEditText = (TextView) findViewById(R.id.input_string_text);
        mName = (TextView) findViewById(R.id.textView2);
        noCertifi = (TextView) findViewById(R.id.tv_no_certifi);
        certifiText = (TextView) findViewById(R.id.certi_text);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayout2);
        btnSeat = (AppCompatButton) findViewById(R.id.appCompatButton2);
        btnCerti = (AppCompatButton) findViewById(R.id.appCompatButton);

        //        ListView mMessageListview = (ListView) findViewById(R.id.message_listview);
        // include된 레이아웃의 루트 뷰를 찾습니다.
        View includeView = findViewById(R.id.include);

        // include된 레이아웃 내의 이미지 버튼에 접근합니다.
        ConstraintLayout imageButton = includeView.findViewById(R.id.constraint11);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d("버튼 눌림 확인", "눌림");
            }
        });

        ConstraintLayout imageButton2 = includeView.findViewById(R.id.constraintLayout10);


        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("버튼 눌림 확인", "눌림");
                Intent intent = new Intent(getApplicationContext(), SirenActivity.class);
                startActivity(intent);
            }
        });

        btnSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeatActivity.class);
                startActivity(intent);
            }
        });

        btnCerti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        // SharedPreferences 객체 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // "num" 키에 해당하는 값을 저장
        editor.putString("num", String.valueOf(numValue));

        // 변경사항 적용
        editor.apply();

        mConversationArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
//        mMessageListview.setAdapter(mConversationArrayAdapter);

        // 권한 처리를 계속 해줘야함. 메서드마다 *************
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            }, REQUEST_BLUETOOTH_PERMISSION);
        }  // 권한이 이미 허용된 경우 블루투스 작업 수행
        // 여기에 블루투스 관련 코드 추가
        //            startBluetoothService();


        Log.d( TAG, "Initalizing Bluetooth adapter...");

        mBluetoothAdapter = bluetoothAdapter;
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

//    private void startBluetoothService() {
//        Log.d(TAG, "numValue in startBluetoothService: " + numValue);
//
//        String deviceAddress = "YOUR_DEVICE_ADDRESS";  // 연결할 Bluetooth 장치의 주소를 설정하세요.
//        Intent serviceIntent = new Intent(this, TestService.class);
//        serviceIntent.putExtra("device_address", deviceAddress);
//        ContextCompat.startForegroundService(this, serviceIntent);
//
//        Intent intent = new Intent(getApplicationContext(), BluetoothService.class); // 실행시키고픈 서비스클래스 이름
//        intent.putExtra("command", numValue); // 필요시 인텐트에 필요한 데이터를 담아준다
//        startService(intent); // 서비스 실행!
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 권한이 허용된 경우 블루투스 작업 수행
        // 여기에 블루투스 관련 코드 추가
        // 권한이 거부된 경우 사용자에게 알림 표시 또는 다른 조치 수행
    }
//
    // 주기적으로 메시지를 보내기 위한 Handler
    private final Handler mHandler = new Handler(Looper.getMainLooper());
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
//
//
    @Override
    public void onResume() {
        super.onResume();

        // 액티비티가 다시 시작될 때 메시지를 주기적으로 보내기 시작
        mHandler.postDelayed(mSendRunnable, MESSAGE_SEND_INTERVAL);
    }
//
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
                                    mName.setVisibility(View.VISIBLE);
                                    mName.setText(name);
                                    Log.d("이름확인",name);
                                    mInputEditText.setVisibility(View.VISIBLE);
                                    mInputEditText.setText(expireDate);
                                    certifiText.setVisibility(View.VISIBLE);
                                    layout.setVisibility(View.VISIBLE);
                                    noCertifi.setVisibility(View.GONE);

                                    SharedPreferences sharedPreferences = getSharedPreferences("seat-change", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("changeSeatColor", true);
                                    editor.apply();

//                                    showPairedDevicesListDialog();
                                } else if (boolValue == 0) {
                                    mName.setText(name);
                                    layout.setVisibility(View.VISIBLE);
                                    noCertifi.setVisibility(View.GONE);
                                    certifiText.setVisibility(View.VISIBLE);
                                    certifiText.setText("인증이 만료되었습니다.");
                                    Toast.makeText(BluetoothActivity.this, "임산부 인증에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("Error", "expireDate is done");
                            }
                        });
                    } else {
                        runOnUiThread(() -> {

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
            public void onFailure(Call<Serial> call, @NonNull Throwable t) {
                Log.e("Retrofit Error", "Failure: " + t.getMessage());
            }
        });
    }

    public class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;

            if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                }, REQUEST_BLUETOOTH_PERMISSION);
            }  // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가

            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d( TAG, "create socket for "+mConnectedDeviceName);

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }
        }


        @Override
        public Boolean doInBackground(Void... params) {
            Log.d(TAG, "numValue in ConnectTask doInBackground: " + numValue);

            if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                }, REQUEST_BLUETOOTH_PERMISSION);
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
                // 이제 boolValue가 1인 경우에만 알람 매니저 설정
                Log.d("alarmManagerUtil", "alarmStart");
                AlarmManagerUtil.Companion.setRepeatingAlarm(getApplicationContext());
                if (Build.VERSION.SDK_INT >= 34) {
                    checkNotificationPermission();
                }
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
//
//
//
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
//            mConnectionStatus.setText( "connected to "+mConnectedDeviceName);
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
                // 이제 boolValue가 1인 경우에만 알람 매니저 설정
                Log.d("alarmManagerUtil", "alarmStart");
                AlarmManagerUtil.Companion.setRepeatingAlarm(getApplicationContext());
                if (Build.VERSION.SDK_INT >= 34) {
                    checkNotificationPermission();
                }
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
//
    private void startBluetoothConnection() {

        if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // 위치 권한이 이미 허용된 경우 블루투스 작업 수행
//            BluetoothAdapter bluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
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
            }
        }
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
//
//
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
//
    void sendMessage(Integer msg){

        if ( mConnectedTask != null ) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);
            mConversationArrayAdapter.insert("Me:  " + msg, 0);
        }
    }
//
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                //BlueTooth is now Enable
                Log.d("연결 기기들", "확인완료");
            }
            if (resultCode == RESULT_CANCELED) {
                showQuitDialog("You need to enable bluetooth");
            }
        }
    }
//
    @RequiresApi(api = 34)
    private void checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DEVICE_POLICY_ACCESSIBILITY)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 요청
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.MANAGE_DEVICE_POLICY_ACCESSIBILITY},
                    CheckAlarmReceiver.REQUEST_NOTIFICATION_PERMISSION
            );
        }
    }


}