package com.github.bondarevv23.memorizer.server.repository.jdbc;

import com.github.bondarevv23.memorizer.server.repository.jdbc.exception.WrongDBStateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

abstract class AbstractJdbcRepository <T> {
    protected static final String ID = "id";
    protected static final String DECK_ID = "deck_id";
    protected static final String QUESTION = "question";
    protected static final String ANSWER = "answer";
    protected static final String TITLE = "title";
    protected static final String OWNER = "owner";
    protected static final String TEXT = "text";
    protected static final String IMAGE_LINK = "image_link";
    protected static final String TG_ID = "tg_id";

    abstract protected Optional<T> parseResultSet(ResultSet rs) throws SQLException;

    protected List<T> resultSetToList(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        while (true) {
            Optional<T> card = parseResultSet(rs);
            if (card.isPresent()) {
                list.add(card.get());
            } else {
                break;
            }
        }
        return list;
    }

    protected T addOrUpdate(int count, UnaryOperator<T> add, UnaryOperator<T> update, T t) {
        if (count == 0) {
            return add.apply(t);
        }
        return update.apply(t);
    }

    <E> E orThrow(Optional<E> optional) {
        Objects.requireNonNull(optional);
        return optional.orElseThrow(WrongDBStateException::new);
    }
}
