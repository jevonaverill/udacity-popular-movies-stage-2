package com.udacity.jevonaverill.udacitypopularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class Review implements Parcelable {

    public String author;
    public String content;

    public Review() {}

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
    }

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static String arrayToString(ArrayList<Review> reviews){
        String result = "";
        try {
            for (int i = 0; i < reviews.size(); i++) {
                result += reviews.get(i).author + ",reviewSeparator," + reviews.get(i).content;
                if (i < reviews.size() - 1) {
                    result += " -reviewSeparator- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return result;
    }

    public static ArrayList<Review> stringToArray(String string){
        String[] elements = string.split(" -reviewSeparator- ");
        ArrayList<Review> result = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",reviewSeparator,");
            try{
                result.add(new Review(item[0], item[1]));
            }catch (IndexOutOfBoundsException e){
                Log.d("Reviews",e.toString());
            }
        }
        return result;
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

}
