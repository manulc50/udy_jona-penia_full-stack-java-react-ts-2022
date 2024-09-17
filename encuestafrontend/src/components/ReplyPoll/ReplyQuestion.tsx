import { FC } from "react";
import Form from "react-bootstrap/Form";
import { Question, Answer, UserAnswer } from "../../types";


interface ReplyQuestionProps {
    question: Question,
    changeCallback: Function
}

const ReplyQuestion: FC<ReplyQuestionProps> = ({ question, changeCallback }) => {

    const handleChange = ({ target }: any): void => {
        const answer: UserAnswer = {
            questionId: parseInt(question.id),
            answerId: parseInt(target.value),
            type: question.type
        };

        changeCallback(answer);
    };

    const renderAnswers = () => {
        switch(question.type) {
            case "RADIO":
            case "CHECKBOX":
                return question.answers.map((answer: Answer) => (
                    <div key={ answer.id } className="mb-2">
                        <Form.Check
                            type={ question.type === "RADIO" ? "radio" : "checkbox" }
                            value={ answer.id }
                            name={ question.id }
                            id={ answer.id }
                            label={ answer.content }
                            onChange={ handleChange }
                        />
                    </div>
                ));
            case "SELECT":
                return (
                    <div className="mb-2">
                        <Form.Control
                            as="select"
                            className="form-select"
                            onChange={ handleChange }
                        >
                            <option value="-1">Seleccione una opción</option>
                            {
                                question.answers.map((answer: Answer) =>
                                    <option key={ answer.id } value={ answer.id }>{ answer.content }</option>)
                            }
                        </Form.Control>
                    </div>
                );
        }
    };

    return (
        <div className="mb-4">
            <h6>{ question.content }</h6>
            { renderAnswers() }
        </div>
    );
};

export default ReplyQuestion;