package com.misty.engine.graphics.UI;

public interface Clickable {

	public boolean isPressed();
	
	boolean onClickPressed(int x, int y);
	
	boolean onClickReleased(int x, int y);

	public void onclickReleasedOutside();

}
