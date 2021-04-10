import { ofType } from 'redux-observable';
import { ajax } from 'rxjs/observable/dom/ajax';
import { switchMap, map, catchError, debounceTime } from 'rxjs/operators';

import { CHANGE_SEARCH_INPUT, FETCH_MOVIES, ADD_TAG, DISPLAY_MOVIE, ADD_RATING }
    from "../actions/action-types";
import { displayTags, addCustomTagToCurrentMovie }
    from "../actions/tagActions";
import { displayMovies, displayRatingOfMovie, addCustomRatingToCurrentMovie }
    from "../actions/movieActions";

import Config from '../config/Config';

//Check https://github.com/creativetimofficial/material-kit-react#demo for login page and future evolutions

export function fetchTagsEpic(action$) {
    return action$.pipe(
        ofType(CHANGE_SEARCH_INPUT),
        debounceTime(300), //should be configurable in case of big traffic
        switchMap(action => {
            return getJSON(
                Config.getTagsCacheUrlForTag(action.payload), {})
        }),
        map(request => request.response),
        map(tags => displayTags(tags)))
};

//move me later
export function fetchMoviesEpic(action$) {
    return action$.pipe(
        ofType(FETCH_MOVIES),
        switchMap(action => {
            return getJSON(Config.getMovieCacheUrlForMovieIds(
                action.payload), {})
        }),
        map(request => request.response),
        map(movies => displayMovies(movies)))
};

export function fetchRatingEpic(action$) {
    return action$.pipe(
        ofType(DISPLAY_MOVIE),
        switchMap(action => {
            return getJSON(Config.getRatingCacheUrlForMovieId(action.payload.movieId), {})
        }),
        map(request => request.response),
        map(rating => displayRatingOfMovie(rating)))
};


export function addTagEpic(action$) {
    return action$.pipe(
        ofType(ADD_TAG),
        switchMap(action => {
            return putJSON(
                Config.getConverterGatewayUrlToAddTagToMovie(
                    action.payload.customTag, action.payload.currentMovieId), {})
        }),
        map(request => addCustomTagToCurrentMovie()),
        catchError(error => alert("Our system seems to not accept tags at the moment, apologies... \n You can still keep browsing however :)"))
    )
};

export function addRatingEpic(action$) {
    return action$.pipe(
        ofType(ADD_RATING),
        switchMap(action => {
            return putJSON(
                Config.getConverterGatewayUrlToAddRating(
                    action.payload.customRating, action.payload.currentMovieId), {})
        }),
        map(request => addCustomRatingToCurrentMovie()),
        catchError(error => alert("Our system seems to not accept ratings at the moment, apologies... \n You can still keep browsing however :)"))
    )
};


const getJSON = (url, headers) =>
    ajax({
        method: "GET",
        url,
        headers
    });

const putJSON = (url, headers) =>
    ajax({
        method: "PUT",
        url,
        headers
    });