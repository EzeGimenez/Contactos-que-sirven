package com.visoft.network.MainPageSearch;

import com.visoft.network.Objects.UserNormal;
import com.visoft.network.Objects.UserPro;

import java.util.ArrayList;
import java.util.List;

public class VisitorUserGetProUser implements VisitorUser {

    private List<UserPro> list;

    public VisitorUserGetProUser() {
        list = new ArrayList<>();
    }

    @Override
    public void visit(UserPro p) {
        list.add(p);
    }

    @Override
    public void visit(UserNormal p) {

    }

    public List<UserPro> getList() {
        return list;
    }
}
