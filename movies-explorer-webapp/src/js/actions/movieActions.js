import {
    ADD_RATING,
    ADD_CUSTOM_RATING_TO_CURRENT_MOVIE,
    CHANGE_CUSTOM_RATING,
    FETCH_MOVIES,
    DISPLAY_MOVIE,
    DISPLAY_MOVIES,
    DISPLAY_ERROR_MOVIES,
    DISPLAY_RATINGS_OF_MOVIE
} from "./action-types";


export function fetchMovies(payload) {
    return { type: FETCH_MOVIES, payload }
};

export function displayMovies(payload) {
    return { type: DISPLAY_MOVIES, payload }
};

export function displayErrorMessageMovies(payload) {
    return { type: DISPLAY_ERROR_MOVIES, payload }
};

export function displayMovie(payload) {
    return { type: DISPLAY_MOVIE, payload }
};

export function displayRatingOfMovie(payload) {
    return { type: DISPLAY_RATINGS_OF_MOVIE, payload }
};

export function addRating(payload) {
    return { type: ADD_RATING, payload }
};

export function addCustomRatingToCurrentMovie() { //so we are sure that we only add it in the webapp if the ajax call worked
    return { type: ADD_CUSTOM_RATING_TO_CURRENT_MOVIE }
};

export function changeCustomRating(payload) {
    return { type: CHANGE_CUSTOM_RATING, payload }
};