import Poll from "../components/Poll/Poll";
import { PollProvider } from "../context/PollProvider";


const CreatePoll = () => {
    return (
        <PollProvider>
            <Poll />
        </PollProvider>
    );
};

export default CreatePoll;