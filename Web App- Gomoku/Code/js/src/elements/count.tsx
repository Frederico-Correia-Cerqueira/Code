import { useState, useEffect } from 'react';

export function useTimer(initial: number): [number, string, () => void, () => void, () => void] {
  const [counter, setCounter] = useState(initial);
  const [isPaused, setIsPaused] = useState(false);

  const increment = () => {
    if (!isPaused) {
      setCounter((prevCounter) => prevCounter + 1);
    }
  };

  const decrement = () => {
    if (!isPaused) {
      setCounter((prevCounter) => prevCounter - 1);
    }
  };

  const reset = () => {
    setCounter(initial);
  };

  const pause = () => {
    setIsPaused((prevIsPaused) => !prevIsPaused);
  };

  const start = () => {
    setIsPaused(false);
  };

  const formatTime = (timeInSeconds) => {
    const hours = Math.floor(timeInSeconds / 3600);
    const minutes = Math.floor((timeInSeconds % 3600) / 60);
    const seconds = timeInSeconds % 60;

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  useEffect(() => {
    let intervalId;

    if (!isPaused) {
      intervalId = setInterval(() => {
        increment();
      }, 1000);
    }

    return () => clearInterval(intervalId);
  }, [isPaused]);

  const formattedTime = formatTime(counter);

  return [counter, formattedTime, start, reset, pause];
}