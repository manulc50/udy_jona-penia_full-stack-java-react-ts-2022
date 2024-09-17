import { useState, useContext, SyntheticEvent } from 'react';
import { loginUser, registerUser } from '../services/userService';
import { authenticate } from '../utils/auth';
import { AuthDispatchContext } from '../context/AuthProvider';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Spinner from 'react-bootstrap/Spinner';



const Register = () => {

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: ""
    });

    const [errors, setErrors] = useState<any>({});

    const[sendingData, setSendingData] = useState(false);

    const authDispatch = useContext(AuthDispatchContext);

    const handleInputChange = ({ target }: any): void => {
        setFormData({
            ...formData,
            [target.name]: target.value
        });
    };

    const register = async (e: SyntheticEvent) => {
        e.preventDefault();

        try {
            setSendingData(true);
            await registerUser(formData.name, formData.email, formData.password);
            const { data } = await loginUser(formData.email, formData.password);
            const { token } = data;
            const email = authenticate(token);
            if(email)
                authDispatch({ type: 'login', payload: email });
            //authDispatch({ type: 'login', payload: formData.email });
        }
        catch(errors: any) {
            setErrors(errors.response.data.errors);
            setSendingData(false);
        }
    };

    return (
        <Container>
            <Row>
                <Col lg="5" md="10" sm="10" className="mx-auto">
                    <Card className="mt-5">
                        <Card.Body>
                            <h4>Crear cuenta</h4>
                            <hr />
                            <Form onSubmit={ register }>
                                <Form.Group className="mb-3" controlId="name">
                                    <Form.Label>Nombre</Form.Label>
                                    <Form.Control
                                        value={ formData.name }
                                        name="name"
                                        onChange={ handleInputChange }
                                        type="text"
                                        placeholder="e.g. John Doe"
                                        isInvalid={ errors.name ? true : false }
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        { errors.name }
                                    </Form.Control.Feedback>
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="email">
                                    <Form.Label>Correo electrónico</Form.Label>
                                    <Form.Control
                                        value={ formData.email }
                                        name="email"
                                        onChange={ handleInputChange }
                                        type="email"
                                        placeholder="e.g. john@gmail.com"
                                        isInvalid={ !!errors.email }
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        { errors.email }
                                    </Form.Control.Feedback>
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="password">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        value={ formData.password }
                                        name="password"
                                        onChange={ handleInputChange }
                                        type="password"
                                        placeholder="********"
                                        isInvalid={ !!errors.password }
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        { errors.password }
                                    </Form.Control.Feedback>
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
                                                <span>Creando usuario...</span>
                                            </>
                                            : 'Crear cuenta'
                                    }
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Register;