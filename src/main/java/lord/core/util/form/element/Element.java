package lord.core.util.form.element;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Элемент формы
 */
@Getter @AllArgsConstructor
public abstract class Element {
    
    /** Тип элемента */
    private final String type;
    
    /** Текст элемента */
    private String text;
    
}
