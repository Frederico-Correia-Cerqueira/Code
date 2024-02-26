import React, {} from 'react'
import {GameResult} from "./gameResult";
import '../images/defeat_gif.gif'

export function Defeat() {
    return (<div><GameResult message={"DEFEATED... L"} imageUrl="../images/defeat_gif.gif"/></div>)
}


