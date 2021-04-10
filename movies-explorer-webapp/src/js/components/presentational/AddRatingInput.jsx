import React from "react";
import PropTypes from "prop-types";

import { Rating } from '@material-ui/lab';
import { Button } from "@material-ui/core";


const AddRatingInput = ({ onChange, onClick, value }) => (
  <div className="add-rating-input">
    <span>Add your own rating:</span>
    <Rating name="custom-rating"
      value={value}
      precision={1}
      onChange={onChange} />  
      {/* precision < 1 doesn't work properly when not readonly */}
    <Button variant="contained" onClick={onClick}><b>Add</b></Button>
  </div>
);

AddRatingInput.propTypes = {
  onChange: PropTypes.func.isRequired,
  onClick: PropTypes.func.isRequired
};

export default AddRatingInput;