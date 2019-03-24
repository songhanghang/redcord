package com.song.redcord.map;

import com.song.redcord.bean.Lover;
import com.song.redcord.interfaces.LoverRefresh;

abstract class Controller<T extends Lover> implements LoverRefresh<T> {
    final LoverRefresh loverRefresh;

    public Controller() {
        this.loverRefresh = null;
    }

    public Controller(LoverRefresh loverRefresh) {
        this.loverRefresh = loverRefresh;
    }

}
