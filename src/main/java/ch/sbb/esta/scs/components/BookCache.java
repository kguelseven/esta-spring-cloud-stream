package ch.sbb.esta.scs.components;

import ch.sbb.esta.scs.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookCache {

    private static final Logger LOG = LoggerFactory.getLogger(BookCache.class);

    private final ConcurrentHashMap<Long, Book> booksById = new ConcurrentHashMap<>();

    public void saveBook(final Book book) {
        LOG.info("STEP 4a: Got book: {}", book);
        booksById.put(book.getId(), book);
    }

    public Book findBookById(final Long id) {
        return booksById.get(id);
    }
}
