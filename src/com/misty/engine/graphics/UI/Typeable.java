package com.misty.engine.graphics.UI;

import java.awt.event.KeyEvent;

public interface Typeable {
	public void onKey(KeyEvent e);

	public boolean hasFocus();
}
