// IMyUserManager.aidl
package com.example.administrator.ipc_test;
import com.example.administrator.ipc_test.MyUser;//手动导入MyUser类

interface IMyUserManager {
     void addUser(in MyUser user);
     void removeUser(in MyUser user);
     int getUserNum();
}
