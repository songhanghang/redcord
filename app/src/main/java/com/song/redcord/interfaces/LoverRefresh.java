package com.song.redcord.interfaces;

import com.song.redcord.bean.Lover;

public interface LoverRefresh<T extends Lover> {
    public void refresh(T t);
}
