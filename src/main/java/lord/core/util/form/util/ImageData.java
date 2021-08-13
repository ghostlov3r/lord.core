package lord.core.util.form.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;

/**
 * Изображение для элемента формы
 */
@JsonSerialize(using = ImageData.ImageDataSerializer.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Accessors(fluent = true)
public final class ImageData {
    
    /** Тип изображения */
    private String type = null;
    
    /** Путь к изображению */
    private String data = null;

    static final class ImageDataSerializer extends JsonSerializer<ImageData> {

        @Override
        public void serialize(ImageData imageData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (imageData.data == null || imageData.data.isEmpty() || imageData.type == null) {
                jsonGenerator.writeNull();
                return;
            }

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("type", imageData.type);
            jsonGenerator.writeStringField("data", imageData.data);
            jsonGenerator.writeEndObject();
        }
    }

}