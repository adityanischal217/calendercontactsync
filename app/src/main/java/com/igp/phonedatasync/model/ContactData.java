package com.igp.phonedatasync.model;

public class ContactData {
    String name;
    String birthday;
    String anniversary;

    public ContactData() {
    }

    public ContactData(String name, String birthday, String anniversary, String phone) {
        this.name = name;
        this.birthday = birthday;
        this.anniversary = anniversary;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;
}
