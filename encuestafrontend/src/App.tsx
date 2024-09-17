import { BrowserRouter as Router, Switch } from 'react-router-dom';
import Navigation from './components/Navigation';
import { AuthProvider } from './context/AuthProvider';
import AppRoute from './router/AppRoute';
import routes from './router/routes';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navigation />
        <Switch>
          {
            routes.map(route => 
              <AppRoute
                key={ route.path }
                exact
                component={ route.component }
                path={ route.path }
                routeType={ route.routeType }
              />
            )
          }
        </Switch>
      </Router>
    </AuthProvider>
  );
}

export default App;
