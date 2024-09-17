
export type PollActions = 
    { type: 'pollcontent', payload: string } | // "payload" es el contenido de la encuesta
    { type: 'questioncontent', payload: { index: number, content: string } } |
    { type: 'answercontent', payload: { questionIndex: number, answerIndex: number ,content: string } } |
    { type: 'changequestiontype', payload: { index: number, value: string } } |
    { type: 'newquestion', payload: number } | // "payload" es el índice de la pregunta
    { type: 'newanswer', payload: number } | // "payload" es el índice de la pregunta
    { type: 'removequestion', payload: string } | // "payload" es el id de la pregunta
    { type: 'removeanswer', payload: { questionIndex: number, answerId: string } } |
    { type: 'orderquestions', payload: { source: number, destination: number } } |
    { type: 'setErrors', payload: any } | // "payload" es un objeto con los errores
    { type: 'resetformpoll' };