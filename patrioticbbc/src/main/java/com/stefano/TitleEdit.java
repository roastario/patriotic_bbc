package com.stefano;

import com.google.appengine.api.datastore.Entity;

/**
 * Author stefanofranz
 */
public class TitleEdit {

    private volatile String titleId;
    private volatile String newTitle;
    private String UUID;

    public TitleEdit() {
    }

    public TitleEdit(String titleId, String newTitle, String UUID) {
        this.titleId = titleId;
        this.newTitle = newTitle;
        this.UUID = UUID;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getNewTitle() {
        return newTitle;
    }

    @Override
    public String toString() {
        return "TitleEdit{" +
                "titleId='" + titleId + '\'' +
                ", newTitle='" + newTitle + '\'' +
                '}';
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUUID() {
        return UUID;
    }

    public Entity asEntity() {
        Entity entity = new Entity(dataStoreType(), UUID);
        entity.setProperty("titleId", titleId);
        entity.setProperty("newTitle", newTitle);
        entity.setProperty("id", UUID);
        return entity;
    }

    public static String dataStoreType() {
        return "Title";
    }

    public static TitleEdit fromEntity(Entity entity) {

        String titleId = (String) entity.getProperty("titleId");
        String newTitle = (String) entity.getProperty("newTitle");
        String UUID = (String) entity.getProperty("UUID");

        return new TitleEdit(titleId, newTitle, UUID);

    }
}
