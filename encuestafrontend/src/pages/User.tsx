import { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import ReactTooltip from "react-tooltip";
import { List, Share, Trash } from "react-bootstrap-icons";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Table from "react-bootstrap/Table";
import ToastContainer from "react-bootstrap/ToastContainer";
import Toast from "react-bootstrap/Toast";
import Button from "react-bootstrap/Button";
import ReactPaginate from "react-paginate";
import copy from "copy-to-clipboard";
import { confirmAlert } from "react-confirm-alert";
import { deletePoll, getUserPolls, togglePollOpened } from "../services/pollService";
import Switch from "../components/UI/Switch";
import { BASE_URL } from "../utils/constants";


const User = () => {

    const [polls, setPolls] = useState<any>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalRecords, setTotalRecords] = useState(0);
    const [showToast, setShowToast] = useState(false);

    const history = useHistory();

    useEffect(() => {
        fetchPolls();
    }, [ currentPage ]);

    const fetchPolls = async () => {
        const { data } = await getUserPolls(currentPage);
        setPolls(data.polls);
        setTotalPages(data.totalPages);
        setTotalRecords(data.totalRecords);
        ReactTooltip.rebuild();
    };

    const handlePollToggle = async (id: number) => {
        const _polls = [ ...polls ];
        const poll = _polls.find(p => p.id === id);
        poll.opened = !poll.opened;
        setPolls(_polls);
        await togglePollOpened(poll.pollId);
    };

    const handlePageChange = (selectedItem: { selected: number }) => setCurrentPage(selectedItem.selected);

    const handleDeletePoll = (id: string) => {
        confirmAlert({
            customUI: ({ onClose }) => {
                return (
                    <div className="custom-ui">
                        <h2>Eliminar encuesta</h2>
                        <p>¿Quieres eliminar esta encuesta?</p>
                        <Button
                            variant="outline-primary"
                            size="sm"
                            className="me-2"
                            onClick={ async () => {
                                await deletePoll(id);
                                currentPage === 0 ? fetchPolls() : setCurrentPage(0);
                                onClose();
                            } }
                        >
                            Sí, eliminar!
                        </Button>
                        <Button variant="outline-primary" size="sm" onClick={ onClose }>No</Button>
                    </div>
                );
            }
        });
    }; 

    const renderTable = () => {
        return (
            <Table striped className="mt-4 polls-table"  bordered hover responsive>
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Recibir más respuestas</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        polls.map((poll: any) => 
                            <tr key={ poll.id }>
                                <td>{ poll.content }</td>
                                <td>
                                    <Switch
                                        label={ poll.opened ? "Activada" : "Desactivada" }
                                        checked={ poll.opened }
                                        id={ poll.pollId }
                                        onChange={ () => handlePollToggle(poll.id) }
                                    />
                                </td>
                                <td className="polls-table-controls">
                                    <span
                                        data-tip="Compartir encuesta"
                                        onClick={ () => {
                                            copy(`${ BASE_URL }/replypoll/${ poll.pollId }`);
                                            setShowToast(true);
                                        } }
                                    >
                                        <Share />
                                    </span>
                                    <span
                                        data-tip="Ver resultados"
                                        onClick={ () => history.push(`/results/${ poll.pollId }`) }
                                    >
                                        <List />
                                    </span>
                                    <span
                                        data-tip="Eliminar encuesta"
                                        onClick={ () => handleDeletePoll(poll.pollId) }
                                    >
                                        <Trash />
                                    </span>
                                </td>
                            </tr>
                        )
                    }
                </tbody>
            </Table>
        );
    };

    return (
        <Container className="mt-5">
            <Row>
                <Col sm="10" md="10" lg="8" className="mx-auto">
                    <h5>Mis encuestas</h5>
                    {
                        totalRecords > 0 && polls
                        ?
                            <>
                                { renderTable() }
                                <ReactPaginate
                                    pageCount={ totalPages }
                                    forcePage={ currentPage }
                                    marginPagesDisplayed={ 2 } // 2 botones a la izquierda
                                    pageRangeDisplayed={ 2 } // 2 botones a la derecha
                                    previousLabel="Anterior"
                                    nextLabel="Siguiente"
                                    containerClassName="pagination justify-content-end"
                                    previousClassName="page-item"
                                    previousLinkClassName="page-link"
                                    nextClassName="page-item"
                                    nextLinkClassName="page-link"
                                    pageClassName="page-item"
                                    pageLinkClassName="page-link"
                                    activeClassName="active"
                                    breakLabel="..." // Label cuando hay muchas páginas
                                    onPageChange={ handlePageChange }
                                />
                                <ReactTooltip place="top" effect="solid" />
                                <ToastContainer position="bottom-center">
                                    <Toast show={ showToast } delay={ 5000 } autohide onClose={ () =>setShowToast(false) }>
                                        <Toast.Header closeButton={ false }>Compartido!</Toast.Header>
                                        <Toast.Body>Enlace copiado al portapapeles</Toast.Body>
                                    </Toast>
                                </ToastContainer>
                            </>
                        :
                        <span className="d-block mt-5">
                            No tienes encuestas creadas, <Link to="/createpoll">comienza</Link> a crear una de ellas
                        </span>
                    }
                </Col>
            </Row>
        </Container>
    );
};


export default User;