package com.khk.lmsapp.modules;

import java.util.Objects;

public class Bookmark {
    String file, fileId, saveAs, userId;

    public Bookmark(){

    }

    public Bookmark(String file, String fileId, String saveAs, String userId) {
        this.file = file;
        this.fileId = fileId;
        this.saveAs = saveAs;
        this.userId = userId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Bookmark){
            return userId.equals(((Bookmark) object).userId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, fileId, saveAs, userId);
    }
}
