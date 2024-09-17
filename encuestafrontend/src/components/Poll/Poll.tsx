import { useContext, useState } from 'react';
import { PollStateContext, PollDispatchContext } from '../../context/PollProvider';
import Question from './Question';
import Form from 'react-bootstrap/Form';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import FloatingLabel from 'react-bootstrap/FloatingLabel';
import Button from 'react-bootstrap/Button';
import Toast from 'react-bootstrap/Toast';
import ToastContainer from 'react-bootstrap/ToastContainer';
import Spinner from 'react-bootstrap/Spinner';
import { v4 as uuid } from 'uuid';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { savePoll } from '../../services/pollService';



const Poll = () => {

    const [showToast, setShowToast] = useState(false);
    const [sendingData, setSendingData] = useState(false);

    const poll = useContext(PollStateContext);
    const pollDispatch = useContext(PollDispatchContext);

    const { errors }: any = poll;

    const renderQuestions = () => {
        return poll.questions.map((question, index) => (
            <Draggable
                key={ question.id }
                draggableId={ question.id }
                index={ index }
            >
                { 
                    provided => (
                        <div ref={ provided.innerRef } { ...provided.draggableProps } { ...provided.dragHandleProps }>
                            <Question index={ index } />
                        </div>
                    )
                }
            </Draggable>
        ));
    };

    const handleOnDragEnd = (result: DropResult) => {
        const { source, destination } = result;
        if(!destination) return; // Si la pregunta no es arrastrada a ningún destino(otra pregunta), no hacemos nada
        if(source.index === destination.index) return; // Si la pregunta es arrastrada a su mismo origen, no hacemos nada
    
        pollDispatch({ type: "orderquestions", payload: { source: source.index, destination: destination.index }});
    };

    const createPoll = async () => {
        const { content, opened, questions } = poll;
        const data = { content, opened, questions };
        
        try {
            setSendingData(true);
            await savePoll(data);
            pollDispatch({ type:"resetformpoll" });
            setShowToast(true);
            setSendingData(false);
        }
        catch(errors: any) {
            pollDispatch({ type: "setErrors", payload: errors.response.data.errors });
            setSendingData(false);
        }
    };

    return (
        <Container className="mt-5 mb-5">
            <Row>
                <Col className="mx-auto" sm="10" md="10" lg="8">
                    <FloatingLabel controlId="poll-content" label="Título de la encuesta">
                        <Form.Control
                            type="text"
                            value={ poll.content }
                            onChange={ ({ target }: any) => pollDispatch({ type: "pollcontent", payload: target.value }) }
                            size="lg"
                            placeholder="Título de la encuesta"
                            isInvalid={ !!errors.content }
                        />
                        <Form.Control.Feedback type="invalid">{ errors.content }</Form.Control.Feedback>
                    </FloatingLabel>

                    <DragDropContext onDragEnd={ handleOnDragEnd }>
                        <Droppable droppableId={ uuid() }>
                            {
                                provided => (
                                    <div ref={ provided.innerRef } { ...provided.droppableProps }>
                                        { renderQuestions() }
                                        { provided.placeholder }
                                    </div>
                                )
                            }
                        </Droppable>
                    </DragDropContext>
                    <Button
                        className="mt-5"
                        size="lg"
                        variant="outline-primary"
                        onClick={ createPoll }
                    >
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
                                    <span>Creando encuesta...</span>
                                </>
                                : 'Crear encuesta'
                        }
                    </Button>
                </Col>
            </Row>
            <ToastContainer position="bottom-center">
                <Toast onClose={ () => setShowToast(false) } show={ showToast } delay={ 5000 } autohide>
                    <Toast.Header closeButton={ false }>
                        <span>La encuesta ha sido creada</span>
                    </Toast.Header>
                    <Toast.Body>Puedes copia el enlace desde el panel</Toast.Body>
                </Toast>
            </ToastContainer>
        </Container>
    );
};

export default Poll;