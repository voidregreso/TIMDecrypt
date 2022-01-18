package es.perez.tim_decryptsqlmsg;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AmigoList {
    private final SimpleIntegerProperty Age = new SimpleIntegerProperty();
    private final SimpleStringProperty Alias = new SimpleStringProperty();
    private final SimpleStringProperty Gender = new SimpleStringProperty();
    private final SimpleStringProperty Name = new SimpleStringProperty();
    private final SimpleStringProperty Remark = new SimpleStringProperty();
    private final SimpleStringProperty QQID = new SimpleStringProperty();

    public int getAge() {
        return Age.get();
    }

    public void setAge(int a) {
        Age.set(a);
    }

    public String getAlias() {
        return Alias.get();
    }

    public void setAlias(String a) {
        Alias.set(a);
    }

    public String getGender() {
        return Gender.get();
    }

    public void setGender(String a) {
        Gender.set(a);
    }

    public String getName() {
        return Name.get();
    }

    public void setName(String a) {
        Name.set(a);
    }

    public String getRemark() {
        return Remark.get();
    }

    public void setRemark(String a) {
        Remark.set(a);
    }

    public String getQQID() {
        return QQID.get();
    }

    public void setQQID(String a) {
        QQID.set(a);
    }
}
