import { Answer, Poll, Question } from "../../types";
import { v4 as uuid } from 'uuid';
import { PollActions } from "../actions/PollActions";
import { MAX_ANSWERS_PER_QUESTION, MAX_QUETIONS_PER_POLL, MIN_ANSWERS_PER_QUESTION, MIN_QUETIONS_PER_POLL } from "../../utils/constants";


const defaultAnswer: Answer = {
    id: uuid(),
    content: ''
};

const defaultQuestion: Question = {
    id: uuid(),
    content: '',
    questionOrder: 1,
    type: "RADIO",
    answers: [ defaultAnswer ]
};

const defaultPoll: Poll = {
    id: uuid(),
    content: '',
    errors: {},
    opened: true,
    questions: [ defaultQuestion ]
};

export const pollInitialState: Poll = { ...defaultPoll };

const orderQuestions = (questions: Question[]): void => questions.forEach((question, i) => question.questionOrder = i + 1);

// Nota: El trabajo para actualizar un state complejo(profundo y con arrays) puede resultar muy tedioso tal y como se puede ver en este caso
// Para facilitar el trabajo en este tipo de casos, una opción es usar la librería Immer que permite realizar actualizaciones inmutables de estados

export const pollReducer = (state: Poll, action: PollActions): Poll => {
    switch(action.type) {
        case "pollcontent":
            return { ...state, content: action.payload };
        case "questioncontent": {
            const { index, content } = action.payload;

            return {
                ...state,
                questions: [
                    ...state.questions.slice(0, index),
                    {
                        ...state.questions[index],
                        content
                    },
                    ...state.questions.slice(index + 1)
                ]
            };
        }
        case "changequestiontype": {
            const { index: questionIndex, value: type } = action.payload;
            const questions = state.questions.map((question,index) =>
                (index !== questionIndex) ? question : { ...question, type } as Question);

            return {
                ...state,
                questions 
            };
        }
        case "answercontent": {
            const { questionIndex, answerIndex, content } = action.payload;

            return {
                ...state,
                questions: [
                    ...state.questions.slice(0, questionIndex),
                    {
                        ...state.questions[questionIndex],
                        answers: [
                            ...state.questions[questionIndex].answers.slice(0, answerIndex),
                            {
                                ...state.questions[questionIndex].answers[answerIndex],
                                content 
                            },
                            ...state.questions[questionIndex].answers.slice(answerIndex + 1)
                        ]
                    },
                    ...state.questions.slice(questionIndex + 1)
                ]
            };
        }
        case "newquestion": {
            const questions  = state.questions.slice();
            const numQuestions = questions.length;

            if(numQuestions === MAX_QUETIONS_PER_POLL)
                return state;

            const { payload: index } = action;

            questions.splice(index + 1, 0, { ...defaultQuestion, id: uuid() });

            orderQuestions(questions);

            return {
                ...state,
                questions
            };
        }
        case "newanswer": {
            const { payload: questionIndex } = action;
            const question = state.questions[questionIndex];
            const numAnswers = question.answers.length;

            if(numAnswers === MAX_ANSWERS_PER_QUESTION)
                return state;

            return {
                ...state,
                questions: [
                    ...state.questions.slice(0, questionIndex),
                    {
                        ...state.questions[questionIndex],
                        answers: [
                            ...state.questions[questionIndex].answers.slice(0, numAnswers),
                            { ...defaultAnswer, id: uuid() }
                        ]
                    },
                    ...state.questions.slice(questionIndex + 1)
                ]
            };
        }
        case "removeanswer": {
            const { questionIndex, answerId } = action.payload;
            const question = state.questions[questionIndex];
            const numAnswers = question.answers.length;

            if(numAnswers === MIN_ANSWERS_PER_QUESTION)
                return state;

            const questions = state.questions.map((question, qIndex) => (qIndex !== questionIndex) 
                ? question
                : { ...question, answers: question.answers.filter(answer => answer.id !== answerId ) });

            return {
                ...state,
                errors: {},
                questions
            };
        }
        case "removequestion": {
            const { payload: questionId } = action;
            const numQuestions = state.questions.length;

            if(numQuestions === MIN_QUETIONS_PER_POLL)
                return state;

            return {
                ...state,
                errors: {},
                questions: state.questions.filter(question => question.id !== questionId)
            };
        }
        case "orderquestions": {
            const { source, destination } = action.payload;

            const questions = state.questions.slice();
            const [removed] = questions.splice(source, 1);
            questions.splice(destination, 0, removed);

            orderQuestions(questions);

            return {
                ...state,
                errors: {},
                questions 
            };
        }
        case "setErrors": {
            const { payload: errors } = action;
            
            return {
                ...state,
                errors
            };
        }
        case "resetformpoll":
            return { ...defaultPoll };
        default:
            return state;
    }
};