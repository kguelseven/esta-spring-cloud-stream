package ch.sbb.esta.scs.converter;

import ch.sbb.esta.scs.book.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import javax.annotation.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

public class BookXmlConverter extends AbstractMessageConverter {

    private final XmlMapper xmlMapper;

    public BookXmlConverter() {
        super(MimeType.valueOf("application/xml"));
        xmlMapper = new XmlMapper();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.equals(Book.class);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
        try {
            return xmlMapper.readValue((byte[]) message.getPayload(), Book.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        try {
            return xmlMapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
