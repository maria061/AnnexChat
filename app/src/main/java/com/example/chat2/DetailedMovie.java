package com.example.chat2;

public class DetailedMovie {
    private String title;
    private int year;
    private String released;
    private String runtime;
    private String genre;
    private String country;
    private String awards;
    private String language;
    private String response;

    public DetailedMovie(String title, int year, String released, String runtime, String genre, String country, String awards, String language, String response) {
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.country = country;
        this.awards = awards;
        this.language = language;
        this.response = response;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "DetailedMovie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", released='" + released + '\'' +
                ", runtime='" + runtime + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", awards='" + awards + '\'' +
                ", language='" + language + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
