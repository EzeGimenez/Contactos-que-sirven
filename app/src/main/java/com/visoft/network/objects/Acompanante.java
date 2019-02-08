package com.visoft.network.objects;

import java.io.Serializable;

public class Acompanante implements Serializable {
    private String dni, name, phone;

    public Acompanante(String d, String n, String p) {
        this.dni = d;
        this.name = n;
        this.phone = p;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
