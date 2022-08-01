package com.example.music;

public class LocalMusicBean {

    private String number; //歌曲id
    private String song; //歌名
    private String singer; //歌手
    private String album; //專輯
    private String time; //歌曲時長
    private String path; //歌曲路徑

    public LocalMusicBean() {
    }

    public LocalMusicBean(String number, String song, String singer, String album, String time, String path) {
        this.number = number;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.time = time;
        this.path = path;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
