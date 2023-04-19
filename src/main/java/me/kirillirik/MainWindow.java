package me.kirillirik;

import me.kirillirik.candidate.Base;

public final class MainWindow {

    private Base base = new Base();

    public void update() {
        if (base != null) {
            base.update();
            if (base.isNeedClose()) {
                base = null;
            } else {
                return;
            }
        }
    }
}
