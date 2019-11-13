import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import UiRow from './UI/UiRow';
import Row from './Row';
import Block from './Block';
import CurrentPiece from './CurrentPiece';
import { PiecePosition, BoardArray, ScoreList } from './Types';
import Board from './Board';
import Score from './UI/Score';


const App: React.FC = () => {
  const boardArray: string[][] = useSelector((state: any) => state.board);
  const currentPosition: PiecePosition = useSelector((state: any) => state.currentPiece);
  const score: number = useSelector((state: any) => state.score);
  const gravityActive: number = useSelector((state: any) => state.gravityActive)
  const newPiece: boolean = useSelector((state: any) => state.newPiece)
  const gameOver: boolean = useSelector((state: any) => state.gameOver)
  const gameStarted: boolean = useSelector((state: any) => state.gameStarted)
  const highScores: ScoreList = useSelector((state: any) => state.highScores)

  const dispatch = useDispatch();

  if(highScores === undefined){
    console.log("---FETCHING---")
    // fetch('http://localhost:4567/getTopTen')
    fetch('/getTopTen')
    .then(res => res.json())
    // .then(res => JSON.parse(res))
    .then(res => dispatch({
      type: "SET_SCORES",
      scores: res
    }))
  }
 

  let boardCopy = [...boardArray];

  const currentPiece: CurrentPiece = new CurrentPiece(boardArray, currentPosition);
  const board: Board = new Board(boardArray)
  

  const fullRows: number[] = newPiece ? board.getFullRows() : [];
  const pauseForAnimation: boolean = new Boolean(fullRows.length).valueOf();
  
  if(fullRows.length){
    const nextScore: number = score + Math.pow(2, fullRows.length);
    let nextBoard = currentPiece.removeFromBoard(boardCopy);
    nextBoard = board.removeFullRows(fullRows);
    nextBoard = currentPiece.addToBoard(nextBoard);
    setTimeout(()=>{
      dispatch({
        type: "REMOVE_FULL_ROWS",
        board: nextBoard,
        score: nextScore
      })
    }, 400)
  }

  const trackPiece = (): void => {
    if(!gameStarted) return;

    if (!currentPiece.lockCurrentPosition) {
      boardCopy = currentPiece.removeFromBoard(boardCopy);
    }
    boardCopy = currentPiece.addNextToBoard(boardCopy);
    console.log('UPDATE')
    
    dispatch({ 
      type: 'UPDATE',
      board: boardCopy,
      currentPiecePosition: currentPiece.nextPosition,
      newPiece: currentPiece.newPiece,
      gravityActive: true,
      score: score,
      gameStarted: true,
      highScores: highScores
    })
  }

    document.addEventListener('keydown', handler)

  function handler(e: KeyboardEvent) {
    if (["ArrowLeft", "ArrowRight", "ArrowDown"].includes(e.key)) {
      if (currentPiece.move(e.key) && !pauseForAnimation) {
        document.removeEventListener('keydown', handler)
        if (!gameIsOver()){
          trackPiece()
        } else {
          endGame()
        }
      }
    }
    if (e.key === "ArrowUp") {
      if(currentPiece.rotate() && !pauseForAnimation){
        document.removeEventListener('keydown', handler)
        trackPiece();
      }
    }
  }

  function gameIsOver(): boolean{
    return !currentPiece.isValidPosition(currentPiece.nextPosition);
  }

  function endGame(): void{
    dispatch({
      type: "GAME_OVER"
    })
  }

  function startGame(): void{
    console.log('start game');
    dispatch({
      type:"START_GAME"
    })
  }

  const fall = (): void => {
      let event = new KeyboardEvent('keydown', {key:"ArrowDown"})
      document.dispatchEvent(event);
  }

  if(gameStarted && !gravityActive){
    setInterval(fall, 500)
  } 
  
  return (
    <div style={{
      display: 'flex',
      paddingTop: '30px',
      justifyContent: 'center'
    }}>
    <div style={{ 
        display: 'flex',
        flexDirection: 'column', 
        alignItems: 'center',
        justifyContent: 'center' }}>
      {
        boardArray.map((row, index) => 
          <UiRow 
            key={index} 
            row={row} 
            rowId={index} 
            isFull={fullRows.includes(index)}
            gameOver={gameOver}
            />)
      }
    </div>
    <Score
    score = {score}
    gameOver = {gameOver}
    startGame = {startGame}
    gameStarted = {gameStarted}
    highScores = {highScores}
  />
  </div>
  );
}

export default App;