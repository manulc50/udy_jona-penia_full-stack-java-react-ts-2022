import { FC, useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Alert from "react-bootstrap/Alert";
import Spinner from "react-bootstrap/Spinner";
import { Check2Circle } from "react-bootstrap-icons";
import { createPollReply, getPollWithQuestions } from "../../services/pollService";
import { PollReply, PollReplyDetail, Question, UserAnswer } from "../../types";
import ReplyQuestion from "./ReplyQuestion";


interface PollProps {
    id: string
}

const Poll: FC<PollProps> = ({ id }) => {

    const [poll, setPoll] = useState<any>(null);
    const [user, setUser] = useState<string>("");
    const [errors, setErrors] = useState<any>({});
    const [userAnswers, setUserAnswers] = useState<any>({});
    const [isPollAnswerd, setIsPollAnswerd] = useState(false);
    const [sendingData, setSendingData] = useState(false);
    const history = useHistory();

    useEffect(() => {
        fetchPoll();
    }, [ ]);


    const fetchPoll = async () => {
        try {
            const resp = await getPollWithQuestions(id);

            const { data } = resp;

            // Nos aseguramos de que las preguntas estén ordenadas por "questionOrden"
            data.questions = data.questions.sort((q1: Question, q2: Question) => q1.questionOrder - q2.questionOrder);

            setPoll(data);
        }
        catch(errors) {
            history.replace("/");
        }
    };

    const handleQuestionChange = (answer: UserAnswer) => {

        const answers = { ...userAnswers };

        switch(answer.type) {
            case "RADIO":
            case "SELECT":
                // Si el usuario seleccionó la opción "Seleccione una opción" de una pregunta tipo "SELECT", no es una respuesta válida
                if(answer.type === "SELECT" && answers[answer.questionId] && answer.answerId === -1)
                    delete answers[answer.questionId];
                else
                    answers[answer.questionId] = answer.answerId;

                break;
            case "CHECKBOX":
                if(answers[answer.questionId]) {
                    const answersArr = answers[answer.questionId];
                    const index = answersArr.indexOf(answer.answerId);
                    if(index === -1)
                        answersArr.push(answer.answerId);
                    else if(answersArr.length === 1)
                        delete answers[answer.questionId];
                    else
                        answersArr.splice(index, 1);
                }
                else
                    answers[answer.questionId] = [ answer.answerId ];
                break;
        }

        setUserAnswers(answers);
    };

    const renderQuestions = () => poll.questions.map((question: Question) =>
        <ReplyQuestion
            key={ question.id }
            question={ question }
            changeCallback = { handleQuestionChange }
        />
    );

    const submitForm = () => {
        setErrors({});

        const userAnswersKeys = Object.keys(userAnswers);

        if(userAnswersKeys.length !== poll.questions.length) {
            setErrors((current: any) =>
                ({ ...current, allQuestionsAnswered: "Por favor, responda todas las preguntas" }));
        }
        else {
            const pollReplies: PollReplyDetail[] = userAnswersKeys.flatMap(key =>
                Array.isArray(userAnswers[key])
                    ? userAnswers[key].map((userAnswerId: number) => 
                      ({ questionId: parseInt(key), answerId: userAnswerId }))

                    : { questionId: parseInt(key), answerId: userAnswers[key] }
            );

            sendForm(pollReplies);
        }
    };

    const sendForm = async (pollReplies: PollReplyDetail[]): Promise<void> => {
        try {
            setSendingData(true);
            const pollReplay: PollReply = { user, poll: poll.id, pollReplies };
            await createPollReply(pollReplay);
            setIsPollAnswerd(true);
            setSendingData(false);
        }
        catch(err: any) {
            setErrors(err.response.data.errors);
            setSendingData(false);
        }
    };

    return (
        <Container>
            <Row>
                <Col sm="10" md="10" lg="8" className="mx-auto mt-5 mb-5">
                    {
                        isPollAnswerd &&
                            <div className="d-flex align-items-center flex-column poll-answered-container">
                                <Check2Circle className="success-icon" />
                                <Alert show={ isPollAnswerd } variant="success">
                                    Muchas gracias por tu respuesta!
                                </Alert>
                            </div>
                    }
                    {
                        poll && !isPollAnswerd &&
                            <>
                                <h2>{ poll.content }</h2>
                                <hr />
                                <Form.Group className="mb-3" controlId="user">
                                    <Form.Label>Nombre</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={ user }
                                        onChange={ e => setUser(e.target.value) }
                                        placeholder="e.g. Jonathan"
                                        // Es lo mismo que poner la expresión "!!error"
                                        isInvalid={ errors.user ? true : false }
                                    />
                                    <Form.Control.Feedback type="invalid">{ errors.user }</Form.Control.Feedback>
                                </Form.Group>
                                <div>
                                    { renderQuestions() }
                                </div>
                                <Button onClick={ submitForm }>
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
                                                <span>Enviando respuesta...</span>
                                            </>
                                            : 'Responder encuesta'
                                    }
                                </Button>
                                {
                                    errors.allQuestionsAnswered &&
                                        <Alert className="mt-4" variant="danger">{ errors.allQuestionsAnswered }</Alert>
                                }
                            </>
                    }
                </Col>
            </Row>
        </Container>
    );

};


export default Poll;