package lord.core.util.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Выпадающий список формы
 */
@Getter
public final class ElementDropdown extends Element {
    
    private final static String TYPE = "dropdown";

    /** Элементы выпадающего списка */
    private final List<String> options = new ArrayList<>();
    
    /** Номер опции по умолчанию */
    @JsonProperty("default")
    private int defaultOptionIndex = 0;

    public ElementDropdown (String text) {
        super(TYPE, text);
    }

    public ElementDropdown (String text, List<String> options) {
        super(TYPE, text);
        
        this.options.addAll(options);
    }
    
    public ElementDropdown (String text, List<String> options, int defaultOpt) {
        super(TYPE, text);

        this.options.addAll(options);
        this.defaultOptionIndex = defaultOpt;
    }
    
    public String getOption (int index) {
        return this.options.get(index);
    }
    
    
    @Getter @RequiredArgsConstructor
    public static class Response {

        /** Номер опции */
        private final int optIndex;
        
        /** Текст опции */
        private final String option;
        
    }
}