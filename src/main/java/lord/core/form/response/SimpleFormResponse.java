package lord.core.form.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lord.core.form.element.ElementButton;

@Getter @RequiredArgsConstructor @Accessors(fluent = true)
public class SimpleFormResponse {

    private final int clickedNum;
    private final ElementButton button;
    
    /**
     * @return Текст с нажатой кнопки
     */
    public String getText () {
        return button.text();
    }
    
}
