import { FC, useEffect, useState } from "react";
import { RouteComponentProps, useHistory } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import palette from "google-palette";
import { getPollResults } from "../services/pollService";
import { ChartType, PollChartData, PollResult, QuestionResult } from "../types";
import ResultChart from "../components/Results/ResultsChart";

interface RouteParams {
    id: string
}

interface ResultsProps extends RouteComponentProps<RouteParams> {}

const Results: FC<ResultsProps> = props => {

    const pollId = props.match.params.id;

    const [charData, setCharData] = useState<PollChartData[]>([]);
    const [chartType, setChartType] = useState<ChartType>("PIE");
    const [pollTitle, setPollTitle] = useState<string>("");

    const history = useHistory();

    useEffect(() => {
        fetchResults();
    }, []);

    const fetchResults = async () => {
        try {
            const data: PollResult = (await getPollResults(pollId)).data; // Es lo mismo que poner "const { data } : { data: PollResult } = await getPollResults(pollId)"
            setPollTitle(data.content);
            formatData(data.results);
        }
        catch(errors) {
            history.replace("/");
        }
    };

    const formatData = (results: QuestionResult[]) => {
        const pollChartData: PollChartData[] = results.map((result, key) => {
            const charData: PollChartData = {
                title: result.question,
                questionId: key,
                data: {
                    labels: [],
                    datasets: [{ data: [] }]
                }
            };
            
            result.details.forEach(detail => {
                charData.data.labels?.push(detail.answer);
                charData.data.datasets[0].data.push(detail.result);
            });

            charData.data.datasets[0].backgroundColor = palette("cb-Set2", result.details.length)
                .map((color: any) => `#${ color }`);

            return charData;

        });

        setCharData(pollChartData);
    };

    const renderResultsChart = () =>
        charData.map(data => <ResultChart key={ data.questionId } chartData={ data } chartType={ chartType }/>);

    return (
        <Container>
            <Row>
                <Col lg="6" md="10" sm="10" className="mx-auto mt-5">
                    <div className="header">
                        <h4>{ pollTitle }</h4>
                        <hr />
                        <div className="mb-3">
                            <Form.Check
                                inline
                                type="radio"
                                label="Gráfico de tortas"
                                name="chart"
                                id="chart-pie"
                                checked={ chartType === "PIE" }
                                onChange={ () => setChartType("PIE") }
                            />
                            <Form.Check
                                inline
                                type="radio"
                                label="Gráfico de barras"
                                name="chart"
                                id="chart-bar"
                                checked={ chartType === "BAR" }
                                onChange={ () => setChartType("BAR") }
                            />
                        </div>
                    </div>
                    { renderResultsChart() }
                </Col>
            </Row>
        </Container>
    );
};


export default Results;