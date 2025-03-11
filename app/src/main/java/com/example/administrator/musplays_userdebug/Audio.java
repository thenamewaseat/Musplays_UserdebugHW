package com.example.administrator.musplays_userdebug;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Audio implements Parcelable {
    private final Uri uri;
    private final String name;
    private final int duration;
    private final String data;
    private final String title;

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public Uri getUri(){return uri;}
    public String getName(){
        return name;
    }
    public int getDuration(){
        return duration;
    }
    public String getData(){
        return data;
    }
    public String geStitle(){
        return title;
    }
    public Audio(Uri uri, String name,int duration,String data,String title){
        this.uri=uri;
        this.name=name;
        this.duration=duration;
        this.data=data;
        this.title=title;

    }
    public Audio(Parcel in){

        uri = in.readParcelable(Uri.class.getClassLoader());
        name = in.readString();
        duration = in.readInt();
        data = in.readString();
        title=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(name);
        dest.writeInt(duration);
        dest.writeString(data);
        dest.writeString(title);
    }
}
