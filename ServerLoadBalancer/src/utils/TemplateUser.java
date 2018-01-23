package ServerLoadBalancer.src.utils;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Collection;

class TemplateUser implements Serializable {

    @Expose
    private String id;

    @Expose
    private String gender;

    @Expose
    private String eyes;

    @Expose
    private Collection<String> roles;

    @Expose
    private boolean isActive;

    @Expose
    private boolean isSuperAdmin;

    @Expose
    private int age;

    TemplateUser(String id, String gender, String eyes, Collection<String> roles, boolean isActive, boolean isSuperAdmin, int age) {
        this.id = id;
        this.gender = gender;
        this.eyes = eyes;
        this.roles = roles;
        this.isActive = isActive;
        this.isSuperAdmin = isSuperAdmin;
        this.age = age;
    }
}
