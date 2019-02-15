package com.structit.apiclient.data;

public class PlayItem {
    private int mId;
    private String mName;
    private String mAuthor;
    private String mRecord;
    private String mURL;

    public PlayItem() {

    }

    public PlayItem(int id, String name, String author,
                    String record, String url) {
        this.mId = id;
        this.mName = name;
        this.mAuthor = author;
        this.mRecord = record;
        this.mURL = url;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public String getAuthor() {
        return this.mAuthor;
    }

    public String getRecord() {
        return this.mRecord;
    }

    public String getUrl() {
        return this.mURL;
    }

    public void setId(long id) {
        Long identifier = new Long(id);
        this.mId = identifier.intValue();
    }

}
