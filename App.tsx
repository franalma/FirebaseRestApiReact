import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { Button, StyleSheet, Text, TextInput, View } from 'react-native';

import { NativeModules } from 'react-native';
var firebaseApi = NativeModules.FirebaseRestApi


const initFirebase = function () {
  var apiKey = "YOUR_API_KEY";
  firebaseApi.init("your_database", apiKey);
  console.log
}

const doLogin = function () {
  firebaseApi.doLogin("user", "password", true, onLoginSuccess, onLoginFailure);
}

const getData = function () {
  firebaseApi.get("mascotas/masc2", onGetDataResponse);
}

const setData = function () {
  firebaseApi.set("mascotas/masc2", '{ "test4": "value4" }');
}


const onGetDataResponse = function (data: any) {
  console.log("--responses: " + data);
}

const onLoginSuccess = function (data: any) {
  console.log("---->" + data);
}

const onLoginFailure = function () {
  console.log("---Failure");
}

const TestApp = () => {
  return (
    <View style={styles.container}>
      <Button
        onPress={() => initFirebase()}
        title="Init"
      />

      <Button
        onPress={() => doLogin()}
        title="Sign In"
      />

      <Button
        onPress={() => getData()}
        title="Get data"
      />

      <Button
        onPress={() => setData()}
        title="Set data"
      />

    </View>
  );
}


export default TestApp;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
