package com.feifan.planlib.entity;

/**
 * Created by xuchunlei on 16/10/18.
 */

public class AutoPoint extends LayerPoint {

    public AutoPoint() {
        setMovable(false);
    }
    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        updateRaw();
    }

    @Override
    public void setReal(float x, float y) {
        super.setReal(x, y);
        updateRaw();
    }

    private void updateRaw() {
        setRaw(getRealX() / getScale(), getRealY() / getScale());
    }
}
