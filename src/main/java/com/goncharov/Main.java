package com.goncharov;

public class Main {
    public static void main(String[] args) {
        Scraper scraper = new Scraper("https://www.kinopoisk.ru/film/326/reviews/ord/date/status/all/perpage/100/page/");
        scraper.getReviews();
    }
}