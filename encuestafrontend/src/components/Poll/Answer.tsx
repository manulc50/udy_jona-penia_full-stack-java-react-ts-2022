import { FC, useContext } from "react";
import { PollStateContext, PollDispatchContext } from "../../context/PollProvider";
import Form from "react-bootstrap/Form";
import { Circle, Square, Trash } from "react-bootstrap-icons";


interface AnswerProps {
    questionIndex: number,
    answerIndex: number
}

const Answer:FC<AnswerProps> = ({ questionIndex, answerIndex }) => {

    const poll = useContext(PollStateContext);
    const pollDispatch = useContext(PollDispatchContext);

    const question = poll.questions[questionIndex];
    const answer = question.answers[answerIndex];
    const errors: any = poll.errors;
    const errorKey = `questions[${ questionIndex }].answers[${ answerIndex }]`;

    const renderIcon = () => {
        switch(question.type) {
            case "SELECT":
                return <span className="me-1">{ answerIndex + 1 }.</span>;
            case "RADIO":
                return <Circle className="me-1" />;
            case "CHECKBOX":
                return <Square className="me-1" />;
        }
    };

    return (
        <>
            <div className="d-flex align-items-center mb-2 answer-item">
                { renderIcon() }
                <Form.Control
                    type="text"
                    placeholder="Respuesta"
                    value={ answer.content }
                    onChange={ ({ target }: any) =>
                        pollDispatch({ type: "answercontent", payload: { questionIndex, answerIndex, content: target.value } }) }
                    isInvalid={ !!errors[`${ errorKey }.content`] }
                />
                <span data-tip="Eliminar respuesta">
                    <Trash
                        className="ms-1 delete-answer"
                        onClick={ () => pollDispatch({ type: "removeanswer", payload: { questionIndex, answerId: answer.id } }) }
                    />
                </span>
            </div>
            <div className="invalid-feedback d-block mb-2">{ errors[`${ errorKey }.content`] }</div>
        </>
    );
};

export default Answer;