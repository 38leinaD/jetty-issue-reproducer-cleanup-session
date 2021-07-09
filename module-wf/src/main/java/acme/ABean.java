package acme;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@SessionScoped
public class ABean implements Serializable {
    private String holder;

    public void setValue(String value) {
        this.holder = value;
    }
}
