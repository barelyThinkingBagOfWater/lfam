import React from "react";

import AddTagContainer from "../container/AddTagContainer.jsx";
import AddRatingContainer from "../container/AddRatingContainer.jsx"

import { Rating } from '@material-ui/lab';

const Movie = ({ movie }) => {
  let tags = 'No tag listed';
  let genres = 'No genre listed';
  let rating = 'No rating available for this movie, be the first to review it'
  const movieToDisplay = { movie }.movie;

  if (movieToDisplay === '') {
    return (<div />);
  }

  if (movieToDisplay.tags != undefined) {
    tags = movieToDisplay.tags
      .sort(function (a, b) {
        return a.localeCompare(b)
      })
      .map(function (tag) {
        return <li key={tag.toString()}>{tag}</li>
      })
  }

  if (movieToDisplay.genres != undefined) {
    movieToDisplay.genres;

    genres = movieToDisplay.genres
      .split("|")
      .sort(function (a, b) {
        return a.localeCompare(b)
      })
      .map(function (genre) {
        return <li key={genre.toString()}>{genre}</li>
      })
  }

  if (movieToDisplay.rating != undefined &&
    movieToDisplay.rating.averageRating != undefined) {
    const roundedRating = Number((movieToDisplay.rating.averageRating * 2).toFixed(1));
    const numberOfRatings = movieToDisplay.rating.count;

    rating = <span><Rating value={movieToDisplay.rating.averageRating} precision={0.2} readOnly />
      ({roundedRating} / 10 with {numberOfRatings} ratings)</span>  //less confusing on /5?
  }

  return (
    <div className='movie animate__animated animate__zoomIn'>
      <div><b>Title:</b> {movieToDisplay.title}</div><br />
      <div><b>Genres:</b> <ul>{genres}</ul></div><br />
      <div className="rating"><b>Rating:</b>
        <ul>{rating}</ul>
        <ul><AddRatingContainer /></ul> </div><br />
      <div className='movie-tags-list'><b>Tags:</b>
        <ul><AddTagContainer /></ul>
        <ul>{tags}</ul></div>
    </div>
  )
}

export default Movie