import React, {useState, createContext} from 'react'
import {RouterProvider} from "react-router-dom"
import {router} from "./router"
import {createRoot} from "react-dom/client"
import {ContextType} from "./types/ContextTypes"

export const AuthContext = createContext<ContextType>({loggedIn:false, userID:-1, gameID:-1, opponentID: -1})

function App(){
    const [loggedIn, setLoggedIn] = useState(false)
    const [userID, setUserID] = useState(-1)
    const [gameID, setGameID] = useState(-1)
    const [opponentID, setOpponentID] = useState(-1)
    return (
        <div>
            <AuthContext.Provider value={{loggedIn,setLoggedIn, userID, setUserID, gameID, setGameID, opponentID, setOpponentID}}>
                <RouterProvider router={router} />
            </AuthContext.Provider>
        </div>
    )
}

export function app(){
    const root = createRoot(document.getElementById("container"))
    root.render(<App />)
}