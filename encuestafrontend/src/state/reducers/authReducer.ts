import { AuthActions } from "../actions/AuthActions";
import { User } from "../../types";

export const authInitialState: User = {
    email: '',
    isAuthenticated: false
};

export const authReducer = (state: User, action: AuthActions): User => {
    switch(action.type) {
        case 'login':
            return { email: action.payload, isAuthenticated: true };
        case 'logout':
            return { ...authInitialState };
        default:
            return state;
    }

};