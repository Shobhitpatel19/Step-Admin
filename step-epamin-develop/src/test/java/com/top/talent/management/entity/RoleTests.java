package com.top.talent.management.entity;

import com.top.talent.management.utils.TestUtils;
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
class RoleTests {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testRoleEntity() {
        Role savedRole = entityManager.persistAndFlush(TestUtils.getRole());
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("P");
    }

    @Test
    void testRoleUniqueConstraint() {
        Role role = TestUtils.getRole();
        entityManager.persistAndFlush(TestUtils.getRole());
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(role);
        });
    }

    @Test
    void testRoleNonNullableFields() {
        Role role = new Role();
        role.setName(null);
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(role);
        });
    }

}
