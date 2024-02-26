import React, {useState, useEffect, useContext} from 'react'
import {useNavigate} from "react-router-dom";
import {AuthContext} from "./App";
import { useTimer } from './elements/count';
import '../images/waiting_room_gif.gif';
import '../css/waitingRoom.css';

export function WaitingRoom() {
    const auth = useContext(AuthContext)
    const navigate = useNavigate();
    const userID = auth.userID
    let gameID = auth.gameID
    const uri = `/api/players/${userID}`
    const [counter, formattedTime, start, reset, pause] = useTimer(0);

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    const [state, setState] = useState("WAITING")
    useEffect(() => {
        const tid = setInterval(() => {
            fetch(uri)
                .then((data) => data.json()) // Parse JSON response
                .then((data) => {
                    const userState = data.properties.state
                    setState(userState)
                })
        }, 3000)
        return () => clearInterval(tid)
    }, [state])

    if (state == "IN_GAME") {
        fetch(`api/players/getGameID/${userID}`)
            .then((data) => data.json())
            .then((data) => {
                gameID = data.properties.id
                auth.setGameID(gameID)
            })
            .then(() => {
                    fetch(`api/games/${gameID}`)
                        .then((data) => data.json())
                        .then((data) => {
                            let opponentID: number
                            if (data.properties.playerB == userID) {
                                opponentID = data.properties.playerW
                            } else {
                                opponentID = data.properties.playerB
                            }
                            auth.setOpponentID(opponentID)
                            navigate("/game")
                        })
                }
            )

    }

    return (
        <div>
            <div className="waitingRoom-container">
                <h2>{formattedTime}</h2>
                <img src='../images/waiting_room_gif.gif' />
                <p> It looks like your opponents are afraid to face you.... </p>
            </div>
            
            
        </div>
    )
}
