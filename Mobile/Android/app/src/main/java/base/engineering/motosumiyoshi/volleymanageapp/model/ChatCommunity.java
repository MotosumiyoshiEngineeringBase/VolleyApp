package base.engineering.motosumiyoshi.volleymanageapp.model;

public class ChatCommunity {
    private String id;
    private String name;
    private String explain;
    private String photoUrl;

    public ChatCommunity(){}

    public ChatCommunity(String id, String name, String explain, String photoUrl){
        this.id = id;
        this.name = name;
        this.explain = explain;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
