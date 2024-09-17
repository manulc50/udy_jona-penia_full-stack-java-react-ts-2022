
// "payload" es el email del usuario 
export type AuthActions = { type: 'login', payload: string } | { type: 'logout' };