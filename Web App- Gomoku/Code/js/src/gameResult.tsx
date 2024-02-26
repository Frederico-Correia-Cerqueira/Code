import React, {ChangeEvent, useContext, useEffect, useState} from 'react'
import {Link, useNavigate} from "react-router-dom";
import {AuthContext} from "./App";
import {fetchPost} from './fetch';
import {Form} from './elements/form';
import '../images/victory_gif.gif';
import {NavBar, NavBarTop} from './elements/navBar';
import '../css/gameResult.css'


export function GameResult({message, imageUrl}: { message: string, imageUrl: string }) {
    const auth = useContext(AuthContext)
    const navigate = useNavigate()
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    function changeHandlerInput1(event: ChangeEvent<HTMLInputElement>) {
        setName(event.target.value)
    }

    function changeHandlerInput2(event: ChangeEvent<HTMLInputElement>) {
        setDescription(event.target.value)
    }

    function handlerHomeClick() {
            fetchPost('api/games/' + auth.gameID + '/savegame/' + auth.userID,
                {name: name, description: description}).catch(() =>
                navigate("/error")
            );
            navigate("/lobby")
    }

    function handlerLobbyRequest() {
        navigate("/lobby")
    }

    return (
        <div>
            <div className="navbar-top">
                    <h2 id="hide"> AAAAA </h2>
                    <h2>Game Result</h2>
                    <h2 id="hide"> AAAAA </h2>
                </div>
            <div className="gameResult-container">
            <img src={imageUrl} />
     
            <p> {message} </p>
            <div className='cont'>
                <Form input1={name} label1="Name:" input2={description} label2="Description:" changeHandlerInput1={changeHandlerInput1} changeHandlerInput2={changeHandlerInput2} error={false} msg="" buttonName="Save Game"
                               clickHandler={handlerHomeClick} secretInput={false}/>
            </div>
                
            </div>
            <NavBar middleName="lobby" handleMiddleRequest={handlerLobbyRequest} />
        </div>
    )

}

