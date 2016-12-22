package com.misty.engine.graphics.UI;

import java.awt.event.KeyEvent;

public interface Typeable {
	public boolean onKey(KeyEvent e);

	public boolean hasFocus();
}
