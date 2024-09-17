import { AUTH_HEADER_KEY, TOKEN_KEY } from "./constants";
import jwt_decoded from 'jwt-decode';
import axios from "axios";


const setToken = (token: string): void => localStorage.setItem(TOKEN_KEY, token);

const getToken = (): string => localStorage.getItem(TOKEN_KEY) || '';

const removeToken = (): void => localStorage.removeItem(TOKEN_KEY);


// Si se pasa el token, significa que se acaba de hacer login
export const authenticate = (token?: string): string | null => {
    if(token)
        setToken(token);
    
    const _token = token ? token : getToken();

    if(!_token)
        return null;

    const decodedToken: any = jwt_decoded(_token);
    const { sub: email, exp } = decodedToken;
    const currentTime = Date.now() / 1000;

    if(exp < currentTime) {
        removeToken();
        return null;
    }

    axios.defaults.headers.common[AUTH_HEADER_KEY] = _token;

    return email;
};

export const logout = (): void => {
    delete axios.defaults.headers.common[AUTH_HEADER_KEY];
    removeToken();
};