package kr.ac.duksung.birth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.ac.duksung.birth.Retrofit.NumApiService
import kr.ac.duksung.birth.Retrofit.Serial
import kr.ac.duksung.birth.alarm.AlarmManagerUtil.Companion.setRepeatingAlarm
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver.Companion.setupNotificationChannel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.UUID

/*
 *
 * webnautes@naver.com
 *
 * 참고
 * https://github.com/googlesamples/android-BluetoothChat
 * http://www.kotemaru.org/2013/10/30/android-bluetooth-sample.html
 * 
 * 주의사항) 애뮬레이터와 서버의 WIFI가 동일해야함
 */ //import kr.ac.duksung.birth.service.RealService;
class BluetoothActivity : AppCompatActivity() {
    private var serviceIntent: Intent? = null
    private val REQUEST_BLUETOOTH_ENABLE = 100
    private var mConnectionStatus: TextView? = null
    private var mInputEditText: TextView? = null
    private var mName: TextView? = null
    var mConnectedTask: ConnectedTask? = null
    private var mConnectedDeviceName: String? = null
    private var mConversationArrayAdapter: ArrayAdapter<String>? = null
    private var numValue: String? = "sbs"
    private var boolValue: Int? = null
    private val mcontext: Context? = null
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val noAction = intent.getIntExtra("no-action", -1)
            // noAction 값이 0인 경우
            if (noAction != null && noAction == 0) {
                boolValue = noAction
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        val intentNum = intent
        numValue = intentNum.getStringExtra("num")
        boolValue = intentNum.getIntExtra("apiCallResult", -1)
        Log.d("happy", numValue!!)
        Log.d("bool", boolValue.toString())
        if (numValue != null && boolValue != -1) {
            makeApiCall(numValue!!)
        }
        val filter = IntentFilter("kr.ac.duksung.birth.DATA_ACTION")
        registerReceiver(receiver, filter)

        // 알림 설정
        if (boolValue == 1) {
            // 정적 선언
            setupNotificationChannel(this)
        }
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        var isWhiteListing = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(applicationContext.packageName)
        }
        if (!isWhiteListing) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:" + applicationContext.packageName)
            startActivity(intent)
        }

//        if (RealService.serviceIntent==null) {
//            serviceIntent = new Intent(this, RealService.class);
//            startService(serviceIntent);
//        } else {
//            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
//            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
//        }

        // Bluetooth 권한 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            // Bluetooth 활성화 확인
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                showErrorDialog("This device does not support Bluetooth.")
            } else if (!bluetoothAdapter.isEnabled) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
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
        mConnectionStatus = findViewById<View>(R.id.connection_status_textview) as TextView
        mInputEditText = findViewById<View>(R.id.input_string_text) as TextView
        mName = findViewById<View>(R.id.textView2) as TextView
        //        ListView mMessageListview = (ListView) findViewById(R.id.message_listview);


        // SharedPreferences 객체 가져오기
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Editor 객체 생성
        val editor = sharedPreferences.edit()

        // "num" 키에 해당하는 값을 저장
        editor.putString("num", numValue.toString())

        // 변경사항 적용
        editor.apply()

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
        mConversationArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1
        )
        //        mMessageListview.setAdapter(mConversationArrayAdapter);

        // 권한 처리를 계속 해줘야함. 메서드마다 *************
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가
//            startBluetoothService();
        }
        Log.d(TAG, "Initalizing Bluetooth adapter...")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            showErrorDialog("This device is not implement Bluetooth.")
            return
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE)
        } else {
            Log.d(TAG, "Initialisation successful.")
            showPairedDevicesListDialog()
        }
        Log.d(TAG, "numValue in onCreate: $numValue")
    }

    private fun startBluetoothService() {
        Log.d(TAG, "numValue in startBluetoothService: $numValue")

//        String deviceAddress = "YOUR_DEVICE_ADDRESS";  // 연결할 Bluetooth 장치의 주소를 설정하세요.
//        Intent serviceIntent = new Intent(this, TestService.class);
//        serviceIntent.putExtra("device_address", deviceAddress);
//        ContextCompat.startForegroundService(this, serviceIntent);

//        Intent intent = new Intent(getApplicationContext(), BluetoothService.class); // 실행시키고픈 서비스클래스 이름
//        intent.putExtra("command", numValue); // 필요시 인텐트에 필요한 데이터를 담아준다
//        startService(intent); // 서비스 실행!
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            } else {
                // 권한이 거부된 경우 사용자에게 알림 표시 또는 다른 조치 수행
            }
        }
    }

    // 주기적으로 메시지를 보내기 위한 Handler
    private val mHandler = Handler()
    private val mSendRunnable: Runnable = object : Runnable {
        override fun run() {
            // boolValue가 1일 때만 메시지를 보냅니다.
            sendMessage(boolValue)
            Log.d("boolValue", boolValue.toString())

            // 일정한 간격 이후에 다음 메시지를 보낼 수 있도록 예약
            mHandler.postDelayed(this, MESSAGE_SEND_INTERVAL.toLong())
        }
    }

    public override fun onResume() {
        super.onResume()

        // 액티비티가 다시 시작될 때 메시지를 주기적으로 보내기 시작
        mHandler.postDelayed(mSendRunnable, MESSAGE_SEND_INTERVAL.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceIntent != null) {
            stopService(serviceIntent)
            serviceIntent = null
        }
        if (mConnectedTask != null) {
            mConnectedTask!!.cancel(true)
        }
    }

    private fun makeApiCall(serialNumber: String) {
        val apiService = retrofitInstance?.create(NumApiService::class.java)
        val call = apiService?.getBySerial(serialNumber)

        call?.enqueue(object : Callback<Serial?> {
            override fun onResponse(call: Call<Serial?>, response: Response<Serial?>) {
                if (response.isSuccessful) {
                    val serial = response.body()
                    if (serial != null) {
                        val name = serial.name
                        val expireDate = serial.expireDate
                        runOnUiThread {
                            if (expireDate != null) {
                                if (boolValue == 1) {
                                    mName?.text = "$name 임산부님 환영합니다."
                                    mInputEditText?.append(expireDate.toString())
                                } else {
                                    mName?.text = "임산부 시리얼 넘버 만료 기간이 종료되었습니다."
                                    mInputEditText?.append(expireDate.toString())
                                    Toast.makeText(
                                        this@BluetoothActivity,
                                        "임산부 인증에 실패하였습니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                mName?.text = "임산부가 아닙니다."
                                mInputEditText?.text = ""
                                Log.e("Error", "expireDate is null")
                            }
                        }
                    } else {
                        runOnUiThread {
                            mName?.text = "임산부가 아닙니다."
                            mInputEditText?.text = ""
                            Log.e("Error", "Response body is null")
                            Toast.makeText(
                                this@BluetoothActivity,
                                "임산부 인증에 실패하였습니다.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    // Handle the case where the server response is not successful
                    Log.e("Retrofit Error", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Serial?>, t: Throwable) {
                // Handle the case where the network request fails
                Log.e("Retrofit Error", "Failure: ${t.message}")
            }
        })
    }


    private inner class ConnectTask internal constructor(bluetoothDevice: BluetoothDevice) :
        AsyncTask<Void, Void, Boolean>() {
        private var mBluetoothSocket: BluetoothSocket? = null
        private var mBluetoothDevice: BluetoothDevice? = null

        init {
            mBluetoothDevice = bluetoothDevice
            if (ContextCompat.checkSelfPermission(
                    this@BluetoothActivity,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(
                    this@BluetoothActivity,
                    arrayOf(Manifest.permission.BLUETOOTH),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            } else {
                // 권한이 이미 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            }
            mConnectedDeviceName = bluetoothDevice.name

            // SPP
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            try {
                mBluetoothSocket = mBluetoothDevice?.createRfcommSocketToServiceRecord(uuid)
                Log.d(TAG, "create socket for $mConnectedDeviceName")
            } catch (e: IOException) {
                Log.e(TAG, "socket create failed " + e.message)
            }
            mConnectionStatus!!.text = "connecting..."
        }

        override fun doInBackground(vararg params: Void): Boolean {
            Log.d(TAG, "numValue in ConnectTask doInBackground: $numValue")
            if (ContextCompat.checkSelfPermission(
                    this@BluetoothActivity,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(
                    this@BluetoothActivity,
                    arrayOf(Manifest.permission.BLUETOOTH),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            } else {
                // 권한이 이미 허용된 경우 블루투스 작업 수행
                // 여기에 블루투스 관련 코드 추가
            }
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter?.cancelDiscovery()

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket?.connect()
            } catch (e: IOException) {
                // Close the socket
                try {
                    mBluetoothSocket?.close()
                } catch (e2: IOException) {
                    Log.e(
                        TAG, "unable to close() " +
                                " socket during connection failure", e2
                    )
                }
                return false
            }
            return true
        }

        override fun onPostExecute(isSuccess: Boolean) {
            if (isSuccess) {
                connected(mBluetoothSocket)

                // 호출 조건 추가
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 이제 boolValue가 1인 경우에만 알람 매니저 설정
                    Log.d("alarmManagerUtil", "alarmStart")
                    setRepeatingAlarm(applicationContext)
                }
                checkNotificationPermission()
            } else {
                isConnectionError = true
                Log.d(TAG, "Unable to connect device")
                showErrorDialog("Unable to connect device")
            }
        }
    }


    fun connected(socket: BluetoothSocket?) {
        mConnectedTask = ConnectedTask(socket)
        mConnectedTask!!.execute()

        // 연결이 성립되면 자동으로 메시지 전송 - sendButton 대신 실행
        val sendMessage = boolValue
        Log.d("sbs", sendMessage.toString())
        if (sendMessage != null) {
            Log.d("write", sendMessage.toString())
            mConnectedTask!!.write(sendMessage)
        }
    }

    inner class ConnectedTask internal constructor(socket: BluetoothSocket?) :
        AsyncTask<Void?, String?, Boolean?>() {
        private var mInputStream: InputStream? = null
        private var mOutputStream: OutputStream? = null
        private var mBluetoothSocket: BluetoothSocket? = null

        init {
            mBluetoothSocket = socket
            try {
                mInputStream = mBluetoothSocket!!.inputStream
                mOutputStream = mBluetoothSocket!!.outputStream
            } catch (e: IOException) {
                Log.e(TAG, "socket not created", e)
            }
            Log.d(TAG, "connected to $mConnectedDeviceName")
            mConnectionStatus!!.text = "connected to $mConnectedDeviceName"
        }

        override fun doInBackground(vararg params: Void?): Boolean {
            Log.d(TAG, "numValue in ConnectedTask doInBackground: $numValue")
            val readBuffer = ByteArray(1024)
            var readBufferPosition = 0
            while (true) {
                if (isCancelled) return false
                try {
                    val bytesAvailable = mInputStream!!.available()
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        mInputStream!!.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b == '\n'.code.toByte()) {
                                val encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(
                                    readBuffer, 0, encodedBytes, 0,
                                    encodedBytes.size
                                )
                                val recvMessage = String(encodedBytes, Charsets.UTF_8)
                                readBufferPosition = 0
                                Log.d(TAG, "recv message: $recvMessage")
                                publishProgress(recvMessage)
                            } else {
                                readBuffer[readBufferPosition++] = b
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "disconnected", e)
                    return false
                }
            }
        }

        override fun onProgressUpdate(vararg recvMessage: String?) {
            mConversationArrayAdapter!!.insert(mConnectedDeviceName + ": " + recvMessage[0], 0)
        }

        override fun onPostExecute(isSuccess: Boolean?) {
            super.onPostExecute(isSuccess)
            if (isSuccess != null && !isSuccess) {
                closeSocket()
                Log.d(TAG, "Device connection was lost")
                isConnectionError = true
                showErrorDialog("Device connection was lost")
            }
        }

        override fun onCancelled(aBoolean: Boolean?) {
            super.onCancelled(aBoolean)
            closeSocket()
        }

        fun closeSocket() {
            try {
                mBluetoothSocket!!.close()
                Log.d(TAG, "close socket()")
            } catch (e2: IOException) {
                Log.e(
                    TAG, "unable to close() " +
                            " socket during connection failure", e2
                )
            }
        }

        //int 값을 표현하는 바이트를 전송하는 문제를 ByteBuffer을 사용해 해결
        fun write(msg: Int?) {
            try {
                // ByteBuffer를 생성하고 int 값을 해당 ByteBuffer에 기록
                val buffer = ByteBuffer.allocate(Integer.BYTES)
                buffer.putInt(msg!!)

                // ByteBuffer의 내용을 바이트 배열로 가져와 전송
                mOutputStream!!.write(buffer.array())
                mOutputStream!!.flush()
                Log.d(TAG, "Sent message: $msg")
            } catch (e: IOException) {
                Log.e(TAG, "Exception during send", e)
            }
        }
    }


    fun showPairedDevicesListDialog() {
        if (ContextCompat.checkSelfPermission(
                this@BluetoothActivity,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this@BluetoothActivity,
                arrayOf(Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            // 권한이 이미 허용된 경우 블루투스 작업 수행
            // 여기에 블루투스 관련 코드 추가
        }
        val devices = mBluetoothAdapter!!.bondedDevices
        val pairedDevices = devices.toTypedArray()
        if (pairedDevices.size == 0) {
            showQuitDialog(
                """
    No devices have been paired.
    You must pair it with another device.
    """.trimIndent()
            )
            return
        }
        val items: Array<String?>
        items = arrayOfNulls(pairedDevices.size)
        for (i in pairedDevices.indices) {
            items[i] = pairedDevices[i].name
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("앉은 자리를 선택하세요")
        builder.setCancelable(false)
        builder.setItems(items) { dialog, which ->
            dialog.dismiss()
            val task: ConnectTask = ConnectTask(
                pairedDevices[which]
            )
            task.execute()
        }
        builder.create().show()
    }

    fun showErrorDialog(message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quit")
        builder.setCancelable(false)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            if (isConnectionError) {
                isConnectionError = false
                finish()
            }
        }
        builder.create().show()
    }

    fun showQuitDialog(message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quit")
        builder.setCancelable(false)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.create().show()
    }

    fun sendMessage(msg: Int?) {
        if (mConnectedTask != null) {
            mConnectedTask!!.write(msg)
            Log.d(TAG, "send message: $msg")
            mConversationArrayAdapter!!.insert("Me:  $msg", 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                //BlueTooth is now Enabled
//                showPairedDevicesListDialog();
            }
            if (resultCode == RESULT_CANCELED) {
                showQuitDialog("You need to enable bluetooth")
            }
        }
    }

    private fun checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없을 경우 요청
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                CheckAlarmReceiver.REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1
        private const val REQUEST_BLUETOOTH_PERMISSION = 101
        var mBluetoothAdapter: BluetoothAdapter? = null
        var isConnectionError = false
        private const val TAG = "BluetoothClient"
        private const val BASE_URL = "http://192.168.0.21:8080"
        private var retrofit: Retrofit? = null
        val retrofitInstance: Retrofit?
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit
            }
        private const val MESSAGE_SEND_INTERVAL = 1000 // 5 초
    }
}