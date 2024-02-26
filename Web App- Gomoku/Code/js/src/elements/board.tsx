import React, { ReactElement, useState } from "react"
import {Board} from "../types/boardTypes"
import {ProfileStat} from "../profile"
import '../../css/board.css'
import '../../images/white_player_piece.png'
import '../../images/black_player_piece.png'
import '../../images/board_background.png'
import '../../images/profile_wins.png'
import '../../images/profile_defeats.png'
import '../../images/profile_statics.png'
import '../../images/profile_matches.png'
import { PlayerRank } from '../lobby';

enum SquarePosition {
    CENTER,       // 0
    TOP_LEFT,     // 1
    TOP,          // 2
    TOP_RIGHT,    // 3
    LEFT,         // 4
    RIGHT,        // 5
    BOTTOM_LEFT,  // 6
    BOTTOM,       // 7
    BOTTOM_RIGHT  // 8
  }


function parseToSquarePosition(row: number, column: number, rows: number, columns: number): SquarePosition {
  return (
    (row === 0 && column === 0 && SquarePosition.TOP_LEFT) ||
    (row === 0 && column === columns - 1 && SquarePosition.TOP_RIGHT) ||
    (row === 0 && SquarePosition.TOP) ||
    (row === rows - 1 && column === 0 && SquarePosition.BOTTOM_LEFT) ||
    (row === rows - 1 && column === columns - 1 && SquarePosition.BOTTOM_RIGHT) ||
    (row === rows - 1 && SquarePosition.BOTTOM) ||
    (column === 0 && SquarePosition.LEFT) ||
    (column === columns - 1 && SquarePosition.RIGHT) ||
    SquarePosition.CENTER
  );
}

function squarePositionMapper(position: SquarePosition): { horizontal: React.ReactNode, vertical: React.ReactNode } {
  switch (position) {
    case SquarePosition.TOP_LEFT:
      return { horizontal: halfEndHorizontalLine(), vertical: halfEndVerticalLine() };
    case SquarePosition.TOP:
      return { horizontal: fullHorizontalLine(), vertical: halfEndVerticalLine() };
    case SquarePosition.TOP_RIGHT:
      return { horizontal: halfStartHorizontalLine(), vertical: halfEndVerticalLine() };
    case SquarePosition.LEFT:
      return { horizontal: halfEndHorizontalLine(), vertical: fullVerticalLine() };
    case SquarePosition.RIGHT:
      return { horizontal: halfStartHorizontalLine(), vertical: fullVerticalLine() };
    case SquarePosition.BOTTOM_LEFT:
      return { horizontal: halfEndHorizontalLine(), vertical: halfStartVerticalLine() };
    case SquarePosition.BOTTOM:
      return { horizontal: fullHorizontalLine(), vertical: halfStartVerticalLine() };
    case SquarePosition.BOTTOM_RIGHT:
      return { horizontal: halfStartHorizontalLine(), vertical: halfStartVerticalLine() };
    default:
      return { horizontal: fullHorizontalLine(), vertical: fullVerticalLine() };
  }
}

export function Board({
                          board,
                          playerID,
                          playerStats,
                          opponentStats,
                          game,
                          acceptSwap,
                          denySwap,
                          onClickCell
                      }
) {

  const [playerS, setPlayerStats] = useState(false)
  const [opponentS, setOpponentStats] = useState(false)
  let currentPlayerImage = ''
  let opponentPlayerImage = ''

    function playerCell(rowIndex: number, columnIndex: number): Number { 
        return board[rowIndex][columnIndex];
    }

    function getPlayerImageUrl(playerID: Number) {
      let imageUrl = null
      if (playerID == game.playerB) { /* Black */
      imageUrl = '../../images/black_player_piece.png';
      } else { /* White */
      imageUrl = '../../images/white_player_piece.png';
      }
  
      return imageUrl
    }
    
    function getPlayersImage() {
        if (playerID == game.playerB) { /* Black */
          currentPlayerImage = '../../images/black_player_piece.png';
          opponentPlayerImage = '../../images/white_player_piece.png';
        } else { /* White */
        opponentPlayerImage = '../../images/black_player_piece.png';
        currentPlayerImage = '../../images/white_player_piece.png';
        }
      }
      if(game !== undefined) getPlayersImage()
    return (
        <div>
          
          <div className='player-display'>
          {opponentStats && (
            <h2 onClick={() => setOpponentStats(!opponentS)}>
              Opponent: {opponentStats.player}
            </h2>
          )}

            {game && <img src={opponentPlayerImage} />}
          </div>
          {board &&
            board.map((row: any[], rowIndex: number) => (
              <div key={rowIndex} style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', textAlign: 'center' }}>
                {row.map((cell, columnIndex) => {

                  const position = parseToSquarePosition(rowIndex, columnIndex, row.length, row.length);
                  const { horizontal, vertical } = squarePositionMapper(position);
                  const playerID = playerCell(rowIndex, columnIndex)
      
                  return (
                    <div
                      key={columnIndex}
                      className="cell"
                      onClick={() => onClickCell(rowIndex, columnIndex)}
                    >
                      
                      
                      {horizontal}
                      {vertical}
                      <img src='../../images/board_background.png' style={{ objectFit: 'cover', width: '100%', height: '100%' }}/>
                      {playerID && <img src={getPlayerImageUrl(playerID)} style={{ position: 'absolute', objectFit: 'cover', width: '100%', height: '100%', top: 0, left: 0, }}/> }
                    </div>
                  );
                })}
              </div>
            ))}
          <ConditionalComponent game={game} acceptSwap={acceptSwap} denySwap={denySwap} playerID={playerID} />
          <div className='player-display'>
            {game && <img src={currentPlayerImage} />}
            {playerStats && <h2 onClick={() => setPlayerStats(!playerS)}>You: {playerStats.player}</h2> }
          </div>
          {opponentS && (
          <div className='clicked-opponent-stats'>
            <h3>{opponentStats.player}'s Stats</h3>
            <PlayerRank rank={opponentStats.rank} elo={opponentStats.elo} />
            <PlayerStat name="wins" stat={opponentStats.victories} />
            <PlayerStat name="defeats" stat={opponentStats.defeats} />
            <PlayerStat name="matches" stat={opponentStats.totalGames} />
            <PlayerStat name="statics" stat={opponentStats.winRate} />
          </div>
          )}
          {playerS && (
          <div className='clicked-player-stats'>
            <h3>{playerStats.player}'s Stats</h3>
            <PlayerRank rank={playerStats.rank} elo={playerStats.elo} />
            <PlayerStat name="wins" stat={playerStats.victories} />
            <PlayerStat name="defeats" stat={playerStats.defeats} />
            <PlayerStat name="matches" stat={playerStats.totalGames} />
            <PlayerStat name="statics" stat={playerStats.winRate} />
          </div>
          )}
        </div>
      );
}

export function PlayerStat({name, stat}) {
  return (
      <div className="player-stats">
          <img src={`../../images/profile_${name}.png`}></img>
          <h1>{stat}</h1>
      </div>
  )
}



function ConditionalComponent({game, acceptSwap, denySwap, playerID}) {
    if (game != undefined) {

        if (game.info && game.board.turn === playerID) {
            return (
                <div style={{display: 'flex'}}>
                    <button onClick={acceptSwap}>Accept</button>
                    <button onClick={denySwap}>Refuse</button>
                </div>
            );
        } else {
            return <div></div>;
        }
    }
}

export function parseGameToBoard(board: Board): any[][] | null {
    const boardMoves = board.moves
    let firstBoard = Array(15).fill(0).map(() => Array(15).fill(''));

    for (const cell in boardMoves) {
        const [row, column] = cell.substring(1, cell.length - 1).split('-').map(Number);
        const playerID = boardMoves[cell].player;
        firstBoard[row][column] = playerID.toString();
    }
    return firstBoard
}

// Função para criar uma linha horizontal completa na célula
function fullHorizontalLine(): React.ReactNode {
return (
    <div
        style={{
            position: 'absolute',
            left: 0,
            top: '50%',
            transform: 'translateY(-50%)',
            width: '100%',
            height: '1px',
            backgroundColor: '#999',
        }}
    />
)};

// Função para criar uma linha vertical completa na célula
function fullVerticalLine(): React.ReactNode {
                       return (
    <div
        style={{
            position: 'absolute',
            top: 0,
            left: '50%',
            transform: 'translateX(-50%)',
            width: '1px',
            height: '100%',
            backgroundColor: '#999',
        }}
    />
)};

// Função para criar uma linha horizontal do início ao centro da célula
function halfStartHorizontalLine(): React.ReactNode {
                              return (
    <div
        style={{
            position: 'absolute',
            left: 0,
            top: '50%',
            transform: 'translateY(-50%)',
            width: '50%',
            height: '1px',
            backgroundColor: '#999',
        }}
    />
)};

// Função para criar uma linha horizontal do centro ao fim da célula
function halfEndHorizontalLine(): React.ReactNode {
                            return (
    <div
        style={{
            position: 'absolute',
            right: 0,
            top: '50%',
            transform: 'translateY(-50%)',
            width: '50%',
            height: '1px',
            backgroundColor: '#999',
        }}
    />
)};

// Função para criar uma linha vertical do início ao centro da célula
function halfStartVerticalLine(): React.ReactNode {
                           return (
    <div
        style={{
            position: 'absolute',
            top: 0,
            left: '50%',
            transform: 'translateX(-50%)',
            width: '1px',
            height: '50%',
            backgroundColor: '#999',
        }}
    />
)};

// Função para criar uma linha vertical do centro ao fim da célula
function halfEndVerticalLine(): React.ReactNode {
                          return (
    <div
        style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%)',
            width: '1px',
            height: '50%',
            backgroundColor: '#999',
        }}
    />
)};
