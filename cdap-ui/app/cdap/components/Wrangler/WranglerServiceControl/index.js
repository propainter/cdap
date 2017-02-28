/*
 * Copyright © 2017 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import React, { Component } from 'react';
import MyWranglerApi from 'api/wrangler';
import {MyArtifactApi} from 'api/artifact';
import find from 'lodash/find';

export default class WranglerServiceControl extends Component {
  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      error: null,
      extendedMessage: null
    };

    this.enableWranglerService = this.enableWranglerService.bind(this);
  }

  componentWillUnmount() {
    if (this.servicePoll && this.servicePoll.dispose) {
      this.servicePoll.dispose();
    }
  }

  enableWranglerService() {
    this.setState({loading: true});
    console.log('Enabling Wrangler Service');
    /**
     *  1. Get Wrangler Service App
     *  2. If not found, create app
     *  3. Start Wrangler Service
     *  4. Poll until service starts, then reload page
     **/

    MyWranglerApi.getWranglerApp({ namespace: 'default' })
      .subscribe(() => {
        // Wrangler app already exist
        // Just start service
        console.log('Wrangler app already found. Starting service');
        this.startService();
      }, () => {
        // App does not exist
        // Go to create app
        console.log('Wrangler app not found. Creating app');
        this.createApp();
      });
  }

  createApp() {
    MyArtifactApi.list({ namespace: 'default' })
      .subscribe((res) => {
        console.log('Finding wrangler artifact');
        let artifact = find(res, { 'name': 'wrangler-service' });

        MyWranglerApi.createApp({ namespace: 'default' }, { artifact })
          .subscribe(() => {
            console.log('Wrangler app created. Starting service');
            this.startService();
          }, (err) => {
            this.setState({
              error: 'Failed to start service',
              extendedMessage: err.data || err
            });
          });
      });
  }

  startService() {
    MyWranglerApi.startService({ namespace: 'default' })
      .subscribe(() => {
        console.log('Service started. Polling for service status');
        this.pollServiceStatus();
      }, (err) => {
        this.setState({
          error: 'Failed to start service',
          extendedMessage: err.data || err
        });
      });
  }

  pollServiceStatus() {
    this.servicePoll = MyWranglerApi.pollServiceStatus({ namespace: 'default' })
      .subscribe((res) => {
        if (res.status === 'RUNNING') {
          console.log('Service is RUNNING. Going into wrangler');
          window.onbeforeunload = null;
          window.location.reload();
        }
        console.log('Current service status: ', res.status);
      }, (err) => {
        this.setState({
          error: 'Failed to get service status',
          extendedMessage: err.data || err
        });
      });
  }

  renderError() {
    if (!this.state.error) { return null; }

    return (
      <div>
        <h5 className="text-danger text-xs-center">
          {this.state.error}
        </h5>
        <p className="text-dangertext-xs-center">
          {this.state.extendedMessage}
        </p>
      </div>
    );
  }

  render() {
    return (
      <div className="wrangler-container error">
        <h3 className="text-danger text-xs-center">
          Error
        </h3>

        <h4 className="text-danger text-xs-center">
          Unable to connect to Data Preparation Service
        </h4>
        <br/>
        <div className="text-xs-center">
          <button
            className="btn btn-primary"
            onClick={this.enableWranglerService}
            disabled={this.state.loading}
          >
            {
              !this.state.loading ? 'Enable Data Preparation Service' : (
                <span>
                  <span className="fa fa-spin fa-spinner" /> Enabling...
                </span>
              )
            }
          </button>
        </div>

        <br/>

        {this.renderError()}
      </div>
    );
  }
}
