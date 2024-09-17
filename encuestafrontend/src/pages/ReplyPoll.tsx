import { FC } from "react";
import { RouteComponentProps } from "react-router-dom";
import Poll from "../components/ReplyPoll/Poll";

interface RouteParams {
    id: string
}

interface ReplyPollProps extends RouteComponentProps<RouteParams> {

}

const ReplyPoll: FC<ReplyPollProps> = props => {

    const pollUUID = props.match.params.id;

    return <Poll id={ pollUUID } />;
};


export default ReplyPoll;