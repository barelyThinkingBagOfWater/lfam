import {
    ADD_TAG,
    ADD_CUSTOM_TAG_TO_CURRENT_MOVIE,
    CHANGE_SEARCH_INPUT,
    CHANGE_CUSTOM_TAG_INPUT,
    FETCH_TAGS,
    DISPLAY_TAGS,
    DISPLAY_ERROR_TAGS
} from "./action-types";


export function changeSearchInput(payload) {
    return { type: CHANGE_SEARCH_INPUT, payload }
};

export function changeCustomTagInput(payload) {
    return { type: CHANGE_CUSTOM_TAG_INPUT, payload }
};

export function fetchTags(payload) {
    return { type: FETCH_TAGS, payload }
};

export function displayTags(payload) {
    return { type: DISPLAY_TAGS, payload }
};

export function displayErrorMessageTags(payload) {
    return { type: DISPLAY_ERROR_TAGS, payload }
};

export function addTag(payload) {
    return { type: ADD_TAG, payload }
};

export function addCustomTagToCurrentMovie() { //so we are sure that we only add it in the webapp if the ajax call worked
    return { type: ADD_CUSTOM_TAG_TO_CURRENT_MOVIE }
};