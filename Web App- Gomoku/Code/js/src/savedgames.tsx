import React, {useState, useEffect, useContext, ChangeEvent} from 'react'
import {useFetchGet, fetchGet, fetchPost, fetchDelete} from './fetch'
import {Board, parseGameToBoard} from "./elements/board";
import {AuthContext} from "./App";
import {Link, useNavigate} from "react-router-dom";
import {Board as BoardType} from "./types/boardTypes"
import {savedGame} from "./types/gameTypes";
import {Stats} from "./types/informationTypes";
import {Game} from "./types/gameTypes"
import {NavBar, NavBarDelete, NavBarTop} from './elements/navBar';
import '../css/savedGames.css'
import '../images/replay_previous.png'
import '../images/replay_next.png'
import '../images/search_button_1.png'
import {isUserLogged} from "./elements/validateUser";

export function SavedGames() {
    const auth = useContext(AuthContext)
    const navigate = useNavigate()
    const content = useFetchGet({uri: '/api/games/savedgames/' + auth.userID});
    const [savedGames, setSavedGames] = useState(undefined);
    const [game, setGame] = useState<Game | null>(null);
    const [text, setText] = useState('')
    const [playerStats, setPlayerStats] = useState<Stats | null>(null);
    const [opponentStats, setOpponentStats] = useState<Stats | null>(null);
    const [activeTab, setActiveTab] = useState<'savedGames' | 'gameView'>('savedGames');
    const [gameToDelete, setGamesToDelete] = useState<Number[]>([])
    const [gameName, setGameName] = useState('')

    useEffect(() => {
        if (game !== null) {
            fetchGet(`api/players/stats/${getOpponentID()}`)
                .then(text => {
                        if (text != undefined) {
                            const jsonData = JSON.parse(text);
                            if (jsonData.properties != undefined) {
                                setOpponentStats(jsonData.properties);
                            }
                        }
                    }
                ).catch(() => navigate("/error"));

            fetchGet(`api/players/stats/${auth.userID}`)
                .then(text => {
                    if (text != undefined) {
                        const jsonData = JSON.parse(text);
                        if (jsonData.properties != undefined) {
                            setPlayerStats(jsonData.properties);
                        }
                    }
                }).catch(() => navigate("/error"));
        }
    }, [game]);

    useEffect(() => {
        if (!auth.loggedIn) {
            navigate('/');
        }
    }, [auth.loggedIn, navigate]);

    function handleSavedClick(gid: Number, gname: string) {
        setGameName(gname)
        fetchGet('api/games/' + gid)
            .then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text);
                    if (jsonData.properties != undefined) {
                        setGame(jsonData.properties);
                        setActiveTab('gameView')
                    }
                }
            })
            .catch(() => navigate("/error"));
    }

    function onChangeHandler(event: ChangeEvent<HTMLInputElement>) {
        setText(event.target.value)
    }

    function handleLobbyRequest() {
        navigate("/lobby")
    }

    function onClickHandler() {
        let saveInfo = {
            name: text
        }
        fetchPost('api/games/savedgame/' + auth.userID, saveInfo)
            .then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text);
                    if (jsonData.properties != undefined) {
                        setSavedGames(jsonData.properties);
                    }
                }
            }).catch(() => navigate("/error"));
    }

    function getOpponentID() {
        if (game == null) return null;
        if (game.playerW === auth.userID) return game.playerB 
        else return game.playerW
    }

    function handleMarkDelete(e, id) {
        e.stopPropagation();
        setGamesToDelete(prevGamesToDelete => [...prevGamesToDelete, id]);
    }

    function handleDesmarkDelete(e, id) {
        e.stopPropagation();
        setGamesToDelete(prevGamesToDelete => prevGamesToDelete.filter(gameId => gameId !== id));
    }

    function handleConfirmRequest() {
        const deletePromises = gameToDelete.map((id) => {
          return fetchDelete('api/games/' + id + '/deletegame/' + auth.userID, {});
        });

        Promise.all(deletePromises)
          .then(() => {
            setGamesToDelete([]);
            return fetchGet('api/games/savedgames/' + auth.userID);
          })
          .then((text) => {
            if (text !== undefined) {
              const jsonData = JSON.parse(text);
              if (jsonData.properties !== undefined) {
                setSavedGames(jsonData.properties);
              }
            }
          })
          .catch(() => {
            navigate("/error");
          });
      }

    function handleCancellRequest() {
        setGamesToDelete([])
    }

    if (content !== undefined && savedGames === undefined) {
        const jsonData = JSON.parse(content);
        if (jsonData.properties) {
            setSavedGames(jsonData.properties);
        }
    }

    if (savedGames == undefined) {
        return (
            <div>
                <h1>
                    Loading...
                </h1>
            </div>
        );
    } else {
        return (
            <div>
            
                <div className="navbar-top">
                    {activeTab === 'gameView' && <text onClick={() => setActiveTab('savedGames')}>Saved Games</text>}
                    {activeTab !== 'gameView' && <h2 id="hide"> AAAA </h2>}
                    <h2>{activeTab === 'gameView' ? 'Replay' : 'Saved Games'}</h2>
                    <h2 id="hide"> AAAAA </h2>
                </div>

                {savedGames && activeTab == 'savedGames' &&
                    <SavedGamesTable onChangeHandler={onChangeHandler} onClickHandler={onClickHandler}
                                     gameToDelete={gameToDelete} savedGames={savedGames}
                                     handleSavedClick={handleSavedClick} handleMarkDelete={handleMarkDelete}
                                     handleDesmarkDelete={handleDesmarkDelete}/>
                }
                {game && activeTab == 'gameView' &&
                    <BoardManage game={game} player={playerStats} opponent={opponentStats} gameName={gameName} />
                }
                {gameToDelete.length !== 0 &&
                    <NavBarDelete handleConfirmRequest={handleConfirmRequest}
                                  handleCancellRequest={handleCancellRequest}/>
                }
                {gameToDelete.length === 0 &&
                    <NavBar middleName="lobby" handleMiddleRequest={handleLobbyRequest}/>
                }
            </div>
        );
    }
}

function SavedGamesTable(
    {
        onChangeHandler,
        onClickHandler,
        gameToDelete,
        savedGames,
        handleSavedClick,
        handleMarkDelete,
        handleDesmarkDelete
    }) {
    return (
        <div className="savedgames-container">
            <input className='input-styled' onChange={onChangeHandler} type="text"
                   placeholder={'Search a saved game by name'}/>
            <img className='input-button' src='../../images/search_button_1.png' onClick={onClickHandler}></img>
            <div className="savedgames-table-container">
                <table className="savedgames-table">
                    <thead>
                    <tr>
                        <th>Position</th>
                        <th>GameID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody>
                    {savedGames && savedGames.games
                        .sort((a: Stats, b: Stats) => b.elo - a.elo)
                        .map((game: savedGame, idx: number) => (
                            <tr key={game.id} onClick={() => handleSavedClick(game.game, game.name)}>
                                <td>{idx + 1}</td>
                                <td>{game.game}</td>
                                <td>{game.name}</td>
                                <td>{game.description}</td>
                                <td>
                                    {gameToDelete.indexOf(game.id) === -1 &&
                                        <button onClick={(e) => {
                                            handleMarkDelete(e, game.id)
                                        }}>Delete</button>
                                    }

                                    {gameToDelete.indexOf(game.id) !== -1 &&
                                        <button onClick={(e) => {
                                            handleDesmarkDelete(e, game.id)
                                        }}>Cancell</button>
                                    }

                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

function BoardManage({game, player, opponent, gameName}: { game: Game, player: Stats, opponent: Stats, gameName: string }) {
    const [idx, setIdx] = useState(0);

    const slicedMoves = Object.fromEntries(
        Object.entries(game.board.moves).slice(0, idx)
    );

    const [board, setBoard] = useState(
        parseGameToBoard({
            moves: slicedMoves,
            turn: game.board.turn,
            type: game.board.type
        })
    )

    useEffect(() => {
        setBoard(
            parseGameToBoard({
                moves: slicedMoves,
                turn: game.board.turn,
                type: game.board.type
            })
        )
    }, [idx]);

    function onPrevClick() {
        if (idx >= 1) {
            setIdx(idx - 1);
        }
    }

    function onNextClick() {
        if (idx < Object.keys(game.board.moves).length) {
            setIdx(idx + 1);
        }
    }

    if (player != null && opponent != null) {
        return (

            <div className="board-container">
                <Board
                    board={board}
                    playerID={null}
                    playerStats={player}
                    opponentStats={opponent}
                    game={game}
                    acceptSwap={null}
                    denySwap={null}
                    onClickCell={() => {
                    }}
                />
                <div className='board-manage-controllers'>
                    <img src='../images/replay_previous.png' onClick={onPrevClick}></img>
                    <img src='../images/replay_next.png' onClick={onNextClick}></img>
                </div>

            </div>
        );
    } else {
        return (
            <div>
                <p> loading...</p>
            </div>
        )
    }
}
