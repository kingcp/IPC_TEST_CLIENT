package com.example.administrator.ipc_test;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/8/15.
 */
public class MyUser implements Parcelable {

    private String mUserName;
    private int mUserAge;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserName);
        dest.writeInt(this.mUserAge);
    }

    public MyUser() {
    }

    protected MyUser(Parcel in) {
        this.mUserName = in.readString();
        this.mUserAge = in.readInt();
    }

    public static final Creator<MyUser> CREATOR = new Creator<MyUser>() {
        @Override
        public MyUser createFromParcel(Parcel source) {
            return new MyUser(source);
        }

        @Override
        public MyUser[] newArray(int size) {
            return new MyUser[size];
        }
    };
}
