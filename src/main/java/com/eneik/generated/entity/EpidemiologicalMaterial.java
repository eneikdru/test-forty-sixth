package com.eneik.generated.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "epidemiological_materials", indexes = {
    @Index(name = "idx_epidemiological_materials_pathogen_type", columnList = "pathogen_type"),
    @Index(name = "idx_epidemiological_materials_title", columnList = "title")
})
public class EpidemiologicalMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "pathogen_type", nullable = false)
    private String pathogenType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public EpidemiologicalMaterial() {
    }

    public EpidemiologicalMaterial(String title, String pathogenType, String content) {
        this.title = title;
        this.pathogenType = pathogenType;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPathogenType() {
        return pathogenType;
    }

    public void setPathogenType(String pathogenType) {
        this.pathogenType = pathogenType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
