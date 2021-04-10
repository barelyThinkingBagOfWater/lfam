import { combineEpics } from 'redux-observable';
import { catchError } from 'rxjs/operators';

import { fetchTagsEpic, fetchMoviesEpic, addTagEpic, fetchRatingEpic, addRatingEpic }
from './MyEpics.jsx';


export const rootEpic = (action$, store$, dependencies) =>
    combineEpics(fetchTagsEpic, fetchMoviesEpic, addTagEpic, addRatingEpic, fetchRatingEpic)
    (action$, store$, dependencies).pipe(
      catchError((error, source) => { //now using lazy global error handling as fallback
        console.error(error);
        return source;
      })
    );  