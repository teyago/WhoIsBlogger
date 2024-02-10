package com.goncharov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {
    private final Converter converter = new Converter();

    public void writeToCsv(List<Review> reviews) {
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

                pw.println(converter.convertToCsv(data));
            });

            System.out.println("Done! Check the output.scv file.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
