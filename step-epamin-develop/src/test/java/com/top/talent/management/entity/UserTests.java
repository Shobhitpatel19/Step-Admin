package com.top.talent.management.entity;

import com.top.talent.management.utils.TestUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserTests {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testUserEntity() {
        Role savedRole = entityManager.persistAndFlush(TestUtils.getRole());
        User savedUser = entityManager.persistAndFlush(TestUtils.getUser(savedRole));

        assertThat(savedUser.getUuid()).isEqualTo(1L);
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getRole().getName()).isEqualTo("P");
    }

    @Test
    void testUserUniqueEmailConstraint() {
        Role role = entityManager.persistAndFlush(TestUtils.getRole());
        User user = TestUtils.getUser(role);

        entityManager.persistAndFlush(TestUtils.getUser(role));
        assertThrows(EntityExistsException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }

    @Test
    void testUserNonNullableFields() {
        User user = new User();
        user.setUuid(1L);
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }
}
