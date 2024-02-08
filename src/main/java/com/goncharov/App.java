package com.goncharov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    static final String USERAGENT = "Dalvik/2.1.0 (Linux; U; Android 9; ADT-2 Build/PTT5.181126.002)";
    static final String BASEURL = "https://www.kinopoisk.ru/film/326/reviews/ord/date/status/all/perpage/100/page/";
    static List<Review> reviews = new ArrayList<>();

    public void getReviews() {
        String relativeUrl = BASEURL;

        int pageNumber = 1;

        Elements reviewItemElements = new Elements();

        System.out.println("The scraping process is in progress, please wait...");

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Document doc = getDocument(relativeUrl);

            Elements pages = doc.select(".list");
            Elements elements = pages.first().getElementsByTag("a");
            Elements reviewItems = doc.select(".reviewItem");
            reviewItemElements.addAll(reviewItems);

            pageNumber++;
            relativeUrl = BASEURL + pageNumber;

            if (!elements.get(elements.size() - 1).text().equals("»»")) {
                break;
            }
        }

        getAllElements(reviewItemElements);
    }

    private void getAllElements(Elements reviewItemElements) {
        for (Element element : reviewItemElements) {
            String author = element.select(".profile_name").first().text();
            String date = element.select(".date").first().text();
            String subTitle = element.select(".sub_title").first().text();
            String text = element.select("._reachbanner_").first().text();

            String grade;

            if (element.select(".good").first() != null) {
                grade = "Положительный отзыв";
            } else if (element.select(".bad").first() != null) {
                grade = "Отрицательный отзыв";
            } else {
                grade = "Нейтральный отзыв";
            }

            Review review = Review.builder()
                    .author(author)
                    .date(date)
                    .grade(grade)
                    .subTitle(subTitle)
                    .text(text)
                    .build();

            reviews.add(review);
        }
        writeToCsv(reviews);
    }

    private void writeToCsv(List<Review> reviews) {
        File csvFile = new File("output.csv");

        try (PrintWriter pw = new PrintWriter(csvFile)) {

            reviews.forEach(review -> {
                String[] data = new String[]{
                        review.getAuthor(),
                        review.getDate(),
                        review.getGrade(),
                        review.getSubTitle(),
                        review.getText()
                };

                pw.println(convertToCsv(data));
            });

            System.out.println("Done! Check the output.scv file.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToCsv(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    private Document getDocument(String URL) {
        try {
            return Jsoup
                    .connect(URL)
                    .userAgent(USERAGENT)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}