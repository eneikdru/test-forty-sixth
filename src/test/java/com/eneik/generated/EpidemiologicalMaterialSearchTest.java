package com.eneik.generated;

import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EpidemiologicalMaterialSearchTest {

    @Autowired
    private EpidemiologicalMaterialRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(new EpidemiologicalMaterial(
                "Influenza Surveillance Protocol 2026",
                "VIRUS",
                "Guidance on seasonal influenza monitoring and reporting protocols."
        ));

        repository.save(new EpidemiologicalMaterial(
                "Cholera Outbreak Control Manual",
                "BACTERIA",
                "Comprehensive measures for waterborne cholera containment."
        ));

        repository.save(new EpidemiologicalMaterial(
                "Ebola Hemorrhagic Fever Response",
                "VIRUS",
                "Emergency response plan and quarantine guidelines for viral outbreaks."
        ));

        repository.save(new EpidemiologicalMaterial(
                "Malaria Prevention and Vector Control",
                "PARASITE",
                "Strategies for mosquito control and parasite containment."
        ));
    }

    @Test
    void testFilterByPathogenType() {
        List<EpidemiologicalMaterial> viruses = repository.findByPathogenType("VIRUS");
        assertThat(viruses).hasSize(2);
        assertThat(viruses).extracting(EpidemiologicalMaterial::getTitle)
                .containsExactlyInAnyOrder(
                        "Influenza Surveillance Protocol 2026",
                        "Ebola Hemorrhagic Fever Response"
                );

        List<EpidemiologicalMaterial> bacteria = repository.findByPathogenType("BACTERIA");
        assertThat(bacteria).hasSize(1);
        assertThat(bacteria.get(0).getTitle()).isEqualTo("Cholera Outbreak Control Manual");
    }

    @Test
    void testFullTextKeywordSearch() {
        List<EpidemiologicalMaterial> outbreakResults = repository.searchByKeyword("outbreak");
        assertThat(outbreakResults).hasSize(2);
        assertThat(outbreakResults).extracting(EpidemiologicalMaterial::getTitle)
                .containsExactlyInAnyOrder(
                        "Cholera Outbreak Control Manual",
                        "Ebola Hemorrhagic Fever Response"
                );

        List<EpidemiologicalMaterial> protocolResults = repository.searchByKeyword("Protocol");
        assertThat(protocolResults).hasSize(1);
        assertThat(protocolResults.get(0).getTitle()).isEqualTo("Influenza Surveillance Protocol 2026");
    }
}
