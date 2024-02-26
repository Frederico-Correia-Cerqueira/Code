export type SquareDetails = {
    player: number;
    type: 'W' | 'N';
};

export type Moves = Record<string, SquareDetails>;

export type Board = {
    moves: Moves;
    turn: number;
    type: string;
};

export type PlayerData = {
    playerName: string
    playerElo: number
}
