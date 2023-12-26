package com.github.bondarevv23.memorizer.server.repository;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.Side;
import com.github.bondarevv23.memorizer.server.repository.interfaces.SideRepository;
import com.github.bondarevv23.memorizer.server.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.server.util.TestUtil.getSide;
import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SideRepositoryJdbcTest extends IntegrationEnvironment {

    @Autowired
    private SideRepository repository;

    @Autowired
    private SideRepository sideRepository;

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @Transactional
    @Rollback
    void whenSaveNewSide_thenSuccessfullyAdded(int count) {
        // given
        List<Side> sides = IntStream.range(0, count).mapToObj(TestUtil::getSide).toList();

        // when
        List<Side> addedSides = sides.stream().map(repository::save).toList();

        // then
        List<Side> storedSides = sideRepository.findAll();
        IntStream.range(0, count).forEach(i -> assertThat(storedSides.get(i)).isEqualTo(addedSides.get(i)));
        addedSides.forEach(s -> assertThat(s.getId()).isNotNull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @Transactional
    @Rollback
    void whenUpdateSide_thenSuccessfullyUpdated(int count) {
        // given
        List<Side> sides = IntStream.range(0, count).mapToObj(TestUtil::getSide)
                .map(repository::save).toList();

        // when
        sides.forEach(TestUtil::updateSide);
        List<Side> updatedSides = sides.stream().map(repository::save).toList();

        // then
        List<Side> storedSides = sideRepository.findAll();
        IntStream.range(0, storedSides.size()).forEach(
                i -> assertThat(storedSides.get(i)).isEqualTo(updatedSides.get(i))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 25})
    @Transactional
    @Rollback
    void whenSaveSomeSides_thenFindAllSuccessfully(int count) {
        // given
        List<Side> sides = IntStream.range(0, count).mapToObj(TestUtil::getSide).map(repository::save).toList();

        // when
        List<Side> storedSides = repository.findAll();

        // then
        IntStream.range(0, storedSides.size()).forEach(i -> assertThat(sides.get(i)).isEqualTo(storedSides.get(i)));
    }

    @Test
    @Transactional
    @Rollback
    void whenFindSideByIdWithRightId_thenSuccess() {
        // given
        Side side = repository.save(getSide(1));

        // when
        Optional<Side> storedSide = repository.findSideById(side.getId());

        // then
        assertThat(storedSide).isNotNull();
        assertThat(storedSide.isPresent()).isTrue();
        assertThat(storedSide.get()).isEqualTo(side);
    }

    @Test
    @Transactional
    @Rollback
    void whenFindSideByIdWithWrongId_thenEmptyOptional() {
        // given
        repository.save(getSide(1));

        // when
        Optional<Side> storedSide = repository.findSideById(-1);

        // then
        assertThat(storedSide).isNotNull();
        assertThat(storedSide.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteSideByIdWithRightId_thenDeleteSuccessfully() {
        // given
        Side side = repository.save(getSide(1));

        // when
        repository.deleteSideById(side.getId());

        // then
        Optional<Side> storedSide = sideRepository.findSideById(side.getId());
        assertThat(storedSide).isNotNull();
        assertThat(storedSide.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteSideByIdWithWrongId_thenNothingHappened() {
        // given
        Side side = repository.save(getSide(1));

        // when
        repository.deleteSideById(-1);

        // then
        List<Side> storedSides = sideRepository.findAll();
        assertThat(storedSides.size()).isEqualTo(1);
        assertThat(storedSides.get(0)).isEqualTo(side);
    }
}
