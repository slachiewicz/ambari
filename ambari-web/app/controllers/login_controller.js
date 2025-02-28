/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var App = require('app');

App.LoginController = Em.Object.extend({

  name: 'loginController',

  loginName: '',
  password: '',

  errorMessage: '',

  isSubmitDisabled: false,

  submit: function (e) {
    this.set('errorMessage', '');
    this.set('isSubmitDisabled', true);
    //Hack to set username and password when user has not focussed on the element and is using previous values
    let username="";
    let password="";
    if(document&&document.getElementsByClassName("login-username")[0]){
      username=document.getElementsByClassName("login-username")[0].value;
    }
    if(document&&document.getElementsByClassName("login-password")[0]){
      password=document.getElementsByClassName("login-password")[0].value;
    }

    if(username){
      this.set('loginName',username);
    }
    if(password){
      this.set('password',password);
    }
    App.get('router').login();
  },

  postLogin: function (isConnected, isAuthenticated, responseText) {
    var errorMessage = "";
    if (!isConnected) {
      this.set('errorMessage', responseText || Em.I18n.t('login.error.bad.connection'));
    } else if (!isAuthenticated) {
      if (responseText === "User is disabled") {
        errorMessage = Em.I18n.t('login.error.disabled');
      } else if (responseText === "Authentication required" || Em.isNone(responseText)) {
        errorMessage = Em.I18n.t('login.error.bad.credentials');
      } else {
        errorMessage = responseText;
      }
      this.set('errorMessage', errorMessage);
    }
    this.set('isSubmitDisabled', false);
  }

});
