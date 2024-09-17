import CreatePoll from "../pages/CreatePoll";
import Login from "../pages/Login";
import NotFound from "../pages/NotFound";
import Register from "../pages/Register";
import ReplyPoll from "../pages/ReplyPoll";
import Results from "../pages/Results";
import User from "../pages/User";
import { Route } from "../types";


const routes: Route[] = [
    {
        path: '/',
        component: Login,
        routeType: "GUEST"
    },
    {
        path: '/register',
        component: Register,
        routeType: "GUEST"
    },
    {
        path: '/user',
        component: User,
        routeType: "PRIVATE"
    },
    {
        path: '/createpoll',
        component: CreatePoll,
        routeType: "PRIVATE"
    },
    {
        path: '/replypoll/:id',
        component: ReplyPoll,
        routeType: "PUBLIC"
    },
    {
        path: '/results/:id',
        component: Results,
        routeType: "PRIVATE"
    },
    {
        path: '*',
        component: NotFound,
        routeType: "PUBLIC"
    }
];

export default routes;