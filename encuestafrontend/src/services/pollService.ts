import axios from 'axios';
import { PollReply } from '../types';
import { CREATE_POLL_ENDPOINT,
         CREATE_POLL_REPLY_ENDPOINT,
         deletePollEndpoint,
         getPollResultsEndpoint,
         getPollWithQuestionsEndpoint,
         getUserPollsEndpoint,
         togglePollOpenedEndpoint } from '../utils/endpoints';


export const savePoll = (data: any): Promise<any> =>
    axios.post(CREATE_POLL_ENDPOINT, data);

export const getPollWithQuestions = (uuid: string): Promise<any> =>
    axios.get(getPollWithQuestionsEndpoint(uuid));

export const createPollReply = (pollReply: PollReply): Promise<any> =>
    axios.post(CREATE_POLL_REPLY_ENDPOINT, pollReply);


export const getUserPolls = (page: number): Promise<any> => 
    axios.get(getUserPollsEndpoint(page));

export const togglePollOpened = (id: string): Promise<any> =>
    axios.patch(togglePollOpenedEndpoint(id));

export const deletePoll = (id: string): Promise<any> =>
    axios.delete(deletePollEndpoint(id));

export const getPollResults = (id: string): Promise<any> =>
    axios.get(getPollResultsEndpoint(id));