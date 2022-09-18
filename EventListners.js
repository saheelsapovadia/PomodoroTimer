import {DeviceEventEmitter} from 'react-native';

//Event listner
export const eventListnerColor = () => {
  DeviceEventEmitter.addListener('textColor', e => {
    console.log('Emitted event from native side ', e);
  });
};
