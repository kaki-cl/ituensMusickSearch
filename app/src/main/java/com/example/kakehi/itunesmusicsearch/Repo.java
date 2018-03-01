package com.example.kakehi.itunesmusicsearch;


import lombok.Data;

@Data
public class Repo {

    public Repo() {}

    public String id;
    public String title;
    public String url;
    public User user;
}
