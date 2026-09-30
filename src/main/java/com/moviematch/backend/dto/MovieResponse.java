package com.moviematch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    private String imdbId;

    private String title;

    private String year;

    private String poster;

    private String genre;

    private String director;

    private String plot;

    private String rating;
}
