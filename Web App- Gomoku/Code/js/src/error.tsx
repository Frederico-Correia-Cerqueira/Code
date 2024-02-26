import React from "react";
import {Link} from "react-router-dom";

export function Error() {
    return (
        <div>
            <h1>Something went wrong</h1>
            <p>Try to refresh the page</p>
            <Link to="/"> «- Home </Link>
        </div>
    )
}