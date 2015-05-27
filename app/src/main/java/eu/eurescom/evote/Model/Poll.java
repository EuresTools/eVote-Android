package eu.eurescom.evote.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by trainee on 27.5.2015.
 */
public class Poll implements Parcelable {

    private String query;
    private ArrayList<String> options;
    private int select;
    private boolean allow_fewer;

    public Poll(JsonObject json) {
        query = json.get("query").getAsString();
        JsonArray json_options = json.getAsJsonArray("options");
        options = new ArrayList<String>();
        for(JsonElement json_option : json_options) {
            options.add(json_option.getAsString());
        }
        select = json.get("select").getAsInt();
        allow_fewer = json.get("allow_fewer").getAsBoolean();
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public boolean isAllow_fewer() {
        return allow_fewer;
    }

    public void setAllow_fewer(boolean allow_fewer) {
        this.allow_fewer = allow_fewer;
    }


    /* Generated code to implement parcelable */

    protected Poll(Parcel in) {
        query = in.readString();
        if (in.readByte() == 0x01) {
            options = new ArrayList<String>();
            in.readList(options, String.class.getClassLoader());
        } else {
            options = null;
        }
        select = in.readInt();
        allow_fewer = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        if (options == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(options);
        }
        dest.writeInt(select);
        dest.writeByte((byte) (allow_fewer ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Poll> CREATOR = new Parcelable.Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel in) {
            return new Poll(in);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
