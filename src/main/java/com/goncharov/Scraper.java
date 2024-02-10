package com.goncharov;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scraper {
    private final String baseUrl;

    public Scraper(String baseURL) {
        this.baseUrl = baseURL;
    }

    private final Connector connector = new Connector();
    private final CSVWriter writer = new CSVWriter();

    public void getReviews() {
        String relativeUrl = baseUrl;

        int pageNumber = 1;

        Elements reviewItemElements = new Elements();

        System.out.println("The scraping process is in progress, please wait...");

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Document doc = connector.getDocument(relativeUrl);

            Elements pages = doc.select(".list");

            if (Objects.isNull(pages.first())) {
                System.out.println("Connection error. Use proxy or try again later");
                break;
            }

            Elements elements = pages.first().getElementsByTag("a");

            Elements reviewItems = doc.select(".reviewItem");
            reviewItemElements.addAll(reviewItems);

            pageNumber++;
            relativeUrl = baseUrl + pageNumber;

            if (!elements.get(elements.size() - 1).text().equals("»»")) {
                break;
            }
        }
        getAllElements(reviewItemElements);
    }

    private void getAllElements(Elements reviewItemElements) {
        List<Review> reviews = new ArrayList<>();

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
        writer.writeToCsv(reviews);
    }
}