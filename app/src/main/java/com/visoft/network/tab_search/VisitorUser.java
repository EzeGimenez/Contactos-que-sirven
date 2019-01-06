package com.visoft.network.tab_search;

import com.visoft.network.objects.UserNormal;
import com.visoft.network.objects.UserPro;

public interface VisitorUser {

    void visit(UserPro p);

    void visit(UserNormal p);

}
