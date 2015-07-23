package eu.eurescom.evote.Model;

import java.io.Serializable;

/**
 * Created by trainee on 23.7.2015.
 */
public class Option implements Serializable {

    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
