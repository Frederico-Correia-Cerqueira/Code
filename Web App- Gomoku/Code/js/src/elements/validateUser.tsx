import React, {useContext} from "react";
import {AuthContext} from "../App";
import {Link} from "react-router-dom";

export function ValidateUser() {
    const auth = useContext(AuthContext)
    if (!auth.loggedIn) {
        return (
            <div>
                <Link to="/"> «- Home </Link>
            </div>
        )
    } else {
        return (
            <div>
                <Link to="/lobby"> «- Lobby </Link>
            </div>
        )
    }
}

export function isUserLogged() {
    const auth = useContext(AuthContext)
    return auth.loggedIn
}
