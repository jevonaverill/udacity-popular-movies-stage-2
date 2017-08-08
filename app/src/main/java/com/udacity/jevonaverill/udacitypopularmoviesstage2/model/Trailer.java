package com.udacity.jevonaverill.udacitypopularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class Trailer implements Parcelable {

    public String title;
    public String url;

    public Trailer() {}

    public Trailer(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    private Trailer(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    static String arrayToString(ArrayList<Trailer> trailers){
        String result = "";
        try {
            for (int i = 0; i < trailers.size(); i++) {
                result += trailers.get(i).title + "," + trailers.get(i).url;
                if (i < trailers.size() - 1) {
                    result += " -trailerSeparator- ";
                }
            }
        } catch (NullPointerException e){
            return "";
        }
        return result;
    }

    public static ArrayList<Trailer> stringToArray(String string){
        String[] elements = string.split(" -trailerSeparator- ");

        ArrayList<Trailer> result = new ArrayList<>();

        for (String element : elements) {
            try {
                String[] item = element.split(",");
                result.add(new Trailer(item[0], item[1]));
            } catch (IndexOutOfBoundsException e) {
                Log.d("Trailers", e.toString());
            }
        }

        return result;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };

}
