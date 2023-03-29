package models;

public class Patient {
    private String name;
    private String address;
    private String tel;
    private String email;
    private String dateNaissance;
    private String situationFamiliale;


    public Patient(){
        //needed for firebase
    }

    public Patient(String name, String address, String tel, String email, String dateNaissance, String situationFamiliale) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.situationFamiliale = situationFamiliale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(String situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }
}
