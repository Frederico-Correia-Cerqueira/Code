import React, {useContext, useState} from 'react'
import {Link} from "react-router-dom";
import {AuthContext} from "./App";
import {fetchGet} from "./fetch";
import '../css/home.css';
import '../images/backgroundVideo.mp4';


export function InitialScreen() {

    const auth = useContext(AuthContext)
    const [uri, setUri] = useState<string>()

    fetchGet("/api/getAuthCookie")
        .then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text)
                    if (jsonData.properties != undefined) {
                        if (jsonData.properties.present) {
                            setUri("/lobby")
                            auth.setUserID(jsonData.properties.playerID)
                            auth.setLoggedIn(() => true)
                        } else {
                            setUri("/login")
                        }
                    }
                }
            }
        )

    return (
        <div className="background">
            <video className="back-video" autoPlay loop muted playsInline >
                <source src={"../images/backgroundVideo.mp4"} type="video/mp4"/>
            </video>
            <div  className="gomoku">
                <h1> GOMOKU </h1>
            </div>

            <div className="letsGetStarted">
                <p><Link to={uri}>Let's get started</Link></p>
            </div>
            <nav>
                <p className="buttonD"><Link to="/credits" >Credits</Link></p>
                <p className="buttonD"><Link to="/ranking">Ranking</Link></p>
            </nav>


        </div>
    )
}
