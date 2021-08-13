package lord.core.form.element;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.util.form.util.ImageData;
import lord.core.gamer.Gamer;

@Getter @Accessors(fluent = true) @Setter
public abstract class ElementButton {

   // type = button
    
    private String text;
    
    private ImageData image;
    
    public abstract void onClick (Gamer gamer);

    public ElementButton (String text) {
        this.text = text;
        this.image = new ImageData();
    }

    public ElementButton (String text, String imgType, String imgData) {
        this.text = text;
        this.image = new ImageData(imgType, imgData);
    }
    
    public void image (String type, String data) {
        this.image.data(data);
        this.image.type(type);
    }
}
