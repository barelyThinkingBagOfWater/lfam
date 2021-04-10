import React, { Component } from "react";
import { connect } from "react-redux";

import { fetchMovies } from "../../actions/movieActions"
import TagsList from "../presentational/TagsList.jsx";


class ConnectedTagsListContainer extends Component {
  constructor() {
    super();

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(event) {
    const { tags } = this.props.tags;

    tags.map(tag => {
      if (tag.tagName === event.target.innerText) {
        this.props.fetchMovies(tag.movieIds);
      }
    })
  };

  render() {
    const { tags } = this.props.tags;

    if (tags.length != 0) {
      return (
        <TagsList
          tags={tags}
          onClick={(event) => { this.handleClick(event) }}
        />
      );
    } else {
      return (<div/>);
    }
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    fetchMovies: (movieIds) => { dispatch(fetchMovies(movieIds)) }
  };
}

const mapStateToProps = (state) => {
  return { tags: state };
}

const TagsListContainer = connect(mapStateToProps, mapDispatchToProps)
  (ConnectedTagsListContainer)

export default TagsListContainer