package com.eneik.generated.material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "pathogen_type")
    private String pathogenType;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "metadata")
    private String metadata;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status = "DRAFT";

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Material() {
    }

    public Material(UUID id, String title, String content, String metadata) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.metadata = metadata;
        this.status = "DRAFT";
        this.createdAt = Instant.now();
    }

    public Material(UUID id, String title, String pathogenType, String content, String metadata, String status) {
        this.id = id;
        this.title = title;
        this.pathogenType = pathogenType;
        this.content = content;
        this.metadata = metadata;
        this.status = status != null ? status : "DRAFT";
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(id, material.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
