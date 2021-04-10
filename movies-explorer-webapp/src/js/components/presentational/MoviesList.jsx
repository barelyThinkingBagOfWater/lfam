import React from "react";
import PropTypes from "prop-types";

import { List, ListItem, ListItemText } from '@material-ui/core';


const MoviesList = ({ movies, onClick }) => (
  <div className="movies-list-container">
    <div class="animate__animated animate__pulse" style={textStyle}>
      Now choose a movie</div>
    <br />

    <List component="nav" className="movies-list">
      {movies.map(movie => (
        <ListItem
          className="movies-list-item animate__animated animate__bounceInDown"
          onClick={onClick}
          button key={movie.movieId} >
          <ListItemText
            primary={movie.title}
            secondary={movie.genres} />
        </ListItem>
      ))}
    </List>
  </div>
)

const textStyle = {
  fontSize: "22px",
  fontFamily: "Yeon Sung, cursive",
  fontWeight: "bold",
}

MoviesList.propTypes = {
  movies: PropTypes.array.isRequired,
  onClick: PropTypes.func.isRequired
}

export default MoviesList