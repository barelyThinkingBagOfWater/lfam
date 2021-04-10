import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom";

import SearchInputContainer from "./container/SearchInputContainer.jsx"
import TagsListContainer from "./container/TagsListContainer.jsx"
import MoviesListContainer from "./container/MoviesListContainer.jsx"
import MovieContainer from "./container/MovieContainer.jsx"
import MonitoringContainer from "./container/MonitoringContainer.jsx";
import SourcesContainer from "./container/SourcesContainer.jsx";
import HeaderBar from "./presentational/HeaderBar.jsx"

import "../../css/App.css";
import "animate.css"


const App = () => {
  return (
    <div className="wrapper">
      <Router>
        <HeaderBar />
        <div className="main-body">
          <Switch>
            <Route path="/monitoring">
              <MonitoringContainer />
            </Route>
            <Route path="/sources">
              <SourcesContainer />
            </Route>
            <Route path="/">
              <SearchInputContainer />
              <div className="body-containers">
                <TagsListContainer />
                <MoviesListContainer />
                <MovieContainer />
              </div>
            </Route>
          </Switch>
        </div>
      </Router>
    </div>
  )
}

export default App