import { createContext, Dispatch, FC, ReactNode, useReducer } from "react";
import { authInitialState, authReducer } from "../state/reducers/authReducer";
import { AuthActions } from "../state/actions/AuthActions";
import { User } from "../types";
import { authenticate } from "../utils/auth";


interface AuthProviderProps {
    children: ReactNode
}

export const AuthStateContext = createContext<User>(authInitialState);
export const AuthDispatchContext = createContext<Dispatch<AuthActions>>(() => {});

const init = (): User => {
    const email = authenticate();
    return email ? { email, isAuthenticated: true } : authInitialState;
};

export const AuthProvider:FC<AuthProviderProps> = ({ children }) => {

    const [user , dispatch] = useReducer(authReducer, authInitialState, init);

    return (
        <AuthStateContext.Provider value={ user }>
            <AuthDispatchContext.Provider value={ dispatch }>
                { children }
            </AuthDispatchContext.Provider>
        </AuthStateContext.Provider>
    );
};