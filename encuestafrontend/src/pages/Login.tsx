import { useState, SyntheticEvent, useContext } from 'react';
import { authenticate } from '../utils/auth';
import { loginUser } from '../services/userService';
import { AuthDispatchContext } from '../context/AuthProvider';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Spinner from 'react-bootstrap/Spinner';
import Alert from 'react-bootstrap/Alert';


const Login = () => {

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const [error, setError] = useState("");

    const[sendingData, setSendingData] = useState(false);

    const authDispatch = useContext(AuthDispatchContext);

    const handleInputChange = ({ target }: any): void => {
        setFormData({
            ...formData,
            [target.name]: target.value
        });
    };

    const login = async (e: SyntheticEvent) => {
        e.preventDefault();

        try {
            setSendingData(true);
            setError("");
            const { data } = await loginUser(formData.email, formData.password);
            const { token } = data;
            const email = authenticate(token);
            if(email)
                authDispatch({ type: 'login', payload: email });
        }
        catch(errors: any) {
            setError("Credenciales incorrectas");
            setSendingData(false);
        }
    };

    return (
        <Container>
            <Row>
                <Col lg="5" md="10" sm="10" className="mx-auto">
                    <Card className="mt-5">
                        <Card.Body>
                            <h4>Iniciar sesión</h4>
                            <hr />
                            <Form onSubmit={ login }>
                                <Form.Group className="mb-3" controlId="email">
                                    <Form.Label>Correo electrónico</Form.Label>
                                    <Form.Control
                                        value={ formData.email }
                                        name="email"
                                        onChange={ handleInputChange }
                                        type="email"
                                        placeholder="e.g. john@gmail.com"
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="password">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        value={ formData.password }
                                        name="password"
                                        onChange={ handleInputChange }
                                        type="password"
                                        placeholder="********"
                                    />
                                </Form.Group>
                                <Button type="submit">
                                    {
                                        sendingData
                                            ?
                                            <>
                                                <Spinner
                                                    animation="border"
                                                    as="span"
                                                    size="sm"
                                                    role="status"
                                                    aria-hidden="true"
                                                />&nbsp;
                                                <span>Iniciando sesión...</span>
                                            </>
                                            : 'Iniciar sesión'
                                    }
                                </Button>
                            </Form>
                            <Alert className="mt-4" show={ error ? true : false } variant="danger">{ error }</Alert>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;