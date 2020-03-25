package base.engineering.motosumiyoshi.volleymanageapp.model;

import java.util.Date;

public class News {
    private String id;
    private String createDate;
    private String text;

    public News(){};

    public News(String createDate, String text) {
        this.createDate = createDate;
        this.text = text;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
