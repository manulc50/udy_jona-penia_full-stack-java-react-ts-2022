import { ChartData } from "chart.js";

export type User = {
    email: string,
    isAuthenticated: boolean
};

export type Answer = {
    id: string,
    content: string,
};

export type QuestionType = 'RADIO' | 'CHECKBOX' | 'SELECT';

export type Question = {
    id: string,
    content: string,
    questionOrder: number,
    type: QuestionType,
    answers: Answer[]
};

export type Poll = {
    id: string,
    errors: {},
    content: string,
    opened: boolean,
    questions: Question[]
 };

export type RouteType = "PRIVATE" | "PUBLIC" | "GUEST";

export type Route = {
    path: string,
    component: any,
    routeType: RouteType
};

export type UserAnswer = {
    questionId: number,
    answerId: number,
    type: QuestionType
};

export type PollReplyDetail = {
    questionId: number,
    answerId: number,
};

export type PollReply = {
    user: string,
    poll: number,
    pollReplies: PollReplyDetail[]
};

export type DetailResult = {
    answer: string,
    result: number
};

export type QuestionResult = {
    question: string,
    details: DetailResult[]
};

export type PollResult = {
    id: number,
    content: string,
    results: QuestionResult[];
};

export type PollChartData = {
    title: string,
    questionId: number,
    data: ChartData
};

export type ChartType = "PIE" | "BAR";