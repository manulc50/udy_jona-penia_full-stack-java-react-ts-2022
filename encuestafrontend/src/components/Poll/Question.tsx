import { FC, useContext, useEffect } from "react";
import { PollStateContext, PollDispatchContext } from "../../context/PollProvider";
import { QUESTION_TYPE_OPTIONS } from "../../utils/constants";
import Answer from "./Answer";
import ReactTooltip from "react-tooltip";
import Card from "react-bootstrap/Card";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Container from "react-bootstrap/Container";
import Button from 'react-bootstrap/Button';
import { PlusCircle, PlusLg, Trash } from "react-bootstrap-icons";


interface QuestionProps {
    index: number
}

const Question:FC<QuestionProps> = ({ index }) => {

    const poll = useContext(PollStateContext);
    const pollDispatch = useContext(PollDispatchContext);

    const question = poll.questions[index];
    const errors: any = poll.errors;
    const errorKey = `questions[${ index }]`;

    useEffect(() => {
        ReactTooltip.rebuild();
     }, [question.answers.length]);

    const renderAnswers = () => {
        return question.answers.map((answer, answerIndex) =>
            <Answer key={ answer.id } questionIndex={ index } answerIndex={ answerIndex }/>);
    };

    return (
        <Card className="mt-3">
            <Card.Body>
                <Row>
                    <Col className="mb-4" sm="12" md="6">
                        <Form.Control
                            type="text"
                            placeholder="Pregunta"
                            value={ question.content }
                            onChange={ ({ target }: any) =>
                                pollDispatch({ type: "questioncontent", payload: { index, content: target.value } }) }
                            isInvalid={ !!errors[`${ errorKey }.content`]}
                        />
                        <Form.Control.Feedback type="invalid">{ errors[`${ errorKey }.content`] }</Form.Control.Feedback>
                    </Col>
                    <Col className="mb-4" sm="12" md="6">
                        <Form.Control
                            as="select"
                            className="form-select"
                            value={ question.type }
                            onChange={ ({ target }: any) =>
                                pollDispatch({ type: "changequestiontype", payload: { index, value: target.value } }) }
                            isInvalid={ !!errors[`${ errorKey }.type`]}
                        >
                            <option value="">Tipo de pregunta</option>
                            {
                                QUESTION_TYPE_OPTIONS.map(option =>
                                    <option key={ option.value } value={ option.value }>{ option.name }</option>)
                            }
                        </Form.Control>
                        <Form.Control.Feedback type="invalid">{ errors[`${ errorKey }.type`] }</Form.Control.Feedback>
                    </Col>
                </Row>
                <Container>
                    { renderAnswers() }

                    <Button
                        className="mt-2"
                        size="sm"
                        variant="outline-primary"
                        onClick={ () => pollDispatch({ type:"newanswer", payload: index }) }
                    >
                        <PlusLg /> Añadir respuesta
                    </Button>
                </Container>
                <hr />
                <div className="d-flex justify-content-end">
                <span data-tip="Añadir pregunta">
                        <PlusCircle
                            className="option-question-icon ms-1"
                            onClick={ () => pollDispatch({ type: "newquestion", payload: index }) }
                        />
                    </span>
                    <span data-tip="Eliminar pregunta">
                        <Trash
                            className="option-question-icon ms-1"
                            onClick={ () => pollDispatch({ type: "removequestion", payload: question.id }) }
                        />
                    </span>
                </div>
                <ReactTooltip place="left" effect="solid" />
            </Card.Body>
        </Card>
    );
};

export default Question;