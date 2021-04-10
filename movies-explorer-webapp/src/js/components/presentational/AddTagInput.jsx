import React from "react";
import PropTypes from "prop-types";
import TextField from '@material-ui/core/TextField';
import { Button } from "@material-ui/core";


const AddTagInput = ({ placeholder, handleChange, customTag, handleClick }) => (
  <div className="add-tag-input">
    <TextField
      type='text'
      id='addTagInputId'
      placeholder={placeholder}
      inputProps={customTag}
      onChange = {handleChange}
      />
     <Button variant="contained" onClick = {handleClick}><b>Add</b></Button>
  </div>
);

AddTagInput.propTypes = {
  placeholder: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleClick: PropTypes.func.isRequired
};


export default AddTagInput;