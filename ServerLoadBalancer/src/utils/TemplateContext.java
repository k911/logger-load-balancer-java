package utils;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

class TemplateContext implements Serializable {

    @Expose
    private String type;

    @Expose
    private TemplateUser user;

    TemplateContext(String type, TemplateUser user) {
        this.type = type;
        this.user = user;
    }
}
