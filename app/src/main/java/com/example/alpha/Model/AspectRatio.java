package com.example.alpha.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class AspectRatio implements Parcelable {

    public static final Creator<com.example.alpha.Model.AspectRatio> CREATOR = new Creator<com.example.alpha.Model.AspectRatio>() {
        @Override
        public com.example.alpha.Model.AspectRatio createFromParcel(Parcel in) {
            return new com.example.alpha.Model.AspectRatio(in);
        }

        @Override
        public AspectRatio[] newArray(int size) {
            return new AspectRatio[size];
        }
    };
    @Nullable
    private final String mAspectRatioTitle;
    private final float mAspectRatioX;
    private final float mAspectRatioY;

    public AspectRatio(@Nullable String aspectRatioTitle, float aspectRatioX, float aspectRatioY) {
        mAspectRatioTitle = aspectRatioTitle;
        mAspectRatioX = aspectRatioX;
        mAspectRatioY = aspectRatioY;
    }

    protected AspectRatio(Parcel in) {
        mAspectRatioTitle = in.readString();
        mAspectRatioX = in.readFloat();
        mAspectRatioY = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAspectRatioTitle);
        dest.writeFloat(mAspectRatioX);
        dest.writeFloat(mAspectRatioY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    public String getAspectRatioTitle() {
        return mAspectRatioTitle;
    }

    public float getAspectRatioX() {
        return mAspectRatioX;
    }

    public float getAspectRatioY() {
        return mAspectRatioY;
    }

}
