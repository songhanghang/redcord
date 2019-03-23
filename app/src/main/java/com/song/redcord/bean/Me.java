package com.song.redcord.bean;

import android.location.Location;
import com.song.redcord.map.Maper;

public class Me extends Lover {

    private final Maper maper;

    public Me(Maper maper) {
        this.maper = maper;
        this.loveId = "lover";
    }

    private Lover you = new Lover() {
        {
            setLocation(33.789925, 104.838326);
        }
    };

    @Override
    public void pull(final Callback callback) {
        Callback wrap = new Callback() {
            @Override
            public void onCall() {
                you.id = loveId;
                pullYou();

                if (callback != null)
                    callback.onCall();
            }
        };
        super.pull(wrap);
    }

    public void update(Location location) {
        setLocation(location.getLatitude(), location.getLongitude());
        push(null);
        pullYou();
    }

    private void pullYou() {
        if (isSingle()) {
            return;
        }

        you.pull(new Callback() {
            @Override
            public void onCall() {
                refreshMap();
            }
        });
    }

    private void refreshMap() {
        if (isSingle()) {
            return;
        }
        maper.refresh(location, you.location);
    }
}
