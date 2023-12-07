package com.github.bondarevv23.memorizer.config;

import com.github.bondarevv23.memorizer.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.SideRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.repository.jdbc.CardRepositoryJdbc;
import com.github.bondarevv23.memorizer.repository.jdbc.DeckRepositoryJdbc;
import com.github.bondarevv23.memorizer.repository.jdbc.SideRepositoryJdbc;
import com.github.bondarevv23.memorizer.repository.jdbc.UserRepositoryJdbc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    @Primary
    public CardRepository jdbcCardRepository(
            JdbcTemplate jdbc,
            SideRepository sideRepository,
            DeckRepository deckRepository) {
        return new CardRepositoryJdbc(jdbc, sideRepository, deckRepository);
    }

    @Bean
    @Primary
    public DeckRepository jdbcDeckRepositoryJdbc(JdbcTemplate jdbc) {
        return new DeckRepositoryJdbc(jdbc);
    }

    @Bean
    @Primary
    public SideRepository jdbcSideRepositoryJdbc(JdbcTemplate jdbc) {
        return new SideRepositoryJdbc(jdbc);
    }

    @Bean
    @Primary
    public UserRepository jdbcUserRepositoryJdbc(JdbcTemplate jdbc, DeckRepository deckRepository) {
        return new UserRepositoryJdbc(jdbc, deckRepository);
    }
}
