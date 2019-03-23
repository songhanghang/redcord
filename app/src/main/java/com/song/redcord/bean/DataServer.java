package com.song.redcord.bean;

public interface DataServer {
    void pull(Callback callback);
    void push(Callback callback);
}
