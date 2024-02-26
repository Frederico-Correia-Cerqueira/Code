import React, {useContext, useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {fetchGet, fetchPost, useFetchGet} from './fetch'
import {AuthContext} from "./App";
import {Board, parseGameToBoard} from "./elements/board";
import {Game, SwapInfo} from "./types/gameTypes"
import {PlayerData} from "./types/boardTypes";
import '../css/game.css'
import { NavBar, NavBarTop, NavBarTopElement, NavBarGameTurn } from './elements/navBar';
import {Stats} from "./types/informationTypes";

export function Game() {
    const [board, setBoard] = useState<any[][] | null>(null);
    const [game, setGame] = useState<Game>();
    const [turn, setTurn] = useState<string>();

    const navigate = useNavigate()
    const gameContext = useContext(AuthContext)
    const auth = useContext(AuthContext)
    const gameID = gameContext.gameID
    const playerID = auth.userID
    const opponentID = auth.opponentID

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    let playerWData: Stats = undefined
    let playerBData: Stats = undefined

    async function fetchGetGame() {
        const fetchPromise = fetchGet('api/games/' + gameID)
        let text = await fetchPromise;
        if (text != undefined) {
            const jsonData = JSON.parse(text);
            if (jsonData.properties != undefined) {
                setGame(jsonData.properties)
            }
        }
    }

    function fetchPlayersInfo() {
        const currentPlayer = useFetchGet({uri: `api/players/stats/${playerID}`})
        if (currentPlayer != undefined) {
            const jsonData = JSON.parse(currentPlayer)
            if (jsonData.properties) {
                playerWData = jsonData.properties
            }
        }
        const opponent = useFetchGet({uri: `api/players/stats/${opponentID}`})
        if (opponent != undefined) {
            const jsonData = JSON.parse(opponent)
            if (jsonData.properties) {
                playerBData = jsonData.properties
            }
        }
    }

    fetchPlayersInfo()

    useEffect(() => {
        const tid = setInterval(() => {
            if(game === undefined) fetchGetGame().catch(() =>
                navigate("/error")
            );
        
            else {
                if (playerID != game.board.turn) {
                    fetchGetGame().catch(() =>
                        navigate("/error")
                    );
                }
             }
            }, 3000)
            return () => clearInterval(tid)
        }, [game])

    useEffect(() => {

        if (game !== undefined) {
            const updatedBoard = parseGameToBoard(game.board);
            if (updatedBoard !== null) {
                setBoard(updatedBoard);
            }

            if (game.board.turn === playerID) setTurn("Your turn")
            else setTurn("Opponent's turn")

            const type = game.board.type
            if (type == "BOARD_WIN" && game.board.turn == playerID) {
                navigate("/victory")
            }
            if (type == "BOARD_WIN" && game.board.turn != playerID) {
                navigate("/defeat")
            }
        }
    }, [game]);


    function onClickCell(row: Number, column: Number) {
        if (game.board.turn == playerID) {
            const play = {
                playerID: playerID,
                l: row,
                c: column,
            };
            const uri = 'api/games/' + gameID
            const fetchPromise = fetchPost(uri, play);
            fetchPromise.then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text);
                    if (jsonData.properties != undefined) {
                        setGame(jsonData.properties)
                    }
                }
            })
        }
    }

    function afterSwap(body: SwapInfo) {
        if (game.variants == "SWAP_FIRST_MOVE") {
            const uri = 'api/games/' + gameID + '/swap_first_move'
            fetchPost(uri, body).catch(() =>
                navigate("/error")
            );
        } else {
            const uri = 'api/games/' + gameID + '/swap'
            fetchPost(uri, body).catch(() =>
                navigate("/error")
            );
        }
        fetchGetGame().catch(() =>
            navigate("/error")
        );
    }

    function acceptSwap() {
        const body: SwapInfo = {
            playerID: playerID,
            info: true
        }
        afterSwap(body)

    }

    function denySwap() {
        const body: SwapInfo = {
            playerID: playerID,
            info: false
        }
        afterSwap(body)
    }

    return (
        <div>
            <div className="navbar-top">
                    <h2 id="hide"> AAAAA </h2>
                    <h2>Game</h2>
                    <h2 id="hide"> AAAAA </h2>
            </div>
        <div className='game-container'>
            
            <Board
                board={board}
                playerID={playerID}
                playerStats={playerWData}
                opponentStats={playerBData}
                game={game}
                acceptSwap={acceptSwap}
                denySwap={denySwap}
                onClickCell={onClickCell}
            />
        </div>
        <NavBarGameTurn turn={turn} />
        </div>
    );
}

