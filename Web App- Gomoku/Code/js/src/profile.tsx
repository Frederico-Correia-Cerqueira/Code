import React, {useEffect, useContext} from 'react'
import {fetchGet, fetchPost, useFetchGet} from './fetch'
import {AuthContext} from "./App";
import {Link, useNavigate} from "react-router-dom";
import {Stats} from "./types/informationTypes";
import { NavBar } from './elements/navBar';
import '../images/profile_wins.png'
import '../images/profile_defeats.png'
import '../images/profile_statics.png'
import '../images/profile_matches.png'
import { PlayerRank } from './lobby';
import '../css/profile.css';

export function Profile() {
    const auth = useContext(AuthContext)
    const playerID = auth.userID
    const content = useFetchGet({uri: '/api/players/stats/' + playerID});
    const navigate = useNavigate()
    let stats: Stats = undefined
    let token = ""

    function handleLobbyRequest() {
        navigate("/lobby")
    }

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    function clickHandler() {
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
                    <text>{stats.player}</text>
                    <h2>Profile</h2>
                    <a onClick={clickHandler}>Logout</a>
                </div>
                <div className="profile-container">
                    <ProfileStat name="wins" stat={stats.victories} />
                    <ProfileStat name="defeats" stat={stats.defeats} />
                    <PlayerRank rank={stats.rank} elo={stats.elo} />
                    <ProfileStat name="matches" stat={stats.totalGames} />
                    <ProfileStat name="statics" stat={stats.winRate} />
                </div>
                <NavBar middleName="lobby" handleMiddleRequest={handleLobbyRequest} />
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

export function ProfileStat({name, stat}) {
    return (
        <div>
            <img src={`../../images/profile_${name}.png`}></img>
            <h1>{stat}</h1>
        </div>
    )
}
