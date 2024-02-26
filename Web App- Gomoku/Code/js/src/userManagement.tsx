import React, {ChangeEvent, useContext, useState} from 'react'
import {fetchPost} from './fetch'
import {Link, useNavigate} from "react-router-dom";
import {AuthContext} from "./App";
import {Form} from "./elements/form";
import {PlayerInputModel} from "./types/playerTypes";
import '../css/form.css'

export function UserManagement({uri, msg, buttonName, link, linkMessage}: {
    uri: string,
    msg: string,
    buttonName: string,
    link: string,
    linkMessage: string
}) {
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);
    const navigate = useNavigate();
    const auth = useContext(AuthContext)

    function changeHandlerName(event: ChangeEvent<HTMLInputElement>) {
        setName(event.target.value)
    }

    function changeHandlerPassword(event: ChangeEvent<HTMLInputElement>) {
        setPassword(event.target.value)
    }

    function clickHandler() {
        const body: PlayerInputModel = {name: name, password: password}
        fetchPost(uri, body)
            .then(it => {
                const jsonData = JSON.parse(it);
                if (jsonData.properties) {
                    auth.setUserID(jsonData.properties.userID);
                    auth.setLoggedIn(() => true);
                    navigate("/lobby");
                } else {
                    if (jsonData.title == "Internal Server Error") {
                        navigate("/error");
                    }
                    setError(true);
                }
            })
            .catch(() =>
                navigate("/error")
            );
    }


    return (
        <div className="cont">
            <Form
                input1={name}
                label1="Name:"
                input2={password}
                label2="Password:"
                error={error}
                msg={msg}
                changeHandlerInput1={changeHandlerName}
                changeHandlerInput2={changeHandlerPassword}
                clickHandler={clickHandler}
                buttonName={buttonName}/>
            <Link to={link}>{linkMessage}</Link>
        </div>
    );
}

