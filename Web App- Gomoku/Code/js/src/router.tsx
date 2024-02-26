import React from 'react'
import {createBrowserRouter} from "react-router-dom";
import {Login} from "./login";
import {Credits} from "./credits";
import {InitialScreen} from "./initialScreen";
import {CreateUser} from "./createUser";
import {Victory} from "./victory";
import {Defeat} from "./defeat";
import {Ranking} from "./ranking";
import {WaitingRoom} from "./waitingRoom";
import {Game} from "./game";
import {Lobby} from "./lobby";
import {SavedGames} from "./savedgames";
import {Profile} from "./profile";
import {Error} from "./error";

export const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <InitialScreen/>
        },
        {
            path: "error",
            element: <Error/>
        },
        {
            path: "login",
            element: <Login/>,
        },
        {
            path: "createUser",
            element: <CreateUser/>,
        },
        {
            path: "credits",
            element: <Credits/>,
        },
        {
            path: "ranking",
            element: <Ranking/>,
        },
        {
            path: "victory",
            element: <Victory/>,
        },
        {
            path: "defeat",
            element: <Defeat/>,
        },
        {
            path: "waitingRoom",
            element: <WaitingRoom/>,
        },
        {
            path: "game",
            element: <Game/>,
        },
        {
            path: "lobby",
            element: <Lobby/>,
        },
        {
            path: "savedGames",
            element: <SavedGames/>,
        },
        {
            path: "profile",
            element: <Profile/>,
        },
    ]
)

