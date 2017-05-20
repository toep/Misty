package com.misty.engine.graphics.UI;

import java.awt.event.KeyEvent;

public interface Typeable {
    boolean onKey(KeyEvent e);

    boolean hasFocus();
}
