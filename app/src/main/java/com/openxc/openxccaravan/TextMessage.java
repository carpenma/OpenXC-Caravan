package com.openxc.openxccaravan;

/**
 * Created by MCARPE53 on 11/15/2016.
 */

public class TextMessage {
    public String body;
    public String sender;
    public long time;

    public TextMessage(String body, String sender, long time) {
        this.body = body;
        this.sender = sender;
        this.time = time;
    }
}
