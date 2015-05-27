package eu.eurescom.evote.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by trainee on 27.5.2015.
 */
public class Poll {

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
}
