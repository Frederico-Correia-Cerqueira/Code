import React, { ReactElement } from "react";
import {useNavigate} from "react-router-dom";
import '../../images/ranking_button_1.png'
import '../../images/profile_button_1.png'
import '../../images/credits_button_1.png'
import '../../images/save_button_1.png'
import '../../images/play_button_1.png'
import '../../images/lobby_button_1.png'
import '../../images/deleteConfirm.png'
import '../../images/deleteCancell.png'

export type NavBarTopElement = {
    react: React.ReactElement;
    position: string
};

export function NavBar({middleName, handleMiddleRequest}) {
    const navigate = useNavigate()

    function handleRankingRequest() {
        navigate("/ranking")
    }

    function handleSavedGamesRequest() {
        navigate("/savedGames")
    }

    function handleCreditsRequest() {
        navigate("/credits")
    }

    function handleProfileRequest() {
        navigate("/profile")
    }

    return (
        <div className="navbar">
            <img src='../../images/ranking_button_1.png' onClick={handleRankingRequest}></img>
            <img src='../../images/save_button_1.png' onClick={handleSavedGamesRequest}></img>
            <img className="middle" src={`../../images/${middleName}_button_1.png`} onClick={handleMiddleRequest}></img>
            <img src='../../images/credits_button_1.png' onClick={handleCreditsRequest}></img>
            <img src='../../images/profile_button_1.png' onClick={handleProfileRequest}></img>
        </div>
    )
}

export interface NavBarItemProps {
    position?: string;
    children: React.ReactNode;
  }
  
  export function NavBarTop({ elements }: { elements: NavBarItemProps[] }) {
    return (
      <div className="navbar-top">
        {elements.map((item, index) => (
          <div key={index} className={`navbar-item ${item.position || 'center'}`}>
            {item.children}
          </div>
        ))}
      </div>
    );
  }
  


export function NavBarDelete({handleConfirmRequest, handleCancellRequest}) {

    return (
        <div className="navbar">
            <img className="middle" src={`../../images/deleteConfirm.png`} onClick={handleConfirmRequest}></img>
            <img className="middle" src={`../../images/deleteCancell.png`} onClick={handleCancellRequest}></img>
        </div>
    )
}

export function NavBarGameTurn({turn}) {
    return (
        <div className="navbar-bottom">
            <h3>{turn}</h3>
        </div>
    )
}