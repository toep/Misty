import java.awt.event.MouseWheelEvent;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;
import com.misty.utils.Util;

public class OvalTest extends Game {

	float scl = 25;
	public static void main(String[] args) {
		new OvalTest("Oval test", 512, 512).start();
	}
	public OvalTest(String name, int width, int height) {
		super(name, width, height, 1);
	}

	@Override
	public void setup() {
		setClearColor(Color.BLACK);
	}

	@Override
	public void draw(Renderer g) {
		g.fillColoredOval(mouseX-(int)(.5f*scl), mouseY-(int)(.5f*scl), (int)scl, (int)scl, Color.BLUE);
		g.fillColoredOval(75, 20, 40, 75, Color.RED);
		g.fillColoredOval(220, 175, 250, 40, Color.LIME);

		g.fillColoredOval(-120, 120, (int)(100+300*Util.sin(tick*5)), (int)(250+100*Util.cos(tick*5)), Color.LIME);

	}
	
	@Override
	public void mouseWheelMoved(int wheelRotation) {
		super.mouseWheelMoved(wheelRotation);
		scl+=wheelRotation;

	}

	@Override
	public void update() {
	}

}
