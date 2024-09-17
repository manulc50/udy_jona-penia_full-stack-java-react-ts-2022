import { EmojiFrown } from "react-bootstrap-icons";


const NotFound = () => {
    return (
        <div className="page-not-found mt-5 container d-flex justify-content-center">
            <EmojiFrown className="not-found-icon" />
            <p className="ms-2">404 - Página no encontrada</p>
        </div>
    );
};

export default NotFound;