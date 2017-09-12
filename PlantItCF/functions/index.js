/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotificationWhenEventTimeChanged = functions.database.ref('/events/{eventId}/time').onUpdate(event => {
  const eventId = event.params.eventId;
  const time = event.data.val();
  console.log('time was:', time);


  if (!event.data.val()) {
    return console.log('Problem with updating dateTime in event: ', eventId);
  }
  console.log('Updating Time in event: ', eventId);

  const ref = 'eventsToUsers';
  admin.database().ref(ref).child(eventId).once('value',snapshot=>{
    if (snapshot.hasChildren()){
      const value = snapshot.val();
      console.log(value);
      var userIds = Object.keys(value.hosted);
      if (value.invited){
        userIds = userIds.concat(Object.keys(value.invited));
      }

      console.log(userIds);

      admin.database().ref('users').once('value', usersSnapshot=>{
        var tokens = [];
        for (let userId of userIds){
          if (usersSnapshot.hasChild(userId)){
            tokens.push(usersSnapshot.child(userId).val().token);
          } else {
            console.log('Child does not exist ' + userId);
          }
        }

        console.log(tokens);
        const titleSTR = 'Event time has changed to ' + time;
        const payload = {
              notification: {
                title: titleSTR,
                body: 'Check in PlanIt to be updated on the event!',
                //icon: user.photoURL
              }
            };

        let sendResult = admin.messaging().sendToDevice(tokens,payload);
        //console.log('Success: '+sendResult.successCount);
        //console.log('Failed '+sendResult.failureCount);

      });

    }
  });
  

  });


exports.sendNotificationWhenEventDateChanged = functions.database.ref('/events/{eventId}/date').onUpdate(event => {
  const eventId = event.params.eventId;
  const time = event.data.val();
  console.log('date is now:', time);


  if (!event.data.val()) {
    return console.log('Problem with updating dateTime in event: ', eventId);
  }
  console.log('Updating Time in event: ', eventId);

  const ref = 'eventsToUsers';
  admin.database().ref(ref).child(eventId).once('value',snapshot=>{
    if (snapshot.hasChildren()){
      const value = snapshot.val();
      console.log(value);
      var userIds = Object.keys(value.hosted);
      if (value.invited){
        userIds = userIds.concat(Object.keys(value.invited));
      }

      console.log(userIds);

      admin.database().ref('users').once('value', usersSnapshot=>{
        var tokens = [];
        for (let userId of userIds){
          if (usersSnapshot.hasChild(userId)){
            tokens.push(usersSnapshot.child(userId).val().token);
          } else {
            console.log('Child does not exist ' + userId);
          }
        }

        console.log(tokens);
        const titleSTR = 'Event date has changed to ' + time;
        const payload = {
              notification: {
                title: titleSTR,
                body: 'Check in PlanIt to be updated on the event!',
                //icon: user.photoURL
              }
            };

        let sendResult = admin.messaging().sendToDevice(tokens,payload);
        //console.log('Success: '+sendResult.successCount);
        //console.log('Failed '+sendResult.failureCount);

      });

    }
  });
  

  });


exports.sendNotificationWhenEventLocationChanged = functions.database.ref('/events/{eventId}/location').onUpdate(event => {
  const eventId = event.params.eventId;
  const time = event.data.val();
  console.log('location is now:', time);


  if (!event.data.val()) {
    return console.log('Problem with updating dateTime in event: ', eventId);
  }
  console.log('Updating Time in event: ', eventId);

  const ref = 'eventsToUsers';
  admin.database().ref(ref).child(eventId).once('value',snapshot=>{
    if (snapshot.hasChildren()){
      const value = snapshot.val();
      console.log(value);
      var userIds = Object.keys(value.hosted);
      if (value.invited){
        userIds = userIds.concat(Object.keys(value.invited));
      }

      console.log(userIds);

      admin.database().ref('users').once('value', usersSnapshot=>{
        var tokens = [];
        for (let userId of userIds){
          if (usersSnapshot.hasChild(userId)){
            tokens.push(usersSnapshot.child(userId).val().token);
          } else {
            console.log('Child does not exist ' + userId);
          }
        }

        console.log(tokens);
        const titleSTR = 'Event location has changed to ' + time;
        const payload = {
              notification: {
                title: titleSTR,
                body: 'Check in PlanIt to be updated on the event!',
                //icon: user.photoURL
              }
            };

        let sendResult = admin.messaging().sendToDevice(tokens,payload);
        //console.log('Success: '+sendResult.successCount);
        //console.log('Failed '+sendResult.failureCount);

      });

    }
  });
  

  });


exports.sendNotificationWhenAddedFriend = functions.database.ref('/eventsToUsers/{eventId}/invited/{userId}').onCreate(event => {
  const eventId = event.params.eventId;
  //console.log(event.data);
  // If un-follow we exit the function.
  if (!event.data.val()) {
    return console.log('Problem with updating dateTime in event: ', eventId);
  }
  console.log('Updating Time in event: ', eventId);

  const ref = 'eventsToUsers';
  admin.database().ref(ref).child(eventId).once('value',snapshot=>{
    if (snapshot.hasChildren()){
      const value = snapshot.val();
      console.log(value);
      var userIds = Object.keys(value.hosted);
      if (value.invited){
        userIds = userIds.concat(Object.keys(value.invited));
      }

      console.log(userIds);

      admin.database().ref('users').once('value', usersSnapshot=>{
        var tokens = [];
        for (let userId of userIds){
          if (usersSnapshot.hasChild(userId)){
            tokens.push(usersSnapshot.child(userId).val().token);
          } else {
            console.log('Child does not exist ' + userId);
          }
        }

        console.log(tokens);
        const payload = {
              notification: {
                title: 'New friend was added!',
                body: 'Check in PlanIt to see who is it!',
                //icon: user.photoURL
              }
            };

        let sendResult = admin.messaging().sendToDevice(tokens,payload);
        //console.log('Success: '+sendResult.successCount);
        //console.log('Failed '+sendResult.failureCount);

      });

    }
  });
  

  });