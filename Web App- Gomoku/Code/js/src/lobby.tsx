import React, {useState, useContext, useEffect} from 'react'
import {fetchGet, fetchPost, useFetchGet} from './fetch'
import {AuthContext} from "./App";
import {useNavigate} from "react-router-dom";
import {GameInputModel} from "./types/gameTypes";
import '../css/navbar.css';
import '../css/lobby.css';
import { NavBar, NavBarTop } from './elements/navBar';
import '../images/unranked.png'
import '../images/pro.png'
import '../images/intermediate.png'
import '../images/expert.png'
import '../images/noob.png'
import '../images/beginner.png'

function getRankGoal(rank: string) {
    const rankGoals = {
        UNRANKED: 50,
        NOOB: 100,
        BEGINNER: 300,
        INTERMEDIATE: 500,
        EXPERT: 1000,
        PRO: 5000
    };

    // Return the goal associated with the given rank, or a default message if not found.
    return rankGoals[rank];
}

function getRankImage(rank: string) {
    const rankGoals = {
        UNRANKED: '../images/unranked.png',
        NOOB: '../images/noob.png',
        BEGINNER: '../images/beginner.png',
        INTERMEDIATE: '../images/intermediate.png',
        EXPERT: '../images/expert.png',
        PRO: '../images/pro.png'
    };

    // Return the goal associated with the given rank, or a default message if not found.
    return rankGoals[rank];
}

export function Lobby() {
    const navigate = useNavigate()
    const auth = useContext(AuthContext)
    const content = useFetchGet({uri: '/api/players/stats/' + auth.userID})

    const [variant, setVariant] = useState<String>("NORMAL")
    const [openingRule, setOpeningRule] = useState<String>("NORMAL")
    const [selected, setSelected] = useState<String>("NORMAL")
    let stats = undefined
    let token = ""

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    function handleGameChange(mode: String) {
        if (mode === "SWAP") {
            setVariant("NORMAL")
            setOpeningRule(mode)
        } else {
            setVariant(mode)
            setOpeningRule("NORMAL")
        }
        setSelected(mode)
    }

    function handlePlayRequest() {
        const gameInputModel: GameInputModel = {playerID: auth.userID, gameType: variant, openingRules: openingRule}
        fetchPost('api/matchmaking', gameInputModel)
            .then(() => {
                navigate("/waitingRoom")
            }).catch(() =>
            navigate("/error")
        );
    }

    function handleLogoutRequest() {
        fetchGet(`api/getAuthCookie`)
            .then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text);
                    if (jsonData.properties != undefined) {
                        token = jsonData.properties.token
                    }
                }
            })
            .then(() => {
                fetchPost(`api/logout`, {token})
                    .then(() => {
                        auth.setUserID(-1)
                        auth.setLoggedIn(() => false)
                    })
                    .then(() => {
                        navigate("/")
                    })
            })
    }


    if (content !== undefined) {
        const jsonData = JSON.parse(content);
        if (jsonData.properties) {
            stats = jsonData.properties;
        }
        return (
            <div>
                <div className="navbar-top">
                    
                    
                    
                </div>
                <div className="navbar-top">
                    <text>{stats.player}</text>
                    <h2>Lobby</h2>
                    <a onClick={handleLogoutRequest}>Logout</a>
                </div>
                <div className="lobby-container">
                    <PlayerRank rank={stats.rank} elo={stats.elo} />
                    <h2>Choose a game mode</h2>
                    <div className='gameModes'>
                        <button 
                            className={selected === "NORMAL" ? 'selected' : ''}
                            onClick={() => handleGameChange("NORMAL")}>Normal
                        </button>
                        <button 
                            className={selected === "SWAP_FIRST_MOVE" ? 'selected' : ''}
                            onClick={() => handleGameChange("SWAP_FIRST_MOVE")}>Swap First Move
                        </button>
                        <button 
                            className={selected === "SWAP" ? 'selected' : ''}
                            onClick={() => handleGameChange("SWAP")}>Swap
                        </button>
                    </div>
                </div>
                <NavBar middleName="play" handleMiddleRequest={handlePlayRequest} />
            </div>
        );
    }

    return (
        <div>
            <h1>
                Loading...
            </h1>
        </div>
    );
}


export function PlayerRank({rank, elo}) {
    let imgSrc = getRankImage(rank)
    let goal = getRankGoal(rank)
    return (
        <div className='show-rank'>
            <img src={imgSrc}></img>
            <p>{rank}</p>
            <p>{elo}/{goal}</p>
        </div>
    )
}