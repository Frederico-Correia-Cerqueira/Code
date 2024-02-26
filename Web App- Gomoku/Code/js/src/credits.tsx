import React from 'react'
import {useFetchGet} from './fetch'
import {Link, useNavigate} from "react-router-dom"
import {isUserLogged, ValidateUser} from "./elements/validateUser";
import {NavBar, NavBarTop} from './elements/navBar';
import '../css/credits.css'
import '../images/pedros_img.png'
import '../images/catarinap_img.png'
import '../images/fredericoc_img.png'

export function Credits() {
    const creditsUri = "api/credits"
    const credits = useFetchGet({uri: creditsUri})
    const navigate = useNavigate();
    let developers = "Loading!"
    let version = "Waiting!"

    function handleLobbyRequest() {
        navigate("/lobby")
    }

    if (credits != undefined) {
        const jsonData = JSON.parse(credits)
        if (jsonData.properties) {
            version = jsonData.properties.version
            developers = jsonData.properties.developers.map((dev: { name: string; number: string }) => {
                return `${dev.name} - ${dev.number}`
            })
        }
        if (jsonData.title == "Internal Server Error") {
            navigate("/error")
        }
    }

    return (
        <div className="credits">
                 <div className="navbar-top">
                    {!isUserLogged() && <Link to='/'>Home</Link>}
                    {isUserLogged() && <h2 id="hide"> AAAA </h2>}
                    <h2 className='center'>Credits</h2>
                    <h2 id="hide"> AAAAA </h2>
                </div>

            <div className="content">
                <div className="containers">
                    <section className="container">
                        <div className="slider-wrapper">
                            <div className="slider">
                                <img id="slide-1" src="../images/pedros_img.png"/>
                                <p id="slide-2">{developers[2]}</p>

                            </div>
                            <div className="slider-nav">
                                <a href="#slide-1"></a>
                                <a href="#slide-2"></a>
                            </div>
                        </div>
                    </section>
                    <section className="container">
                        <div className="slider-wrapper">
                            <div className="slider">
                                <img id="slide-3" src="../images/catarinap_img.png"/>
                                <p id="slide-4">{developers[0]}</p>
                            </div>
                            <div className="slider-nav">
                                <a href="#slide-3"></a>
                                <a href="#slide-4"></a>
                            </div>
                        </div>
                    </section>
                    <section className="container">
                        <div className="slider-wrapper">
                            <div className="slider">
                                <img id="slide-5" src="../images/fredericoc_img.png"/>
                                <p id="slide-6">{developers[1]}</p>
                            </div>
                            <div className="slider-nav">
                                <a href="#slide-5"></a>
                                <a href="#slide-6"></a>
                            </div>
                        </div>
                    </section>
                </div>
                <p className="version">Version: {version}</p>

            </div>
            {isUserLogged() && <NavBar middleName="lobby" handleMiddleRequest={handleLobbyRequest}/>}
        </div>
    )
}

