package com.github.bondarevv23.memorizer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class User {
    @Id
    private Long tgId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_deck",
            joinColumns = {@JoinColumn(name = "user_tg_id")},
            inverseJoinColumns = {@JoinColumn(name = "deck_id")}
    )
    private List<Deck> decks;
}
