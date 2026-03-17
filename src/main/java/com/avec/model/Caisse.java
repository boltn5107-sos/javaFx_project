package com.avec.model;

public class Caisse {

    private Long id;
    private String codeSecurite;
    private Long avecId;
    private Avec avec;

    public Caisse() {
    }

    public Caisse(Long avecId) {
        this.avecId = avecId;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeSecurite() {
        return codeSecurite;
    }

    public void setCodeSecurite(String codeSecurite) {
        this.codeSecurite = codeSecurite;
    }

    public Long getAvecId() {
        return avecId;
    }

    public void setAvecId(Long avecId) {
        this.avecId = avecId;
    }

    public Avec getAvec() {
        return avec;
    }

    public void setAvec(Avec avec) {
        this.avec = avec;
        if (avec != null) {
            this.avecId = avec.getId();
        }
    }

    @Override
    public String toString() {
        return "Caisse{" +
                "id=" + id +
                ", codeSecurite='" + codeSecurite + '\'' +
                ", avecId=" + avecId +
                '}';
    }
}