package eu.eurescom.evote.Model;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trainee on 27.5.2015.
 */
public class Poll implements Serializable {// implements Parcelable {

    private String title;
    private String question;
    private int select_min;
    private int select_max;
    private ArrayList<Option> options;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getSelectMin() {
        return select_min;
    }

    public void setSelectMin(int select_min) {
        this.select_min = select_min;
    }

    public int getSelectMax() {
        return select_max;
    }

    public void setSelectMax(int select_max) {
        this.select_max = select_max;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }
}
