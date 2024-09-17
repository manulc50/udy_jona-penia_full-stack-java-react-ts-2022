import { USER_POLLS_PER_PAGE } from "./constants";

const API_URL: string = 'http://localhost:8080';

export const REGISTER_ENDPOINT = API_URL + '/users';
export const LOGIN_ENDPOINT = API_URL + '/users/login';
export const CREATE_POLL_ENDPOINT = API_URL + '/polls';
export const getPollWithQuestionsEndpoint = (uuid: string) => `${ API_URL }/polls/${ uuid }/questions`;
export const CREATE_POLL_REPLY_ENDPOINT = API_URL + '/polls/reply';
export const getUserPollsEndpoint = (page: number) => `${ API_URL }/polls?page=${ page }&limit=${ USER_POLLS_PER_PAGE }`;
export const togglePollOpenedEndpoint = (id: string) => `${ API_URL }/polls/${ id }/opened`;
export const deletePollEndpoint = (id: string) => `${ API_URL }/polls/${ id }`;
export const getPollResultsEndpoint  = (id: string) => `${ API_URL }/polls/${ id }/results`;