package lord.core.util.form.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.core.util.form.element.ElementButtonV1;

@Getter @RequiredArgsConstructor
public class SimpleFormResponseV1 {

    private final int clickedId;
    private final ElementButtonV1 button;
    
    /**
     * @return Текст с нажатой кнопки
     */
    public String getText () {
        return button.getText();
    }
    
}
