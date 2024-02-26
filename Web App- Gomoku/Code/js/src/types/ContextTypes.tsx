export type ContextType = {
    loggedIn : boolean,
    setLoggedIn? : (f:(v:boolean)=>boolean) => void,
    userID : number,
    setUserID? : (f:number) => void,
    gameID: number,
    setGameID? : (f:number) => void
    opponentID: number,
    setOpponentID? : (f:number) => void
}