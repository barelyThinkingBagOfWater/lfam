import React, { Component } from "react";
import { connect } from "react-redux";

import { changeSearchInput } from "../../actions/tagActions";

import SearchInput from "../presentational/SearchInput.jsx";

class ConnectedInputContainer extends Component {
    constructor() {
        super();

        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        const inputValue = event.target.value.trim().toLowerCase();
        
        this.props.changeInput(inputValue);
    }

    render() {
        const { searchInput } = this.props.searchInput;

        return (
            <SearchInput
                text="Welcome fantastic user!"
                label="searchInputLabel"
                type="text"
                id="searchInputId"
                placeholder="Enter a word describing the movie you want to watch"
                inputProps={searchInput}
                handleChange={this.handleChange}
            />
        );
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        changeInput: (searchInput) => { dispatch(changeSearchInput(searchInput)) }
    };
}

const mapStateToProps = (state) => {
    return { searchInput: state };
  };

const InputContainer = connect(mapStateToProps, mapDispatchToProps)
    (ConnectedInputContainer);

export default InputContainer;