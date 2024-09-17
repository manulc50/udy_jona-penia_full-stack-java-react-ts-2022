import { useContext } from 'react';
import { Route, Redirect, RouteProps, RouteComponentProps } from 'react-router-dom';
import { AuthStateContext } from '../context/AuthProvider';
import { RouteType } from '../types';


interface AppRouteProps extends RouteProps {
    component: any;
    routeType: RouteType;
}

const AppRoute = (props: AppRouteProps) => {

    const { component: Component, path, routeType, ...rest } = props;

    const user = useContext(AuthStateContext);

    const renderComponent = (routeProps: RouteComponentProps) => {
        switch(routeType) {
            case "PRIVATE":
                return user.isAuthenticated ? <Component { ...routeProps } /> : <Redirect to="/" />;
            case "GUEST":
                return !user.isAuthenticated ? <Component { ...routeProps } /> : <Redirect to="/user" />;
            case "PUBLIC":
                return <Component { ...routeProps } />
        }
    }

    return <Route { ...rest } path={ path } render={ (routeProps: RouteComponentProps) => renderComponent(routeProps) } />;
};


export default AppRoute;