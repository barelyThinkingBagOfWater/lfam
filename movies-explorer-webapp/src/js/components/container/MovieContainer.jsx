import React, { Component } from "react";
import { connect } from "react-redux";
import Movie from "../presentational/Movie.jsx"

class ConnectedMovieContainer extends Component {
    constructor() {
        super();
    }

    render() {
        const { movieToDisplay } = this.props.movieToDisplay;

        return (
            <Movie movie={movieToDisplay} />
        );
    }
};


const mapStateToProps = (state) => {
    return { movieToDisplay: state };
};

const MovieContainer = connect(mapStateToProps)(ConnectedMovieContainer);

export default MovieContainer;