

export const QUESTION_TYPE_OPTIONS: { name: string, value: string }[] = [
    { name: "Desplegable", value: "SELECT" },
    { name: "Casillas", value: "CHECKBOX" },
    { name: "Varias Opciones", value: "RADIO" }
];

export const TOKEN_KEY: string = "token";
export const AUTH_HEADER_KEY: string = "Authorization"

export const MAX_QUETIONS_PER_POLL: number = 30;
export const MIN_QUETIONS_PER_POLL: number = 1;

export const MAX_ANSWERS_PER_QUESTION: number = 10;
export const MIN_ANSWERS_PER_QUESTION: number = 1;

export const USER_POLLS_PER_PAGE: number = 2;

export const BASE_URL = window.location.origin.toString();