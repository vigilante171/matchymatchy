package com.moviematch.backend.service;

import com.moviematch.backend.config.OmdbProperties;
import com.moviematch.backend.dto.MovieResponse;
import com.moviematch.backend.dto.OmdbMovieResponse;
import com.moviematch.backend.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MovieService {

    private final OmdbProperties omdbProperties;
    private final RestClient restClient;

    public MovieService(OmdbProperties omdbProperties) {

        this.omdbProperties = omdbProperties;

        this.restClient = RestClient.builder()
                .baseUrl(omdbProperties.getBaseUrl())
                .build();
    }

    public MovieResponse getMovieById(String imdbId) {

        OmdbMovieResponse omdbMovie = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", omdbProperties.getApiKey())
                        .queryParam("i", imdbId)
                        .queryParam("plot", "full")
                        .build())
                .retrieve()
                .body(OmdbMovieResponse.class);

        if (omdbMovie == null ||
                "False".equalsIgnoreCase(omdbMovie.getResponse())) {

            String message = omdbMovie != null &&
                    omdbMovie.getError() != null
                    ? omdbMovie.getError()
                    : "Movie not found";

            throw new MovieNotFoundException(message);
        }

        MovieResponse movie = new MovieResponse();

        movie.setImdbId(omdbMovie.getImdbID());
        movie.setTitle(omdbMovie.getTitle());
        movie.setYear(omdbMovie.getYear());
        movie.setPoster(omdbMovie.getPoster());
        movie.setGenre(omdbMovie.getGenre());
        movie.setDirector(omdbMovie.getDirector());
        movie.setPlot(omdbMovie.getPlot());
        movie.setRating(omdbMovie.getImdbRating());

        return movie;
    }
}