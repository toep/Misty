import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.TextField;

public class TopBar extends Group {

	private int score_int = 0;
	private Label score;
	
	private TextField tf;
	public TopBar() {
		super(0, -24, (int) Game.getCurrent().getWidth(), 24);
		
		setbackgroundColor(0xffeeeeee);
		score = new Label("Score:"+score_int, 0, 0);
		tf = new TextField("test", 0, 12);
		add(score);
		add(tf);
	}
	
	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, Color.create(0xffaaaaaa));
		super.draw(r);
	}
	

	public void setScore(int newScore){
		this.score_int = newScore;
		this.score.setText("Score:"+score_int);
	}

	public void incScore() {
		setScore(score_int+50);
	}
	
}
