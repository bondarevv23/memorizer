package com.github.bondarevv23.memorizer.server.config;

import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.SideRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.server.repository.jpa.CardRepositoryJpa;
import com.github.bondarevv23.memorizer.server.repository.jpa.DeckRepositoryJpa;
import com.github.bondarevv23.memorizer.server.repository.jpa.SideRepositoryJpa;
import com.github.bondarevv23.memorizer.server.repository.jpa.UserRepositoryJpa;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    @Primary
    public CardRepository jpaCardRepository(CardRepositoryJpa cardRepositoryJpa) {
        return cardRepositoryJpa;
    }

    @Bean
    @Primary
    public DeckRepository jpaDeckRepository(DeckRepositoryJpa deckRepositoryJpa) {
        return deckRepositoryJpa;
    }

    @Bean
    @Primary
    public SideRepository jpaSideRepository(SideRepositoryJpa sideRepositoryJpa) {
        return sideRepositoryJpa;
    }

    @Bean
    @Primary
    public UserRepository jpaUserRepository(UserRepositoryJpa userRepositoryJpa) {
        return userRepositoryJpa;
    }
}
