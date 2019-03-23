package com.song.redcord.bean;

import com.song.redcord.interfaces.RequestCallback;

public interface DataServer {
    void pull(RequestCallback callback);
    void push(RequestCallback callback);
}
