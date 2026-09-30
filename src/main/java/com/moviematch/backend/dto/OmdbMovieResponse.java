package com.moviematch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmdbMovieResponse {
    private String imdbID;

    private String Title;

    private String Year;

    private String Poster;

    private String Genre;

    private String Director;

    private String Plot;

    private String imdbRating;

    private String Response;

    private String Error;
}
