package com.foxconn.iisd.rcadsvc.domain.fa;

import javax.persistence.*;

@Entity
@Table(name = "fa_sympton")
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private SymptomType type;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private String line;

    @Column(nullable = false)
    private String station;

    public void clearId() {
        id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymptomType getType() {
        return type;
    }

    public void setType(SymptomType type) {
        this.type = type;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}