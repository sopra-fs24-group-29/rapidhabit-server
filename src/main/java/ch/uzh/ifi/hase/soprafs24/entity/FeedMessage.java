package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

public class FeedMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msg;

    public FeedMessage(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return this.msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "FeedMessage{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
