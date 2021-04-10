import { createStore, applyMiddleware, compose } from "redux";
import { createEpicMiddleware } from 'redux-observable';
import {composeWithDevTools} from 'redux-devtools-extension/developmentOnly';

import rootReducer from "../reducers/index";
import { rootEpic } from "../epics/index";


const epicMiddleware = createEpicMiddleware();

const store = createStore(rootReducer,
    composeWithDevTools(applyMiddleware(epicMiddleware)));

epicMiddleware.run(rootEpic);


export default store;