package kr.ac.duksung.birth;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import kr.ac.duksung.birth.BluetoothActivity;
import kr.ac.duksung.birth.R;

public class CustomDeviceListDialog extends Dialog {
    // CustomDeviceListDialog 클래스 내부에 ConnectTask 클래스 정의
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            // ConnectTask의 생성자 내부에 Bluetooth 연결 코드 작성
            // 여기에 블루투스 관련 코드 추가
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // 백그라운드에서 블루투스 연결 작업 수행
            // 여기에 블루투스 관련 코드 추가
            return null;
        }
    }

    // CustomDeviceListDialog 클래스의 나머지 코드
    private Context mContext;
    private BluetoothDevice[] mPairedDevices;

    public CustomDeviceListDialog(Context context, BluetoothDevice[] pairedDevices) {
        super(context);
        mContext = context;
        mPairedDevices = pairedDevices;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_device_list_dialog_layout);

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<>(mContext, R.layout.custom_list_item, mPairedDevices);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            dismiss();
            // CustomDeviceListDialog 내부에 있는 ConnectTask 생성
            ConnectTask task = new ConnectTask(mPairedDevices[position]);
            task.execute();
        });
    }
}

