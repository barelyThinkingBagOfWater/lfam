let CLUSTER_URL;

if (window.REACT_APP_CLUSTER_URL == undefined) {
    // GKE
    CLUSTER_URL = 'https://letsfindamovie.com'
} else {
    //Url coming from conf
    CLUSTER_URL = window.REACT_APP_CLUSTER_URL;
}

// Wiremock env for development
// const CLUSTER_URL = 'http://localhost:8080'


const MOVIE_CACHE_URL = CLUSTER_URL + '/api/movies?'
const TAGS_CACHE_URL = CLUSTER_URL + '/api/tag/{tag}'
const RATINGS_CACHE_URL = CLUSTER_URL + '/api/rating/{movieId}'

const CONVERTER_GATEWAY_URL_FOR_TAG = CLUSTER_URL + '/api/movie/{movieId}/tag/{tag}'
const CONVERTER_GATEWAY_URL_FOR_RATING = CLUSTER_URL + '/api/movie/{movieId}/rating/{rating}'

const GRAFANA_PUBLIC_DASHBOARD_RELATIVE_URL = 'api/grafana/d/public-dashboard/public-dashboard?orgId=1&refresh=5s&kiosk=tv'
const GRAFANA_ALL_DASHBOARDS_URL = 'api/grafana/dashboards'

export default class Config {
    static getTagsCacheUrlForTag(tag) {
        return TAGS_CACHE_URL.replace("{tag}", tag)
    }

    static getMovieCacheUrlForMovieIds(movieIds) {
        let url = MOVIE_CACHE_URL;

        movieIds.map(movieId => {
            url = url.concat("&id=", movieId);
        })

        return url;
    }


    static getRatingCacheUrlForMovieId(movieId) {
        return RATINGS_CACHE_URL.replace("{movieId}", movieId)
    }

    static getConverterGatewayUrlToAddTagToMovie(tag, movieId) {
        return CONVERTER_GATEWAY_URL_FOR_TAG
            .replace("{tag}", tag)
            .replace("{movieId}", movieId)
    }

    static getConverterGatewayUrlToAddRating(rating, movieId) {
        return CONVERTER_GATEWAY_URL_FOR_RATING
            .replace("{rating}", rating)
            .replace("{movieId}", movieId)
    }

    static getGrafanaPublicDashboardUrl() {
        return GRAFANA_PUBLIC_DASHBOARD_RELATIVE_URL;
    }

    static getGrafanaAllDashboardsUrl() {
        return GRAFANA_ALL_DASHBOARDS_URL;
    }
}
