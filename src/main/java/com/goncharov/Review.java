package com.goncharov;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Review {
    private String author;
    private String date;
    private String grade;
    private String subTitle;
    private String text;
}
