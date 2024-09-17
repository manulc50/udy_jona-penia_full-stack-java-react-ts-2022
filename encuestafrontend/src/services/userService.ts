import axios from 'axios';
import { REGISTER_ENDPOINT, LOGIN_ENDPOINT } from '../utils/endpoints';


export const registerUser = (name: string, email: string, password: string): Promise<any> => {
    return axios.post(REGISTER_ENDPOINT, { name, email, password });
};

export const loginUser = (email: string, password: string): Promise<any> => {
    return axios.post(LOGIN_ENDPOINT, { email, password });
};

