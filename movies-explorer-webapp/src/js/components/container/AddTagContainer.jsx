import React, { Component } from "react";
import { connect } from "react-redux";

import { addTag, changeCustomTagInput } from "../../actions/tagActions";
import AddTagInput from "../presentational/AddTagInput.jsx"


class ConnectedAddTagContainer extends Component {
    constructor() {
        super();

        this.handleChange = this.handleChange.bind(this);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let payload = new Object();
        payload.customTag = this.props.customTagInput;
        payload.currentMovieId = this.props.movieToDisplay.movieId;

        if (!this.props.movieToDisplay.tags.includes(payload.customTag)) {
            this.props.addTag(payload);
        } else {
            alert("The tag already exists for this movie!");
            //a proper dialog? https://material-ui.com/components/dialogs/
        }
    }

    handleChange(e) {
        const customTagInput = event.target.value.trim();

        this.props.changeCustomTagInput(customTagInput);
    }

    render() {
        const { customTagInput } = this.props.customTagInput;

        return (
            <AddTagInput
                placeholder='Enter here your own tag'
                handleChange={this.handleChange}
                inputProps={customTagInput}
                handleClick={this.handleClick}
            />
        );
    }
}

//
const mapDispatchToProps = (dispatch) => {
    return {
        changeCustomTagInput: (customTagInput) => dispatch(changeCustomTagInput(customTagInput)),
        addTag: (payload) => dispatch(addTag(payload))
    };
}



const mapStateToProps = (state) => {
    return {
        customTagInput: state.customTagInput,
        movieToDisplay: state.movieToDisplay
    };
};

const AddTagContainer = connect(mapStateToProps, mapDispatchToProps)
    (ConnectedAddTagContainer);

export default AddTagContainer;