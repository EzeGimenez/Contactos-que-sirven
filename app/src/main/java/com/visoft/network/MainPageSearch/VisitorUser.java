package com.visoft.network.MainPageSearch;

import com.visoft.network.Objects.UserNormal;
import com.visoft.network.Objects.UserPro;

public interface VisitorUser {

    void visit(UserPro p);

    void visit(UserNormal p);

}
