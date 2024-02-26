import React, {useState} from 'react'
import {useFetchGet} from './fetch'
import {useNavigate, Link} from "react-router-dom";
import {isUserLogged} from "./elements/validateUser";
import '../css/ranking.css'
import { NavBar, NavBarTop } from './elements/navBar';
import '../images/unranked.png'

export function Ranking() {
    const content = useFetchGet({uri: 'api/stats'})
    const navigate = useNavigate();
    const [rank, setRank] = useState<String>("")
    let stats = undefined

    function handleRankChange(rank: String) {
        setRank(rank)
    }

    function handleLobbyRequest() {
        navigate("/lobby")
    }

    if (content !== undefined) {
        const jsonData = JSON.parse(content)
        if (jsonData.properties) {
            stats = jsonData.properties.stats
        }
        if (jsonData.title == "Internal Server Error") {
            navigate("/error")
        }
    }

    return (
        <div>

            <div className="navbar-top">
                    {!isUserLogged() && <Link className='start' to='/'>Home</Link>}
                    {isUserLogged() && <h2 id="hide"> AAAA </h2>}
                    <h2 className='center'>Ranking</h2>
                    <h2 id="hide"> AAAAA </h2>
                </div>
            <div className="ranking-container">
          
                    <h2>Filter by Rank</h2>
                    <div className="ranks">
                        <RankSearchButton onClick={() => handleRankChange(rank === "UNRANKED" ? "" : "UNRANKED")} rank="unranked" selected={rank === "UNRANKED"}/>
                        <RankSearchButton onClick={() => handleRankChange(rank === "NOOB" ? "" : "NOOB")} rank="noob" selected={rank === "NOOB"} />
                        <RankSearchButton onClick={() => handleRankChange(rank === "BEGINNER" ? "" : "BEGINNER")} rank="beginner" selected={rank === "BEGINNER"} />
                        <RankSearchButton onClick={() => handleRankChange(rank === "INTERMEDIATE" ? "" : "INTERMEDIATE")} rank="intermediate" selected={rank === "INTERMEDIATE"} />
                        <RankSearchButton onClick={() => handleRankChange(rank === "EXPERT" ? "" : "EXPERT")} rank="expert" selected={rank === "EXPERT"} />
                        <RankSearchButton onClick={() => handleRankChange(rank === "PRO" ? "" : "PRO")} rank="pro" selected={rank === "PRO"} />
                    </div>
        
                <div className="ranking-table-container">
                <table className="ranking-table">
                <thead>
                <tr>
                    <th>Position</th>
                    <th>Name</th>
                    <th>W/L</th>
                    <th>WinRate</th>
                    <th>Elo</th>
                    <th>Rank</th>
                </tr>
                </thead>
                <tbody>
                    {stats && stats
                        .sort((a, b) => b.elo - a.elo)
                        .filter(stat => !rank || stat.rank === rank)
                        .map((stat, idx) => (
                            <tr key={stat.player}>
                                <td>{idx + 1}</td>
                                <td>{stat.player}</td>
                                <td>{stat.victories}/{stat.defeats}</td>
                                <td>{stat.winRate}%</td>
                                <td>{stat.elo}</td>
                                <td>{stat.rank}</td>
                            </tr>
                        ))
                    }
                </tbody>
                </table>
                </div>
            
            </div>
            {isUserLogged() && <NavBar middleName="lobby" handleMiddleRequest={handleLobbyRequest} />}
        </div>
    )
}


function RankSearchButton({onClick, rank, selected}) {
    return (
        <div>
            <img className={selected ? "selected" : ""} src={`../../images/${rank}.png`} onClick={onClick}></img>
        </div>
    )
}
