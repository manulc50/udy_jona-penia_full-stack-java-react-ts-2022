import { useContext } from "react";
import { Link } from 'react-router-dom';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Container from 'react-bootstrap/Container';
import { AuthDispatchContext, AuthStateContext } from "../context/AuthProvider";
import { logout } from "../utils/auth";


const Navigation = () => {

    const user = useContext(AuthStateContext);
    const authDispath = useContext(AuthDispatchContext);

    const handleLogout = () => {
        authDispath({ type: 'logout' });
        logout();
    };

    return (
        <Navbar bg="light" expand="lg">
            <Container>
                <Navbar.Brand as={ Link } to="/">Encuesta</Navbar.Brand>
                <Navbar.Toggle aria-controls="navbar" />
                <Navbar.Collapse id="navbar">
                    <Nav className="me-auto" />
                    <Nav className="justify-content-end">
                        {
                            user.isAuthenticated
                                ?
                                <>
                                    <Nav.Link as={ Link } to="/createpoll">Crear encuesta</Nav.Link>
                                    <NavDropdown id="navbar-dropdown" title={ user.email }>
                                        <NavDropdown.Item as={ Link } to="/user">Mis encuestas</NavDropdown.Item>
                                        <NavDropdown.Divider />
                                        <NavDropdown.Item onClick={ handleLogout }>Cerrar sesión</NavDropdown.Item>
                                    </NavDropdown>
                                </>
                                :
                                <>
                                    <Nav.Link as={ Link } to="/">Iniciar sesión</Nav.Link>
                                    <Nav.Link as={ Link } to="/register">Crear cuenta</Nav.Link>
                                </>
                        }
                        
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};


export default Navigation;