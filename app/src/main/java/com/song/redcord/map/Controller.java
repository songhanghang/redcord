package com.song.redcord.map;

import com.song.redcord.interfaces.LoverRefresh;

abstract class Controller implements LoverRefresh {
    final LoverRefresh loverRefresh;

    public Controller() {
        this.loverRefresh = null;
    }

    public Controller(LoverRefresh loverRefresh) {
        this.loverRefresh = loverRefresh;
    }

}
