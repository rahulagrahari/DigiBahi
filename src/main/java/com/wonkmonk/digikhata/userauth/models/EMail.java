package com.wonkmonk.digikhata.userauth.models;

import java.util.Map;

public class EMail {

    String to;
    String from;
    String subject;
    String content;

    public EMail(String to, String from, String subject, String content) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

}
