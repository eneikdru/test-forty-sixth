package com.eneik.generated.material;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@org.springframework.test.context.ContextConfiguration(classes = com.eneik.generated.Application.class)
class MaterialRepositoryTest {

    @Autowired
    private MaterialRepository materialRepository;

    @Test
    void testSaveAndRetrieveMaterialDraftSuccess() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        String title = "Epidemiological Protocol A";
        String content = "Detailed protocol instructions for outbreak tracing.";
        String metadata = "{\"category\": \"protocol\", \"version\": 1}";

        Material draft = new Material(id, title, content, metadata);
        materialRepository.saveAndFlush(draft);

        Optional<Material> retrieved = materialRepository.findById(id);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(id);
        assertThat(retrieved.get().getTitle()).isEqualTo(title);
        assertThat(retrieved.get().getContent()).isEqualTo(content);
        assertThat(retrieved.get().getMetadata()).isEqualTo(metadata);
    }

    @Test
    void testSaveMissingMandatoryTitleRaisesConstraintViolation() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        Material invalidMaterial = new Material(id, null, "Content without title", "{}");

        assertThatThrownBy(() -> materialRepository.saveAndFlush(invalidMaterial))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void testSaveMissingMandatoryContentRaisesConstraintViolation() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000003");
        Material invalidMaterial = new Material(id, "Title without content", null, "{}");

        assertThatThrownBy(() -> materialRepository.saveAndFlush(invalidMaterial))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void testSaveMissingMandatoryIdRaisesException() {
        Material invalidMaterial = new Material(null, "Title", "Content", "{}");

        assertThatThrownBy(() -> materialRepository.saveAndFlush(invalidMaterial))
                .isInstanceOf(Exception.class);
    }
}
