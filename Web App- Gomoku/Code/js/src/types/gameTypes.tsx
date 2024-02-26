import {Board} from "./boardTypes";

export type Game = {
    id: number;
    playerB: number;
    playerW: number;
    board: Board;
    variants: string,
    openingRules: string,
    info: boolean
}

export type SwapInfo = {
    playerID: number,
    info: boolean
}

export type GameInputModel = {
    playerID: Number,
    gameType: String,
    openingRules: String
}

export type savedGame = {
    id: number,
    game: number,
    name: string,
    description: string
}