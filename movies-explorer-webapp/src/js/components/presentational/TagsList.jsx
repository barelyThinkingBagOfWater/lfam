import React from "react";
import PropTypes from "prop-types";

import { List, Chip } from '@material-ui/core';


const TagsList = ({ tags, onClick }) => (
  <div className="tags-list">
    <br />
    <div class="animate__animated animate__shakeX" style={textStyle}>
      Pick a tag describing your movie </div>
    <br />

    <List style={tagsListStyle}>
      {tags.map(tag => (
        <div class="animate__animated animate__backInLeft" >
          <Chip key={tag.tagName} style={tagStyle} label={tag.tagName} onClick={onClick} />
        </div>
      ))}
    </List>
    <br />
  </div>
)


const textStyle = {
  fontSize: "22px",
  fontFamily: "Yeon Sung, cursive",
  fontWeight: "bold",
}

const tagStyle = {
  color: "white",
  backgroundColor: "rgb(83, 76, 75)",
  fontWeight: "bolder",
  fontFamily: "Yeon Sung, cursive",
  fontSize: "17px",
  marginRight: "10px",
  marginTop: "10px"
}

const tagsListStyle = {
  display: 'flex',
  flexDirection: 'row',
  flexWrap: 'wrap',
  justifyContent: 'space-evenly',
}

TagsList.propTypes = {
  tags: PropTypes.array.isRequired,
  onClick: PropTypes.func.isRequired
}

export default TagsList