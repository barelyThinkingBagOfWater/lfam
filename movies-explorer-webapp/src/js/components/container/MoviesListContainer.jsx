import React, { Component } from "react";
import { connect } from "react-redux";

import { displayMovie } from "../../actions/movieActions"
import MoviesList from "../presentational/MoviesList.jsx";

class ConnectedMoviesListContainer extends Component {
  constructor() {
    super();

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(event) {
    const { movies } = this.props.movies;

    //hack to not care if click comes from primary or secondary from ListItemText
    let movieTitle;
    if (event.target.classList.length == 3) {
      movieTitle = event.target.innerText;
    } else {
      movieTitle = event.target.parentNode.children[0].innerText;
    }

    movies.map(movie => {
      if (movie.title === movieTitle) {
        this.props.displayMovie(movie);
      }
    });
  };

  render() {
    const { movies } = this.props.movies;

    if (movies.length != 0) {
      if (movies === undefined) {
        return (
          <div><br />No Movie found, please try another word</div>
        );
      } else {
        return (
          <div>
            <MoviesList
              movies={movies}
              onClick={(event) => { this.handleClick(event) }}
            /> </div>
        );
      }
    }
    else {
      return (<div />)
    }
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    displayMovie: (movie) => { dispatch(displayMovie(movie)) }
  };
}

const mapStateToProps = (state) => {
  return { movies: state };
};

const MoviesListContainer = connect(mapStateToProps, mapDispatchToProps)
  (ConnectedMoviesListContainer);

export default MoviesListContainer;