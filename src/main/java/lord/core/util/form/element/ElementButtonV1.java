package lord.core.util.form.element;

import cn.nukkit.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lord.core.util.form.util.ImageData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
public final class ElementButtonV1 {

   // type = button
    
    private final String text;
    
    private final ImageData image;
    
    @JsonIgnore @Nullable
    private Consumer<Player> listener;

    public ElementButtonV1 (String text) {
        this.text = text;
        this.image = new ImageData();
    }

    public ElementButtonV1 (String text, String imgType, String imgData) {
        this.text = text;
        this.image = new ImageData(imgType, imgData);
    }
}
