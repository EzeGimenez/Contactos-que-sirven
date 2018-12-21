package com.visoft.network.Objects;

import com.visoft.network.MainPageSearch.VisitorUser;

/**
 * Clase UserNormal, usada para almacenar la informacion de un usuario
 */
public class UserNormal extends User {

    @Override
    public void acept(VisitorUser v) {
        v.visit(this);
    }
}
