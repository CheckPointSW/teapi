package com.checkpoint.tp_api.dir;

import java.io.File;

/**
 * Created by edanha on 4/4/2017.
 */
public class FileToQuery {
    private String md5;
    private String  sha1;
    private File file;

    public FileToQuery(String md5, String sha1, File file) {
        this.md5 = md5;
        this.sha1 = sha1;
        this.file = file;
    }

    public FileToQuery(FileToQuery fileToQuery) {
        this.md5 = md5;
        this.sha1 = sha1;
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public String getSha1() {
        return sha1;
    }

    public File getFile() {
        return file;
    }
}
