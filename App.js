/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect, useState} from 'react';
// import type {Node} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  DeviceEventEmitter,
  Button,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';
import CustomModule from './CustomModule';
import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';
import BackgroundTimer from './BackgroundTimer/BackgroundTimer';
const {RNBackgroundTimer} = NativeModules;
const App = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [ncolor, setnColor] = useState(false);
  const [music, setMusic] = useState(false);
  const [pomoTimer, setPomoTimer] = useState(0);
  const [secondsLeft, setSecondsLeft] = useState(3601);
  const [timerOn, setTimerOn] = useState(false);
  useEffect(() => {
    if (timerOn) startTimer();
    else BackgroundTimer.stopBackgroundTimer();
    return () => {
      BackgroundTimer.stopBackgroundTimer();
    };
  }, [timerOn]);

  const startTimer = () => {
    BackgroundTimer.runBackgroundTimer(() => {
      setSecondsLeft(secs => {
        RNBackgroundTimer.notify(clockifyNotify(secs - 1), 'PomoTimer');
        if (secs > 0) return secs - 1;
        else return 0;
      });
    }, 1000);
  };

  useEffect(() => {
    if (secondsLeft === 0) BackgroundTimer.stopBackgroundTimer();
  }, [secondsLeft]);

  const clockifyNotify = secs => {
    let hours = Math.floor(secs / 60 / 60);
    let mins = Math.floor((secs / 60) % 60);
    let seconds = Math.floor(secs % 60);
    let displayHours = hours < 10 ? `0${hours}` : hours;
    let displayMins = mins < 10 ? `0${mins}` : mins;
    let displaySecs = seconds < 10 ? `0${seconds}` : seconds;
    return `${displayHours}hr ${displayMins}mins ${displaySecs}secs`;
  };

  const clockify = () => {
    let hours = Math.floor(secondsLeft / 60 / 60);
    let mins = Math.floor((secondsLeft / 60) % 60);
    let seconds = Math.floor(secondsLeft % 60);
    let displayHours = hours < 10 ? `0${hours}` : hours;
    let displayMins = mins < 10 ? `0${mins}` : mins;
    let displaySecs = seconds < 10 ? `0${seconds}` : seconds;
    return {
      displayHours,
      displayMins,
      displaySecs,
    };
  };

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
    this.eventListener = eventEmitter.addListener('pomoTimer', event => {
      console.log('pomoTimerCount', pomoTimer, event); // "someValue"
      setPomoTimer(event.val);
    });
    return () => {
      this.eventListener.remove(); //Removes the listener
    };
  }, [pomoTimer]);
  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  // CustomModule.bangNotification();
  return (
    <View style={styles.container}>
      <Text style={styles.time}>
        {clockify().displayHours} Hours {clockify().displayMins} Mins{' '}
        {clockify().displaySecs} Secs
      </Text>
      <Button
        title="Start/Stop"
        onPress={() => setTimerOn(timerOn => !timerOn)}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    backgroundColor: '#000',
  },
  time: {
    fontSize: 30,
    color: '#fff',
    marginBottom: 30,
    textAlign: 'center',
  },
});

export default App;
