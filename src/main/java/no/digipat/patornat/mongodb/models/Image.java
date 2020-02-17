package no.digipat.patornat.mongodb.models;

public class Image {
    private int id;
    private String comment;
    // Todo change names to english
    private int kjernestruktur;
    private int cellegrenser;
    private int kontrastKollagen;
    private int kontrastBindevev;

    public Image(int id, String comment, int kjernestruktur, int cellegrenser, int kontrastKollagen, int kontrastBindevev) {
        this.id = id;
        this.comment = comment;
        this.kjernestruktur = kjernestruktur;
        this.cellegrenser = cellegrenser;
        this.kontrastKollagen = kontrastKollagen;
        this.kontrastBindevev = kontrastBindevev;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getKjernestruktur() {
        return kjernestruktur;
    }

    public void setKjernestruktur(int kjernestruktur) {
        this.kjernestruktur = kjernestruktur;
    }

    public int getCellegrenser() {
        return cellegrenser;
    }

    public void setCellegrenser(int cellegrenser) {
        this.cellegrenser = cellegrenser;
    }

    public int getKontrastKollagen() {
        return kontrastKollagen;
    }

    public void setKontrastKollagen(int kontrastKollagen) {
        this.kontrastKollagen = kontrastKollagen;
    }

    public int getKontrastBindevev() {
        return kontrastBindevev;
    }

    public void setKontrastBindevev(int kontrastBindevev) {
        this.kontrastBindevev = kontrastBindevev;
    }
}
