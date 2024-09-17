import { createContext, Dispatch, FC, ReactNode, useReducer } from "react";
import { PollActions } from "../state/actions/PollActions";
import { pollInitialState, pollReducer } from "../state/reducers/pollReducer";
import { Poll } from "../types";

interface PollProviderProps {
    children: ReactNode
}

export const PollStateContext = createContext<Poll>(pollInitialState);
export const PollDispatchContext = createContext<Dispatch<PollActions>>(() => {});

export const PollProvider:FC<PollProviderProps> = ({ children }) => {

    const [poll , dispatch] = useReducer(pollReducer, pollInitialState);

    return (
        <PollStateContext.Provider value={ poll }>
            <PollDispatchContext.Provider value={ dispatch }>
                { children }
            </PollDispatchContext.Provider>
        </PollStateContext.Provider>
    );
};