package com.example.administrator.ipc_test_client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.administrator.ipc_test.IMyUserManager;
import com.example.administrator.ipc_test.MyUser;

public class MainActivity extends AppCompatActivity {
    private IMyUserManager managerBinder = null;
    //连接服务端应用，获得managerBinder
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            managerBinder = IMyUserManager.Stub.asInterface(iBinder);
            try {
                Toast.makeText(MainActivity.this,"size = " + managerBinder.getUserNum(),Toast.LENGTH_LONG).show();
                MyUser user = new MyUser();
                managerBinder.addUser(user);
                Toast.makeText(MainActivity.this,"size = " + managerBinder.getUserNum(),Toast.LENGTH_LONG).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(MainActivity.this,"onServiceDisconnected",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      隐式启动
        Intent intent = new Intent();
        intent.setAction("com.example.administrator.ipc_test.action.MyUserManagerService");
        intent.setPackage("com.example.administrator.ipc_test");//服务端service所在的包名
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }
}
