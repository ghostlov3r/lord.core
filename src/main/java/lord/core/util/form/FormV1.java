package lord.core.util.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lord.core.gamer.Gamer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class FormV1<Resp, F extends FormV1> {

    /** Тип формы */
    private final String type;
    
    /** Заголовок */
    private String title = "";

    /** При отправке игроком */
    @JsonIgnore
    private BiConsumer<Gamer, Resp> onSubmit;
    
    /** При закрытии игроком */
    @JsonIgnore
    private Consumer<Gamer> onClose;
    
    /** При ошибке */
    @JsonIgnore
    private Consumer<Gamer> onError;
    
    public FormV1 (String type) {
        this.type = type;
    }

    public abstract void handleResponse(Gamer p, JsonNode node);
    
    protected abstract F self ();

    protected void close (Gamer player) {
        if (onClose != null) {
            onClose.accept(player);
        }
    }

    protected void submit (Gamer player, Resp response) {
        if (response == null) {
            close(player);
            return;
        }
        if (onSubmit != null) {
            onSubmit.accept(player, response);
        }
    }
    
    protected void error (Gamer player) {
        if (onError != null) {
            onError.accept(player);
        }
    }
    
    public F title (String title) {
        this.title = title;
        return self();
    }
    
    public F onSubmit (BiConsumer<Gamer, Resp> listener) {
        this.onSubmit = listener;
        return self();
    }
    
    public F onClose (Consumer<Gamer> listener) {
        this.onClose = listener;
        return self();
    }
    
    public F onError (Consumer<Gamer> listener) {
        this.onError = listener;
        return self();
    }
    
    /**
     * @return Новая пустая простая форма
     */
    public static SimpleFormV1 simple () {
        return new SimpleFormV1();
    }
    
    /**
     * @return Новая пустая модальная форма
     */
    public static ModalFormV1 modal () {
        return new ModalFormV1();
    }
    
    /**
     * @return Новая пустая кастомная форма
     */
    public static CustomFormV1 custom () {
        return new CustomFormV1();
    }
    
}
