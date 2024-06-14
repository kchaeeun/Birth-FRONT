package kr.ac.duksung.birth;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mList;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 101;

    public CustomAdapter(Context context, BluetoothDevice[] list) {
        super(context, R.layout.custom_list_item); // 부모 클래스의 생성자 수정
        this.mContext = context;

        // BluetoothDevice 배열에서 이름 추출하여 String 배열로 변환
        mList = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            }
            mList[i] = list[i].getName();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItemView = inflater.inflate(R.layout.custom_list_item, parent, false);
        }

        // 리스트 아이템 내용 설정
        TextView textView = listItemView.findViewById(R.id.textView);
        textView.setText(mList[position]);

        return listItemView;
    }
}
