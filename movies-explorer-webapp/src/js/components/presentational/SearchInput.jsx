import React from "react";
import PropTypes from "prop-types";
import TextField from '@material-ui/core/TextField';

const SearchInput = ({ text, value, handleChange, placeholder }) => (
  <div className="search-input-container">
    <br /><br />
    <div class="animate__animated animate__pulse" style={style}>{text}</div>
    <br /><br />
    
    <TextField
      multiline={false}
      className='search-input'
      type='text'
      id='searchInputId'
      inputProps={value}
      onChange={handleChange}
      placeholder={placeholder}
      required />
  </div>
)

const style = {
  fontSize: "22px",
  fontFamily: "Yeon Sung, cursive",
  fontWeight: "bold",
}

SearchInput.propTypes = {
  text: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string.isRequired
}

export default SearchInput

