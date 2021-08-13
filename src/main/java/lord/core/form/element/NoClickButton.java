package lord.core.form.element;

import lord.core.gamer.Gamer;

public class NoClickButton extends ElementButton {
	
	public NoClickButton (String text) {
		super(text);
	}
	
	public NoClickButton (String text, String imgType, String imgData) {
		super(text, imgType, imgData);
	}
	
	@Override
	public void onClick (Gamer gamer) {
	
	}
	
}
