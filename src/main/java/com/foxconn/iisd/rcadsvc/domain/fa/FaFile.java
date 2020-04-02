package com.foxconn.iisd.rcadsvc.domain.fa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "fa_file")
public class FaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String objectName;

    @JsonIgnoreProperties
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "fa_case_id", referencedColumnName = "id")
    private FaCase faCase;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private BigInteger sizeInBytes;

    @Transient
    private MultipartFile file;

    public void clearId() {
        id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public FaCase getFaCase() {
        return faCase;
    }

    public void setFaCase(FaCase faCase) {
        this.faCase = faCase;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(BigInteger sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
