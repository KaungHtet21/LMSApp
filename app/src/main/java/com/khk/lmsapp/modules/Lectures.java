package com.khk.lmsapp.modules;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Lectures {
    String content, file, id, saveAs;

    public Lectures(){

    }

    public Lectures(String content){
        this.content = content;
    }

    public Lectures(String content, String file, String id, String saveAs) {
        this.content = content;
        this.file = file;
        this.id = id;
        this.saveAs = saveAs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Lectures){
            return id.equals(((Lectures) object).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, file, id, saveAs);
    }
}
