import {
    ADD_CUSTOM_TAG_TO_CURRENT_MOVIE,
    ADD_CUSTOM_RATING_TO_CURRENT_MOVIE,
    CHANGE_SEARCH_INPUT,
    CHANGE_CUSTOM_TAG_INPUT,
    CHANGE_CUSTOM_RATING,
    DISPLAY_ERROR_TAGS,
    DISPLAY_ERROR_MOVIES,
    DISPLAY_TAGS,
    DISPLAY_MOVIES,
    DISPLAY_MOVIE,
    DISPLAY_RATINGS_OF_MOVIE
} from "../actions/action-types";


const initialState = {
    searchInput: '',
    customTagInput: '',
    customRating: 0.0,
    tags: [],
    movies: [],
    movieToDisplay: ''
};


function rootReducer(state = initialState, action) {

    if (action.type == CHANGE_SEARCH_INPUT) {
        return Object.assign({}, state, {
            searchInput: action.payload
        });
    }

    if (action.type == CHANGE_CUSTOM_TAG_INPUT) {
        return Object.assign({}, state, {
            customTagInput: action.payload
        });
    }

    if (action.type == CHANGE_CUSTOM_RATING) {
        return Object.assign({}, state, {
            customRating: action.payload
        });
    }


    if (action.type == DISPLAY_TAGS) {
        let tagsSortedAlphabetically = action.payload.sort(function(a, b) {
            return a.tagName.localeCompare(b.tagName);
          });

        return Object.assign({}, state, {
            tags: tagsSortedAlphabetically
        });
    }

    if (action.type == DISPLAY_ERROR_TAGS) {
        console.log("Error received during tags fetching:", action.payload);
        //dans app fait un OR avec une des variables de ton state qui indiquerait le message d'erreur?
        //update: wait what? Are you thinking in bitset again? :)
    }

    if (action.type == DISPLAY_MOVIES) {
        let moviesSortedByTitle = action.payload.sort(function(a, b) {
            return a.title.localeCompare(b.title)
          });

        return Object.assign({}, state, {
            movies: moviesSortedByTitle
        });
    }

    if (action.type == DISPLAY_ERROR_MOVIES) {
        console.log("display error movies received", action.payload);
    }

    if (action.type == DISPLAY_MOVIE) {
        return Object.assign({}, state, {
            movieToDisplay: action.payload
        });
    }

    if (action.type == DISPLAY_RATINGS_OF_MOVIE) {
        const currentMovie = state.movieToDisplay
        currentMovie.rating = action.payload

        return Object.assign({}, state, {
            movieToDisplay: currentMovie
        });
    }

    if (action.type == ADD_CUSTOM_TAG_TO_CURRENT_MOVIE) {
        const currentMovie = state.movieToDisplay;
        currentMovie.tags.push(state.customTagInput);

        return Object.assign({}, state, {
            movieToDisplay: currentMovie
        });
    }

    if (action.type == ADD_CUSTOM_RATING_TO_CURRENT_MOVIE) { //later add some restrictions based on the answer of the service (if already rating for userId - movieId (takes last rating?) and returns a not 200)
        const currentMovie = state.movieToDisplay;

        if (currentMovie.rating == null) {
            currentMovie.rating = new Object();
            currentMovie.rating.count = 0;
            currentMovie.rating.ratings = 0;
        }

        let currentRating = currentMovie.rating;

        currentRating.count ++;
        currentRating.ratings = currentRating.ratings + parseFloat(state.customRating);
        currentRating.averageRating = parseFloat(currentRating.ratings / currentRating.count);

        currentMovie.rating = currentRating;

        return Object.assign({}, state, {
            movieToDisplay: currentMovie
        });
    }
    

    return state;
}

export default rootReducer;