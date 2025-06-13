package vn.edu.tlu.buicongson.football247.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Article {
    private String id;
    private String title;
    private String description;
    private String content;
    private Timestamp createdAt;
    private String headerImageUrl;
    private List<String> imageUrls;

    public Article() {}

    public Article(String title, String description, String content, Timestamp createdAt, String headerImageUrl, List<String> imageUrls) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.createdAt = createdAt;
        this.headerImageUrl = headerImageUrl;
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }
}
