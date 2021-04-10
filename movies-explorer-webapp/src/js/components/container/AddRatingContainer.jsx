import React, { Component } from "react";
import { connect } from "react-redux";

import { addRating, changeCustomRating } from "../../actions/movieActions";
import AddRatingInput from "../presentational/AddRatingInput.jsx"


class ConnectedAddRatingContainer extends Component {
    constructor() {
        super();

        this.handleChange = this.handleChange.bind(this);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let payload = new Object();
        payload.customRating = this.props.customRating;
        payload.currentMovieId = this.props.movieToDisplay.movieId;

        this.props.addRating(payload);
    }

    handleChange(e) {
        const customRating = event.target.value;

        this.props.changeCustomRating(customRating);
    }

    render() {
        const ratingToDisplay = parseFloat(this.props.customRating);

        return (
            <AddRatingInput
                onChange={this.handleChange}
                onClick={this.handleClick}
                value={ratingToDisplay} />
        );
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        changeCustomRating: (customRating) => dispatch(changeCustomRating(customRating)),
        addRating: (payload) => dispatch(addRating(payload))
    };
}

const mapStateToProps = (state) => {
    return {
        customRating: state.customRating,
        movieToDisplay: state.movieToDisplay
    };
};

const AddRatingContainer = connect(mapStateToProps, mapDispatchToProps)
    (ConnectedAddRatingContainer);

export default AddRatingContainer;